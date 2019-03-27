`timescale 1ns / 1ps
//////////////////////////////////////////////////////////////////////////////////
// Company: 
// Engineer: 
// 
// Create Date: 2017/12/13 10:36:59
// Design Name: 
// Module Name: regfiles
// Project Name: 
// Target Devices: 
// Tool Versions: 
// Description: 
// 
// Dependencies: 
// 
// Revision:
// Revision 0.01 - File Created
// Additional Comments:
// 
//////////////////////////////////////////////////////////////////////////////////
`include "defines.vh"

module regfiles(
    input clk,
    input[3:0] rA,
    input[3:0] rB,
    output reg[`QWORD] valA,
    output reg[`QWORD] valB,
    input[3:0] dstE,
    input[3:0] dstM,
    input[`QWORD] valE,
    input[`QWORD] valM,
    output reg[`QWORD] regs[15:0],
    
    input[2:0] new_cc,
    input write_cc,
    output reg[2:0] cc
);

always @ (posedge clk)
begin
    if(dstE != `RNONE)
        regs[dstE] <= valE;
    if(dstM != `RNONE)
        regs[dstM] <= valM;
    if(rA != `RNONE)
        valA <= regs[rA];
    if(rB != `RNONE)
        valB <= regs[rB];
end

always @ (posedge clk)
begin
    if(write_cc)
        cc <= new_cc;
end

initial
fork
    regs[0] <= 64'B0;
    regs[1] <= 64'B0;
    regs[2] <= 64'B0;
    regs[3] <= 64'B0;
    regs[4] <= 64'B0;
    regs[5] <= 64'B0;
    regs[6] <= 64'B0;
    regs[7] <= 64'B0;
    regs[8] <= 64'B0;
    regs[9] <= 64'B0;
    regs[10] <= 64'B0;
    regs[11] <= 64'B0;
    regs[12] <= 64'B0;
    regs[13] <= 64'B0;
    regs[14] <= 64'B0;
    regs[15] <= 64'B0;
join
    
endmodule
