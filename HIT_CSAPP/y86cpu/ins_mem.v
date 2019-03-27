`include "defines.vh"

module ins_mem(
    input[`QWORD] addr,
    output[79:0] bytes,
    input imem_err
);

parameter INS_MEM_SIZE = 1024;

reg[`BYTE] mem[0:INS_MEM_SIZE-1];

assign imem_err = addr >= INS_MEM_SIZE-10;

assign bytes[7:0] = mem[addr];
assign bytes[15:8] = mem[addr+1];
assign bytes[23:16] = mem[addr+2];
assign bytes[31:24] = mem[addr+3];
assign bytes[39:32] = mem[addr+4];
assign bytes[47:40] = mem[addr+5];
assign bytes[55:48] = mem[addr+6];
assign bytes[63:56] = mem[addr+7];
assign bytes[71:64] = mem[addr+8];
assign bytes[79:72] = mem[addr+9];

initial
fork
    mem[0] <= 8'H30; mem[1] <= 8'HF0; mem[2] <= 8'HEF; mem[3] <= 8'H00; mem[4] <= 8'H00;
    mem[5] <= 8'H00; mem[6] <= 8'H00; mem[7] <= 8'H00; mem[8] <= 8'H00; mem[9] <= 8'H00; //irmovq $0x00000000000000ef, %rax
    mem[10] <= 8'H30; mem[11] <= 8'HF2; mem[12] <= 8'HFF; mem[13] <= 8'HFF; mem[14] <= 8'HFF;
    mem[15] <= 8'HFF; mem[16] <= 8'HFF; mem[17] <= 8'HFF; mem[18] <= 8'HFF; mem[19] <= 8'HFF;//irmovq $0xffffffffffffffff, %rcx
    mem[20] <= 8'H60; mem[21] <= 8'H02; //addq   %rax, %rcx
    mem[22] <= 8'H77; mem[23] <= 8'H21; mem[24] <= 8'H00; mem[25] <= 8'H00;
    mem[26] <= 8'H00; mem[27] <= 8'H00; mem[28] <= 8'H00; mem[29] <= 8'H00; mem[30] <= 8'H00;//jg   $33
    mem[31] <= 8'HA0; mem[32] <= 8'H0F; //pushq  %rax
    mem[33] <= 8'HA0; mem[34] <= 8'H2F; //pushq  %rcx
    mem[35] <= 8'H00;//halt
join

endmodule
