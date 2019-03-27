`timescale 1ns / 1ps
//////////////////////////////////////////////////////////////////////////////////
// Company: 
// Engineer: 
// 
// Create Date: 2017/12/16 10:42:51
// Design Name: 
// Module Name: dmem
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

module dmem(
    input clk,
    input[`QWORD] mem_addr,
    input[`QWORD] mem_data,
    input mem_write,
    input[`QWORD] read_addr,
    output[`QWORD] read_data,
    output dmem_err
);
        
reg[`BYTE] mem[0:`DATA_MEM_SIZE-1];

assign dmem_err = mem_addr > `DATA_MEM_SIZE - 8 ? 1'B1 : 1'B0;

always @ (posedge clk)
begin
    if(mem_write && ~dmem_err)
    begin
        mem[mem_addr] <= mem_data[7:0];
        mem[mem_addr+1] <= mem_data[15:8];
        mem[mem_addr+2] <= mem_data[23:16];
        mem[mem_addr+3] <= mem_data[31:24];
        mem[mem_addr+4] <= mem_data[39:32];
        mem[mem_addr+5] <= mem_data[47:40];
        mem[mem_addr+6] <= mem_data[55:48];
        mem[mem_addr+7] <= mem_data[63:56];
    end
end

assign read_data[7:0] = mem[read_addr];
assign read_data[15:8] = mem[read_addr+1];
assign  read_data[23:16] = mem[read_addr+2];
assign  read_data[31:24] = mem[read_addr+3];
assign  read_data[39:32] = mem[read_addr+4];
assign  read_data[47:40] = mem[read_addr+5];
assign  read_data[55:48] = mem[read_addr+6];
assign  read_data[63:56] = mem[read_addr+7];

endmodule
