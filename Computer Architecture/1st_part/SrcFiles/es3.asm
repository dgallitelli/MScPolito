; Computer Architectures (02LSEOV)
; Problem n. 3
public _countweeks
.MODEL small, C
.STACK
.DATA
; variables
DAYPERMONTH DB 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31
DATE DB 8 DUP(?)
PAST_FEBRUARY DB ?
TOTALDAYS DW ?
DAYSPERYEAR DW 365
.CODE
_countweeks PROC NEAR
	
	PUSH BP
	MOV BP, SP
	MOV SI, [BP]+4 											; pointer to char array
	MOV CX, 8
	MOV BX, 0
read_date:
	MOV DL, [SI]
	MOV DATE[BX], DL
	INC SI
	INC BX
	LOOP read_date
	
	; Now the DATE[] holds the date in the following format DD/MM/YY 

	; DAYS COMPUTATION
dd_compute:
	XOR AX, AX
	MOV AL, DATE[0]							; Load in AX the first character, standing for the most significant char of DD/mm/yy
	MOV BX, 10
	MUL BX
	ADD AL, DATE[1]						; Sum the current day count by the second char of DD/mm/yy
	DEC AL									; To consider starting from 1st of January
	MOV TOTALDAYS, AX
	; END OF DAYS COMPUTATION
	
	; MONTH COMPUTATION
mm_compute:
	XOR AX, AX
	MOV AL, DATE[3]
	MOV BX, 10
	MUL BL
	ADD AL, DATE[4]
	DEC AL
	CMP AL, 0
	JE january
	CMP AL, 1
	JE february
	MOV PAST_FEBRUARY, 1					; Set the flag to check for leap year ONLY IF CHECKING MONTH AFTER FEB
	february:
	DEC AX
	MOV BX, AX
	MOV CX, AX
	MOV AX, TOTALDAYS
increment_days_from_month:
	ADD AL, DAYPERMONTH[BX]
	LOOP increment_days_from_month
	MOV TOTALDAYS, AX
january:
	; END OF MONTH COMPUTATION
	
	; YEAR COMPUTATION
yy_compute:
	XOR AX, AX
	MOV AL, DATE[6] 
	MOV BX, 10
	MUL BL
	ADD AL, DATE[7]								; Sum the Second char of dd/mm/YY
	MOV BL, AL									; Store the value of current year
	MOV CX, 365
	MUL CL
	ADD TOTALDAYS, AX
	
	; Check for leap years up to this year
	XOR AX, AX
	MOV AL, BL									; restore value of current year
	MOV DL, 4
	DIV DL										; Dividing by 8-bit register, stores the remainder of division in AH and the quotient in AL
												; AL represents the number of leap days since 1/1/00, excluding the one from year 2000.
	CMP AH, 0
	JNE out_leap_check							; There's remainder, the year is not a leap year
	CMP PAST_FEBRUARY, 1
	JNE out_leap_check
	INC AL
	out_leap_check:
	
	ADD TOTALDAYS, AX
	XOR AH, AH
	MOV AL, BL
	MOV BX, DAYSPERYEAR
	MUL BX
	ADD TOTALDAYS, AX
	
	; #WEEKS COMPUTATION
week_compute:
	MOV AX, TOTALDAYS 
	MOV BX, 7
	DIV BX
	; END OF #WEEKS COMPUTATION
	
	POP BP
	RET														; Before RETURN, result always in AX!
_countweeks ENDP
END