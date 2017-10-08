; Computer Architectures (02LSEOV)
; Problem n. 0
DIM	  EQU 20
       .MODEL small
       .STACK
       .DATA
VETT   DB     DIM DUP(?)
       .CODE
       .STARTUP
       MOV CX,DIM
       MOV DI,0
       MOV AH,1
lab1:  INT 21H
       MOV VETT[DI],AL
       INC DI
       DEC CX
       CMP CX,0
       JNZ lab1
       MOV CX,DIM
       MOV AH,2
lab2:  DEC DI
       MOV DL,VETT[DI]
       INT 21H
       DEC CX
       CMP CX,0
       JNZ lab2
       .EXIT
       END
