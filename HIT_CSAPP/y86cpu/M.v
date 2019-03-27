`include "defines.vh"

module M(
    input[3:0] icode,
    input[`QWORD] valA,
    input[`QWORD] valE,
    input[`QWORD] valP,
    input imem_err,
    input dmem_err,
    input instr_valid,
    output reg[1:0] stat,
    output reg mem_read,
    output reg mem_write,
    output reg[`QWORD] mem_data,
    output reg[`QWORD] mem_addr
);

// set mem_read
always @ *
begin
    case(icode)
    `IMRMOVQ, `IPOPQ, `IRET:
        mem_read <= 1'B1;
    default:
        mem_read <= 1'B0;
    endcase
end

// select mem_addr
always @ *
begin
    case(icode)
    `IRMMOVQ, `IPUSHQ, `ICALL, `IMRMOVQ:
        mem_addr <= valE;
    `IPOPQ, `IRET:
        mem_addr <= valA;
    endcase
end

// select mem_write
always @ *
begin
    case(icode)
    `IRMMOVQ, `IPUSHQ, `ICALL:
        mem_write <= 1'B1;
    default:
        mem_write <= 1'B0;
    endcase
end

// set mem_data
always @ *
begin
    case(icode)
    `IRMMOVQ, `IPUSHQ:
        mem_data <= valA;
    `ICALL:
        mem_data <= valP;
    endcase
end

// set stat
always @ *
begin
    if(imem_err || dmem_err)
        stat <= `SADR;
    else if(!instr_valid)
        stat <= `SINS;
    else if(icode == `IHALT)
        stat <= `SHLT;
    else
        stat <= `SAOK;
end

endmodule
