`timescale 1ns / 1ps
//////////////////////////////////////////////////////////////////////////////////
// Company: 
// Engineer: 
// 
// Create Date: 2017/06/15 21:37:23
// Design Name: 
// Module Name: Inputer
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


module Inputer(D0, D1, D2, D3, D4, D5, D6, D7, Dout, Valid, Input_light);
    
    input D0, D1, D2, D3, D4, D5, D6, D7;
    output reg [2:0] Dout;
    output Valid;
    output [7:0] Input_light;
    
    
    assign Valid = D0 | D1 | D2 | D3 | D4 | D5 | D6 | D7;
    assign Input_light[0] = D0;
    assign Input_light[1] = D1;
    assign Input_light[2] = D2;
    assign Input_light[3] = D3;
    assign Input_light[4] = D4;
    assign Input_light[5] = D5;
    assign Input_light[6] = D6;
    assign Input_light[7] = D7;
    
    always @(posedge Valid)
    begin
        if(D0 == 1)
            Dout <= 3'b000;
        else if(D1 == 1)
            Dout <= 3'b001;
        else if(D2 == 1)
            Dout <= 3'b010;
        else if(D3 == 1)
            Dout <= 3'b011;
        else if(D4 == 1)
            Dout <= 3'b100;
        else if(D5 == 1)
            Dout <= 3'b101;
        else if(D6 == 1)
            Dout <= 3'b110;
        else if(D7 == 1)
            Dout <= 3'b111;
    end
    /*always @(negedge D0) Dout <= 3'b000;
    always @(negedge D1) Dout <= 3'b001;
    always @(negedge D2) Dout <= 3'b010;
    always @(negedge D3) Dout <= 3'b011;
    always @(negedge D4) Dout <= 3'b100;
    always @(negedge D5) Dout <= 3'b101;
    always @(negedge D6) Dout <= 3'b110;
    always @(negedge D7) Dout <= 3'b111;*/
    
endmodule
