// 
//////////////////////////////////////////////////////////////////////////////////


module elock(Clk, Reset, D, Check,  Red, Green, warn_sig, input_light, Circ, count, countDown);
`timescale 1ns / 1ps
//////////////////////////////////////////////////////////////////////////////////
// Company: 
// Engineer: 
// 
// Create Date: 2017/06/15 16:03:04
// Design Name: 
// Module Name: elock 
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

    parameter LOCK = 1000;
    parameter DELAY = 4;

    input Clk;
    input Reset;
    input [7:0] D;
    input Check;
    output Red, Green;
    output warn_sig;
    output [7:0] input_light;
    output [1:0] count;
    output [0:8] countDown;
    output Circ;
    wire [2:0] D_num;
    wire Valid;
    wire [11:0] Key;   //the key_reg output
    wire [11:0] Val;   //the input_reg output
    wire timeout;
    reg Correct = 0; //If the lock has been unlocked
    reg begin_count = 0;
    
    Inputer inputer(.D0(D[0]), .D1(D[1]), .D2(D[2]), .D3(D[3]), .D4(D[4]), .D5(D[5]), .D6(D[6]), .D7(D[7]), .Dout(D_num), .Valid(Valid), .Input_light(input_light));
    key_register key_reg(.Din(D_num), .Dout(Key), .Ld(Valid), .Reset(Reset), .unlocked(Correct));
    input_register input_reg(.Din(D_num), .Dout(Val), .Ld(Valid), .Circ(Circ), .Reset(Reset), .count(count));
    warning warn(.Clk(Clk), .begin_count(begin_count), .unlocked(Correct), .Reset(Reset), .warn_sig(timeout), .show_num(countDown));
    
    always @(posedge Valid)
    begin
        begin_count <= ~Reset & ~Correct;
    end
    
    always @(negedge Check)
    begin
        if(Val == Key)
        begin
            Correct <= 1'b1;
        end
        else
        begin
            Correct <= 0'b0;
        end
    end
    
    assign Green = Correct;
    assign Red = ~Correct;
    assign warn_sig = timeout & ~Correct & ~Reset;
    
endmodule
