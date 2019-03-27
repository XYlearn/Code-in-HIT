`timescale 1ns / 1ps
//////////////////////////////////////////////////////////////////////////////////
// Company: 
// Engineer: 
// 
// Create Date: 2017/06/19 08:33:18
// Design Name: 
// Module Name: lock
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


module lock(
    input [3:0] D,
    input q,
    input clk,
    output warn
    );
    wire warning;
    wire reset;
    cyto pro(D,q,clk,reset,warning,green);
    warning proc(clk,warning,warn);
    
endmodule
