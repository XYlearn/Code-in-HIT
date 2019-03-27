`include "defines.vh"

module Y86_64(
    //input clk
);
reg clk;

wire[79:0] bytes;

wire[`QWORD] valM;

wire[`QWORD] valA;
wire[`QWORD] valB;
wire[`QWORD] valE;
wire[2:0] w_cc;

reg Cnd;
wire[3:0] srcA;
wire[3:0] srcB; 
wire[3:0] dstE;
wire[3:0] dstM;

wire imem_err;
wire instr_valid;
wire need_regids;
wire need_valC;
wire[3:0] icode;
wire[3:0] ifun;
wire[3:0] rA;
wire[3:0] rB;
wire[`QWORD] valC;

wire[1:0] stat;
wire[`QWORD] mem_data;
wire[`QWORD] mem_addr;

wire[`QWORD] valP;

//######################### Data Memory ###########################

wire dmem_err;

dmem data_mem(
.clk(clk),
.mem_addr(mem_addr),
.mem_data(mem_data),
.mem_write(mem_write),
.read_addr(srcB),
.read_data(valM),
.dmem_err(dmem_err)
);


//######################### Registerfiles ##########################
reg[`QWORD] regs[15:0];
reg[2:0] cc;
reg[`QWORD] pc;

assign valA = regs[srcA];
assign valB = regs[srcB];

// update cc
always @ (posedge clk)
begin
    if(set_cc)
        cc <= w_cc;
end

//##################### INSTUCTION_MEMORY ####################
ins_mem imem(
.addr(pc),
.bytes(bytes),
.imem_err(imem_err)
);

//################ Update Register and PC ######################

// set valP
assign valP = pc + 1 + need_regids + need_valC * 8;

always @ (posedge clk)
begin
    // write registers
    if(dstE != `RNONE)
        regs[dstE] <= valE;
    if(dstM != `RNONE)
        regs[dstM] <= valM;
    
    // update pc
    if(stat == `SHLT)
        pc <= pc;
    else if(icode == `IJXX && Cnd)
        pc <= valC;
    else
    begin
    case(icode)
    `ICALL:
        pc <= valC;
    `IRET:
        pc <= valM;
    default:
        pc <= valP;
    endcase
    end
end

//################ Fetch Stage ###################

F fetch(
.ins(bytes),
.imem_err(imem_err),
.instr_valid(instr_valid),
.need_regids(need_regids),
.need_valC(need_valC),
.rA(rA),
.rB(rB),
.valC(valC),
.icode(icode),
.ifun(ifun)
);

//################ Decode Stage #####################

// calculate Cnd
always @ *
begin
    if(icode == `ICMOVXX)
    begin
        case(ifun)
        `FRRMOVQ:   // cnd must be true
            Cnd <= 1'B1;
        `FCMOVLEQ:  // <=
            Cnd <= cc[`SF] | cc[`ZF];
        `FCMOVLQ:
            Cnd <= cc[`SF];
        `FCMOVEQ:
            Cnd <= cc[`ZF];
        `FCMOVNEQ:
            Cnd <= ~cc[`ZF];
        `FCMOVGEQ:
            Cnd <= ~cc[`SF];
        `FCMOVGQ:
            Cnd <= ~(cc[`SF] | cc[`ZF]);
        endcase
    end
    else if(icode == `IJXX)
    begin
        case(ifun)
        `FJMP:
            Cnd <= 1'B1;
        `FJLE:
            Cnd <= cc[`SF] | cc[`ZF];
        `FJL:
            Cnd <= cc[`SF];
        `FJE:
            Cnd <= cc[`ZF];
        `FJNE:
            Cnd <= ~cc[`ZF];
        `FJGE:
            Cnd <= ~cc[`SF] | cc[`ZF];
        `FJG:
            Cnd <= (~cc[`SF]) & (~cc[`ZF]);
        endcase
    end
end

D decode(
.icode(icode),
.rA(rA),
.rB(rB),
.Cnd(Cnd),
.srcA(srcA),
.srcB(srcB),
.dstE(dstE),
.dstM(dstM)
);

//################ Execute Stage ######################
E execute(
.icode(icode),
.ifun(ifun),
.valA(valA),
.valB(valB),
.valC(valC),
.valE(valE),
.cc(w_cc),
.set_cc(set_cc)
);

//################ Memory Stage #######################

M memorize(
.icode(icode),
.valA(valA),
.valE(valE),
.valP(valP),
.imem_err(imem_err),
.dmem_err(dmem_err),
.instr_valid(instr_valid),
.stat(stat),
.mem_read(mem_read),
.mem_write(mem_write),
.mem_data(mem_data),
.mem_addr(mem_addr)
);

initial
begin
    regs[`RRAX] = 64'B0;
    regs[`RRBX] = 64'B0;
    regs[`RRCX] = 64'B0;
    regs[`RRDX] = 64'B0;
    regs[`RRSI] = 64'B0;
    regs[`RRDI] = 64'B0;
    regs[`RRSP] = `DATA_MEM_SIZE;
    regs[`RRBP] = 64'B0;
    regs[`RR8] = 64'B0;
    regs[`RR9] = 64'B0;
    regs[`RR10] = 64'B0;
    regs[`RR11] = 64'B0;
    regs[`RR12] = 64'B0;
    regs[`RR13] = 64'B0;
    regs[`RR14] = 64'B0;
    regs[`RNONE] = 64'B0;
    pc <= 64'B0;
    clk <= 1'B0;
end
always #10 clk = ~clk;


endmodule
