`timescale 1ns / 1ps
//////////////////////////////////////////////////////////////////////////////////
// Company: 
// Engineer: 
// 
// Create Date: 2017/06/19 08:21:33
// Design Name: 
// Module Name: warning
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


module warning(Clk, begin_count, unlocked, Reset, warn_sig, show_num);
    input Clk;
    input begin_count;
    input unlocked;
    input Reset;
    output reg warn_sig = 1'b0;
    reg [31:0] A = 32'b0;
    output reg [0:8] show_num;
    always@(posedge Clk)
    begin
        if(unlocked == 1'b1)
            A <= 32'b0;
        if(A<32'b11101110011010110010100000000)
        begin
            if(begin_count == 1'b1)
            begin
                A <= A + 1;
            end
            else
            begin
                A <= 32'b0;
            end
        end
        if(A>=32'b11101110011010110010100000000)
        begin
            warn_sig <= 1;
        end
        else
        begin
            warn_sig <= 0;
        end
    end
    
    always @(posedge Clk)
    begin
        if(A < 32'b101111101011110000100000000)
            show_num <= 9'b110110110;
        else if(A < 32'b1011111010111100001000000000)
            show_num <= 9'b101100110;
        else if(A < 32'b10001111000011010001100000000)
            show_num <= 9'b111110010;
        else if(A < 32'b10111110101111000010000000000)
            show_num <= 9'b111011010;
        else if(A < 32'b11101110011010110010100000000)
            show_num <= 9'b101100000;
        else
            show_num <= 9'b111111100;
        show_num[0] <= ~unlocked;
    end
    
endmodule
