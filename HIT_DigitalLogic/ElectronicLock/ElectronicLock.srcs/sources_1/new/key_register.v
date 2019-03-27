`timescale 1ns / 1ps
//////////////////////////////////////////////////////////////////////////////////
// Company: 
// Engineer: 
// 
// Create Date: 2017/06/20 22:53:38
// Design Name: 
// Module Name: lock_register
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


module key_register(Din, Dout, Ld, Reset, unlocked);
    
    input [2:0] Din;
    input Ld;
    input Reset;
    input unlocked;
    output [11:0] Dout;
    reg [11:0] Data = 12'b000000000000;
    reg [1:0] count = 2'b00;
    
    always @(negedge Ld)
    begin
        if(Reset == 1'b1 && unlocked == 1'b1)
        begin
            case(count)
                2'b00:
                begin
                    Data[0] <= Din[0];
                    Data[1] <= Din[1];
                    Data[2] <= Din[2];
                end
                2'b01:
                begin
                    Data[3] <= Din[0];
                    Data[4] <= Din[1];
                    Data[5] <= Din[2];
                end
                2'b10:
                begin
                    Data[6] <= Din[0];
                    Data[7] <= Din[1];
                    Data[8] <= Din[2];
                end
                2'b11:
                begin
                    Data[9] <= Din[0];
                    Data[10] <= Din[1];
                    Data[11] <= Din[2];
                end
            endcase
            count <= count + 1;
        end
        else
            count <= 0;
    end
    
    assign Dout = Data;
    
endmodule
