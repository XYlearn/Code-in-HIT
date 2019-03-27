`include "defines.vh"

module E(
    input[3:0] icode,
    input[3:0] ifun,
    input[`QWORD] valA,
    input[`QWORD] valB,
    input[`QWORD] valC,
    output[`QWORD] valE,
    output[2:0] cc,
    output set_cc
);

wire[`QWORD] aluA;
wire[`QWORD] aluB;
wire[3:0] alufun;
wire[`QWORD] w_aluA;
wire[`QWORD] w_aluB;
wire[3:0] w_alufun;
assign w_aluA = aluA;
assign w_aluB = aluB;
assign w_alufun = alufun;

ALU alu(
    .aluA(w_aluA),
    .aluB(w_aluB),
    .alufun(w_alufun),
    .set_cc(set_cc),
    .valE(valE),
    .cc(cc)
);

// set aluA
assign aluA = ((icode == `IRRMOVQ) | (icode == `IOPQ)) ? valA :
              (((icode == `IIRMOVQ) | (icode == `IRMMOVQ) | (icode == `IMRMOVQ)) ? valC :
              (((icode == `ICALL) | (icode == `IPUSHQ)) ? -8 : 
              (((icode == `IRET) | (icode == `IPOPQ)) ? 8 : 0)));
/*
always @ *
begin
    case(icode)
    `IRRMOVQ, `IOPQ:
        aluA <= valA;
    `IIRMOVQ, `IRMMOVQ, `IMRMOVQ:
        aluA <= valC;
    `ICALL, `IPUSHQ:
        aluA <= -8;
    `IRET, `IPOPQ:
        aluA <= 8;
    default:
        aluA <= 0;
    endcase
end
*/
// set aluB
assign aluB = ((icode == `IRMMOVQ) | (icode == `IMRMOVQ) | (icode == `IOPQ) | (icode == `ICALL) |
             (icode == `IPUSHQ) | (icode == `IRET) | (icode == `IPOPQ)) ? valB :
             (((icode == `IRRMOVQ) | (icode == `IIRMOVQ)) ? 0 : 0);
/*
always @ *
begin
    case(icode)
    `IRMMOVQ, `IMRMOVQ, `IOPQ, `ICALL,
    `IPUSHQ, `IRET, `IPOPQ:
        aluB <= valB;
    `IRRMOVQ, `IIRMOVQ:
        aluB <= 0;
    endcase
end
*/

// set alufun
assign alufun = icode == `IOPQ ? ifun : `FADDQ;
/*
always @ *
begin 
    case(icode)
    `IOPQ:
        alufun <= ifun;
    default:
        alufun <= `FADDQ;
    endcase
end
*/
// set set_cc
assign set_cc = (icode == `IOPQ); 

endmodule
