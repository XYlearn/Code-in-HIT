`include "defines.vh"

module ALU(
    input[`QWORD] aluA,
    input[`QWORD] aluB,
    input[3:0] alufun,
    input set_cc,
    output [`QWORD] valE,
    output[2:0] cc
);

assign cc[`ZF] = (valE == 64'B0);
assign cc[`SF] = valE[63];
assign cc[`OF] = (aluA[63]==aluB[63] && aluA[63] != valE[63]);

// calculate valE
assign valE = aluA + aluB;
/*
always @ *
begin
    case(alufun)
    `FADDQ:
        valE = aluA + aluB;
    endcase
end
*/
endmodule
