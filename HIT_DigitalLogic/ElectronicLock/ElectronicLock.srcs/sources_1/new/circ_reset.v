`timescale 1ns / 1ps
//////////////////////////////////////////////////////////////////////////////////
// Company: 
// Engineer: 
// 
// Create Date: 2017/06/21 19:28:49
// Design Name: 
// Module Name: circ_reset
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


module circ_reset(circ, circ_next);
    
    parameter DELAY = 8;
    
    input circ;
    output reg circ_next;
    
    always @(posedge circ)
    begin
        #(DELAY) circ_next = 0;
    end

endmodule
