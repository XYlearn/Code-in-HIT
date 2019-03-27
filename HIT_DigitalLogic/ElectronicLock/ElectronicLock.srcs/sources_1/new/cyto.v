`timescale 1ns / 1ps
//////////////////////////////////////////////////////////////////////////////////
// Company: 
// Engineer: 
// 
// Create Date: 2017/06/18 16:05:10
// Design Name: 
// Module Name: cyto
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


module cyto(
    input [3:0] D,
    input q,
    input clk,
    input reset,
    output warning,
    output green
    );
    reg warning=0;
    reg green=0;
    reg [3:0] A = 4'b0;
    reg [3:0] B = 4'b0;
    reg [3:0] C = 4'b0;
    reg [3:0] E = 4'b0;
    reg [2:0] delay;
    always @(posedge clk)
    delay <= {delay[1:0],q};
    wire pos_signal = delay[1]&&(~delay[2]);
    always @(posedge clk)
    begin
    if(reset==1)
    begin
    A = 0;
    B = 0;
    C = 0;
    E = 0;
    end
    else if(D[0]==1)
    begin
    warning <= 1;
    if(pos_signal==1)
    A <= A + 1;
    end
    else if(D[1]==1)
    begin
    if(pos_signal==1)
    B <= B + 1;
    end
    else if(D[2]==1)
    begin
    if(pos_signal==1)
    C <= C + 1;
    end
    else if(D[3]==1)
    begin
    if(pos_signal==1)
    E <= E + 1;
    end
    if(A==1&&B==1&&C==1&&E==1)
    green <= 1;
    warning<=0;
    end
endmodule
