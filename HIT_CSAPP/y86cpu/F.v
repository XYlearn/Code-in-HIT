`include "defines.vh"

module F(
    input[79:0] ins,
    input imem_err,
    output[3:0] rA,
    output[3:0] rB,
    output[`QWORD] valC,
    output instr_valid,
    output need_regids,
    output need_valC,
    output [3:0] icode,
    output [3:0] ifun
);

assign rA = need_regids ? ins[`RA] : `RNONE;
assign rB = need_regids ? ins[`RB] : `RNONE;
assign valC = need_regids ? ins[`VALC] : ins[`NO_REGIDS_VALC];
assign icode = imem_err ? `INOP : ins[`ICODE];
assign ifun = imem_err ? `FNONE : ins[`IFUN];

// set instr_valid
assign instr_valid = (icode == `INOP) | (icode == `IHALT) | (icode == `IRRMOVQ) | 
    (icode == `IIRMOVQ) | (icode == `IRMMOVQ) | (icode == `IMRMOVQ) |
    (icode == `IOPQ) | (icode == `IJXX) | (icode == `ICALL) | (icode == `IRET) |
    (icode == `IPUSHQ) | (icode == `IPOPQ);

// set need_regids
assign need_regids = (icode == `IRRMOVQ) | (icode == `IOPQ) | (icode == `IPUSHQ) | 
    (icode == `IPOPQ) | (icode == `IIRMOVQ) | (icode == `IRMMOVQ) | (icode == `IMRMOVQ);

// set need_valC
assign need_valC = (icode == `IIRMOVQ) | (icode == `IRMMOVQ) | (icode == `IMRMOVQ) |
            (icode == `IJXX) | (icode == `ICALL);

endmodule
