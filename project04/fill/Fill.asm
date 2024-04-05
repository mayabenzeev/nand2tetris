// This file is part of www.nand2tetris.org
// and the book "The Elements of Computing Systems"
// by Nisan and Schocken, MIT Press.
// File name: projects/04/Fill.asm

// Runs an infinite loop that listens to the keyboard input.
// When a key is pressed (any key), the program blackens the screen
// by writing 'black' in every pixel;
// the screen should remain fully black as long as the key is pressed. 
// When no key is pressed, the program clears the screen by writing
// 'white' in every pixel;
// the screen should remain fully clear as long as no key is pressed.

(RESET)
// RAM[i] = 0
@i
M=0
// get value from keybord
@KBD
D=M
@BLACK
D;JGT
@WHITE
D;JEQ

(BLACK)
// D = RAM[i]
@i
D=M 
@SCREEN
A=A+D //goto the right place in screen
M=-1 //color pixel in black
@NEXTB
0;JMP

(NEXTB)
//if i <= 8191 reset
@8192 
D=A
@i
M=M+1
D=D-M
@RESET
D;JLE
// else goto black
@BLACK
0;JMP

(WHITE)
@i
D=M // D = RAM[i]
@SCREEN
A=A+D //goto the right place in screen
M=0 //color pixel in white
@NEXTW
0;JMP

(NEXTW)
//if i <= 8191 reset
@8192 
D=A
@i
M=M+1
D=D-M
@RESET
D;JLE
// else increase i
@WHITE
0;JMP

