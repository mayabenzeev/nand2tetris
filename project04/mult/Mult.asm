// This file is part of www.nand2tetris.org
// and the book "The Elements of Computing Systems"
// by Nisan and Schocken, MIT Press.
// File name: projects/04/Mult.asm

// Multiplies R0 and R1 and stores the result in R2.
// Assumes that R0 >= 0, R1 >= 0, and R0 * R1 < 32768.
// (R0, R1, R2 refer to RAM[0], RAM[1], and RAM[2], respectively.)
	
	//initialize R2 to 0
	@R2
	M=0
	//R3=1 indexer
	@R3
	M=1
(LOOP)
	//if (R3==R1) goto END
	@R3
	D=M
	@R1
	D=D-M
	@END
	D;JGT
	//else RAM[R2] = RAM[R2] + RAM[R0]
	@R0
	D=M
	@R2
	M=D+M
	//R3 = R3 + R3
	@R3
	M=M+1
	//goto LOOP
	@LOOP
	0;JMP
(END)
	@END
	0;JMP
