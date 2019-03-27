`include "defines.vh"
parameter DATA_MEM_SIZE=1024;

module data_mem(
    input clk,
    input[`QWORD] write_addr,
    input[`QWORD] read_addr,
    input[`QWORD] write_bytes,
    output reg[79:0] read_bytes,
    output invalid_addr,
    input write,
    input read
    );
reg[`BYTE] mem[0:DATA_MEM_SIZE-1];

assign invalid_addr = (read ? (read_addr > DATA_MEM_SIZE-8) : 1'B0) | (write ? (write_addr > DATA_MEM_SIZE-8) : 1'B0); 

always @ (negedge clk)
begin
    if(write && invalid_addr)
    fork
        mem[write_addr] <= write_bytes[7:0];
        mem[write_addr+1] <= write_bytes[15:8];
        mem[write_addr+2] <= write_bytes[23:16];
        mem[write_addr+3] <= write_bytes[31:24];
        mem[write_addr+4] <= write_bytes[39:32];
        mem[write_addr+5] <= write_bytes[47:40];
        mem[write_addr+6] <= write_bytes[55:48];
        mem[write_addr+7] <= write_bytes[63:56];
    join
end

always @ (read_addr)
fork
    read_bytes[7:0] <= mem[read_addr];
    read_bytes[15:8] <= mem[read_addr+1];
    read_bytes[23:16] <= mem[read_addr+2];
    read_bytes[31:24] <= mem[read_addr+3];
    read_bytes[39:32] <= mem[read_addr+4];
    read_bytes[47:40] <= mem[read_addr+5];
    read_bytes[55:48] <= mem[read_addr+6];
    read_bytes[63:56] <= mem[read_addr+7];
join

endmodule
