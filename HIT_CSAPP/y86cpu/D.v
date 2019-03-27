`include "defines.vh"

module D(
    input[3:0] icode,
    input[3:0] rA,
    input[3:0] rB,
    input Cnd,
    output[3:0] srcA,
    output[3:0] srcB,
    output[3:0] dstE,
    output[3:0] dstM
);

// set srcA
assign srcA = ((icode == `IRRMOVQ) | (icode == `IRMMOVQ) | (icode == `IOPQ) | (icode == `IPUSHQ)) ? rA :
            (((icode == `IPOPQ) | (icode == `IRET)) ? `RRSP : `RNONE);
            /*
always @ *
begin
    case(icode)
    `IRRMOVQ, `IRMMOVQ, `IOPQ, `IPUSHQ:
        srcA <= rA;
    `IPOPQ, `IRET:
        srcA <= `RRSP;
    default:
        srcA <= `RNONE;
    endcase
end
*/

// set srcB
assign srcB = ((icode ==`IOPQ) | (icode == `IRMMOVQ) | (icode == `IMRMOVQ)) ? rB :
            (((icode == `IPUSHQ) | (icode == `IPOPQ) | (icode == `ICALL) | (icode == `IRET)) ? `RRSP :
            `RNONE);
/*
always @ *
begin
    case(icode)
    `IOPQ, `IRMMOVQ, `IMRMOVQ:
        srcB <= rB;
    `IPUSHQ, `IPOPQ, `ICALL, `IRET:
        srcB <= `RRSP;
    default:
        srcB <= `RNONE;
    endcase
end
*/

// set dstE
assign dstE = ((icode == `ICMOVXX && Cnd) | (icode == `IIRMOVQ) | (icode == `IOPQ)) ? rB :
            (((icode == `IPUSHQ) | (icode == `IPOPQ) | (icode == `ICALL) | (icode == `IRET)) ? `RRSP :
                `RNONE);
/*always @ *
begin
    if(icode == `ICMOVXX && Cnd)
        dstE <= rB;
    else
    begin
    case(icode)
    `IIRMOVQ, `IOPQ:
        dstE <= rB;
    `IPUSHQ, `IPOPQ, `ICALL, `IRET:
        dstE <= `RRSP;
    default:
        dstE <= `RNONE;
    endcase
    end
end
*/

// set dstM
assign dstM = ((icode == `IMRMOVQ) | (icode == `IPOPQ)) ? rA : `RNONE;
/*
always @ *
begin
    case(icode)
    `IMRMOVQ, `IPOPQ:
        dstM <= rA;
    default:
        dstM <= `RNONE;
    endcase
end
*/
endmodule
