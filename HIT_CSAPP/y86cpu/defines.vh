`timescale 1ns / 1ps
//////////////////////////////////////////////////////////////////////////////////
// Company: 
// Engineer: 
// 
// Create Date: 2017/11/26 22:26:03
// Design Name: 
// Module Name: defines
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

//ICode Defines
`define IHALT    4'H0
`define INOP     4'H1
`define IRRMOVQ  4'H2
`define IIRMOVQ  4'H3
`define IRMMOVQ  4'H4
`define IMRMOVQ  4'H5
`define IOPQ     4'H6
`define IIADDQ   4'H6
`define IJXX     4'H7
`define ICMOVXX  4'H2   // equal to IRRMOVQ
`define ICALL    4'H8
`define IRET     4'H9
`define IPUSHQ   4'HA
`define IPOPQ    4'HB

//FCode Defines
//1.FOPQ
`define FADDQ    4'H0

//2.FJXX
`define FJMP     4'H1
`define FJLE     4'H2
`define FJL      4'H3
`define FJE      4'H4
`define FJNE     4'H5
`define FJGE     4'H6
`define FJG      4'H7

//3.FCMOVXX
`define FRRMOVQ  4'H0
`define FCMOVLEQ 4'H1
`define FCMOVLQ  4'H2
`define FCMOVEQ  4'H3
`define FCMOVNEQ 4'H4
`define FCMOVGEQ 4'H5
`define FCMOVGQ  4'H6

`define FNONE 4'H0

//register index
`define RRAX 4'H0
`define RRBX 4'H1
`define RRCX 4'H2
`define RRDX 4'H3
`define RRSI 4'H4
`define RRDI 4'H5
`define RRSP 4'H6
`define RRBP 4'H7
`define RR8  4'H8
`define RR9  4'H9
`define RR10 4'HA
`define RR11 4'HB
`define RR12 4'HC
`define RR13 4'HD
`define RR14 4'HE
`define RNONE 4'HF

// instruction related
`define ICODE 7:4
`define IFUN  3:0
`define RA    15:12
`define RB    11:8
`define VALC  79:16
`define NO_REGIDS_VALC 71:8

// size definitions
`define BYTE 7:0
`define WORD 15:0
`define DWORD 31:0
`define QWORD 63:0

// cc regs
`define ZF 0
`define SF 1
`define OF 2

// stat definitions
`define SAOK 2'H0
`define SADR 2'H1
`define SINS 2'H2
`define SHLT 2'H3

`define DATA_MEM_SIZE 1024
