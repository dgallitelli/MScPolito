; Computer Architectures (02LSEOV)
; Problem n. 2
.MODEL small
.STACK
.DATA
; variables
; arrays for text acquisition
DIM EQU 52
DIM_BUFF EQU 40
FIRST_ROW DB DIM_BUFF DUP(?)
SECOND_ROW DB DIM_BUFF DUP(?)
THIRD_ROW DB DIM_BUFF DUP(?)
FOURTH_ROW DB DIM_BUFF DUP(?)
; Buffer for text acquisition
BUFFER DB DIM_BUFF,DIM_BUFF+1 DUP(?)
; Message to be printed
INSERT_STRING_MSG DB 'Insert the string please: $'
HALF_TIME_MSG DB 'Character appearing half the maximum time: $'
GLOBAL_MAX_MSG DB 'Character appearing the most: $'
CAESAR_TEXT DB 'This is the text after the Caesar Cypher: $'
; Arrays for higher/lower case counting: the first 26 elements are for higher case, the others for lower
CHARS_ARRAY_1 DB DIM DUP(0)
CHARS_ARRAY_2 DB DIM DUP(0)
CHARS_ARRAY_3 DB DIM DUP(0)
CHARS_ARRAY_4 DB DIM DUP(0)
TOTAL_APPEARANCES DB DIM DUP(0)
; Each CHAR_ARRAY will have one character appearing most times. This array keeps the number of times it appears [0->3] and its value [5->8], leaving two elements for the character appearing the most in all strings.
MOST_APPEAR DW 10 DUP(0)
; Offsets for higher/lower case calculations.
HIGHER_CASE_OFFSET EQU 65
LOWER_CASE_OFFSET EQU 122
CARRIAGE_RETURN EQU 0Dh
; Table for redirect to appropriate ROW and CHARS_ARRAY
REDIRECT_TABLE DW 8 DUP(?)
; Constant for Caeser Cipher
CAESAR_FACTOR EQU 3
.CODE

new_line PROC
	; Print new line and carriage return
	PUSH DX
	PUSH AX
	MOV DL, 10
	MOV AH, 02h
	INT 21H
	MOV DL, 13
	MOV AH, 02h
	INT 21H
	POP AX
	POP DX
	RET
new_line ENDP

.STARTUP
; body

; SECTION 0 - Initialization of the REDIRECT_TABLE - used later
MOV AX, OFFSET FIRST_ROW
MOV BX, OFFSET SECOND_ROW
MOV CX, OFFSET THIRD_ROW
MOV DX, OFFSET FOURTH_ROW
MOV REDIRECT_TABLE[0], AX
MOV REDIRECT_TABLE[2], BX
MOV REDIRECT_TABLE[4], CX
MOV REDIRECT_TABLE[6], DX
MOV AX, OFFSET CHARS_ARRAY_1
MOV BX, OFFSET CHARS_ARRAY_2
MOV CX, OFFSET CHARS_ARRAY_3
MOV DX, OFFSET CHARS_ARRAY_4
MOV REDIRECT_TABLE[8], AX
MOV REDIRECT_TABLE[10], BX
MOV REDIRECT_TABLE[12], CX
MOV REDIRECT_TABLE[14], DX

; SECTION 1 - ARRAY ACQUISITION THROUGH INT 21H
XOR SI, SI
input_strings:
; FILLING OF THE FIRST ROW
CALL new_line
; Print a message asking for the string in input
LEA DX, INSERT_STRING_MSG
MOV AH, 9
INT 21H
; Call the interrupt for reading up to 40 chars (size of buffer)
LEA DX, BUFFER
MOV AH, 0Ah
INT 21H
;Initialize the movement from buffer to the row
XOR AX, AX								; Contains the actual character that I want to store
XOR BX, BX								; Will contain the address of the row to be accessed
XOR CX, CX								; Will contain the actual dimension of the buffer, the useful characters stored in it
XOR DI, DI								; Index for the position of the character in the ROW
MOV CL, BUFFER[1]						; Store the dimension of the buffer
fill_row:
MOV AL, BUFFER[DI+2]					; AL <- Character from the buffer, position DI
; Access the REDIRECT_TABLE[SI][DI], and store the character from AL
MOV BX, REDIRECT_TABLE[SI]				; BX <- OFFSET SI_ROW = address of the row in position SI (position 0 contains the address of FIRST_ROW and so on)
MOV BYTE PTR [BX+DI], AL				; Access memory at address stored in BX + DI, and store the content of AL
INC DI
LOOP fill_row
MOV AL, CARRIAGE_RETURN					; The carriage return ASCII is used to signal the end of the string and to get out of the loop.
MOV BX, REDIRECT_TABLE[SI]				; BX <- OFFSET SI_ROW = address of the row in position BP (position 0 contains the address of FIRST_ROW and so on)
MOV BYTE PTR [BX+DI], AL				; Access memory at address stored in BX + DI, and store the content of AL

; Code section to clean buffer of last inputs
MOV CX, DIM_BUFF+1
MOV DI, 1								; Clean starting from the second element, the # of valid cells
clean_buffer:
MOV BUFFER[DI], 0
INC DI
LOOP clean_buffer

; Loop to the next row
INC SI
INC SI
CMP SI, 8
JS input_strings						; Jump back to input if the SF is set, which means we've not filled the 4 arrays

; Now, the four arrays are filled. Operations can be done on them.

; SECTION 2 - Letter counting for each row and printing the one that appears a number of times equal to half the maximum times
; In ASCII, Higher case characters have a value between 65 and 90 in decimal, inclusive.
; Lower case characters instead range between 97 and 122, inclusive.
; I'm using a 52 element array for each of them, called CHARS_ARRAY_#, where the element at position i contains the number of times that the character OFFSET+i is present in its row.
; The first 26 elements of each CHARS_ARRAY_# relate to the appearances of higher case characters, the others for lower case ones.
; OFFSET is of course 65 for higher case chars, and 97 for lower case chars.

XOR SI, SI								; Index for looping on rows
chars_appearances:
XOR BX, BX								; Will contain the address of the row to be accessed
MOV CX, DIM								; Dimension for looping inside the arrays
XOR DI, DI								; Index for the position inside the ROW
read_char:
XOR AX, AX								; Will contain the character for further analysis
; I load the elements of the row to check their value if it's in the range
MOV BX, REDIRECT_TABLE[SI]				; BX <- OFFSET SI_ROW = address of the row in position SI (position 0 contains the address of FIRST_ROW and so on)
MOV BYTE PTR AL, [BX+DI]				; Access memory at address stored in BX + DI, and load the content of AL

; Test if the value read is a character or not; if so, test if it's higher or lower case
CMP AL, CARRIAGE_RETURN					; The carriage return ASCII is used to signal the end of the string and to get out of the loop.
JE next_row
CMP AL, HIGHER_CASE_OFFSET
JAE above_higher_char
JMP after_char
above_higher_char:
CMP AL, 90
JBE higher_char
CMP AL, 97
JAE above_lower_char
JMP after_char
above_lower_char:
CMP AL, LOWER_CASE_OFFSET
JBE lower_char
higher_char:							; Here if the character is higher case
SUB AX, HIGHER_CASE_OFFSET				; Convert from ASCII to a value useful between 0 and 25
JMP good_char_read
lower_char:								; Here if the character is lower case
SUB AX, 97								; To get letter to number correspondance
ADD AX, 26								; For the position insede the array (second 26 numbers are lower case)
good_char_read:
PUSH DI
MOV DI, AX								; AX -> DI that will be used as index to write in CHARS_ARRAY_#
MOV BX, REDIRECT_TABLE[SI+8]			; BX <- OFFSET (CHARS_ARRAY_SI)+8 = address of the row in position SI+8 (position 8 contains the address of CHARS_ARRAY_1 and so on)
INC BYTE PTR [BX+DI]					; Access memory at address stored in BX + DI, and increase that value (appearances of one character)
POP DI
JMP after_char

;here if not a character or after value read
after_char:
INC DI
LOOP read_char

next_row:
INC SI
INC SI
CMP SI, 8
JS chars_appearances					; Jump back if the SF is set, which means we've not filled the 4 arrays

; ======================================
; ======================================

; The arrays CHAR_ARRAY_# now contain the number of times the characters appear in the string
; Now I need to print the character(s) that appear half of the maximum time. I will truncate to choose the half values.
XOR SI, SI
outer_maximum_time_search:
XOR DI, DI
MOV CX, DIM
inner_maximum_time_search:
XOR AX, AX
;First, look for the character appearing the most.
MOV BX, REDIRECT_TABLE[SI+8]			; BX <- OFFSET CHARS_ARRAY_SI+8 = address of the row in position SI+8 (position 8 contains the address of CHARS_ARRAY_1 and so on)
MOV AL, [BX+DI]							; Access memory at address stored in BX + DI, and load that value in AL (appearances of one character)
CMP AL, BYTE PTR MOST_APPEAR[SI]		; Compare that value with the local maximum stored in MOST_APPEAR[SI]
JNA not_maximum_or_maximum_found

MOV BYTE PTR MOST_APPEAR[SI], AL		; I store how many times the character appears (found a new local maximum)
MOV DX, DI
CMP DX, 26								; Check if it's an higher/lower char from its position in the array (the first 26 are for lower case chars)
JNS maximum_lower_case					; Jump if the local maximum character is a lower case
ADD DX, HIGHER_CASE_OFFSET				; Get ASCII of higher case character (a -> 65 ... z -> 90)
MOV BYTE PTR MOST_APPEAR[SI+10], DL		; Store the value corresponding to the new character (higher case)
JMP not_maximum_or_maximum_found
maximum_lower_case:
SUB DX, 26								; Get relative position of lower case character (a -> 0, b -> 1, ... z -> 24)
ADD DX, 97								; Get ASCII of lower case character (a -> 97 ... z -> 122)
MOV BYTE PTR MOST_APPEAR[SI+10], DL		; Store the value corresponding to the new character (higher case)

not_maximum_or_maximum_found:			; Skip to the next iteration
INC DI
LOOP inner_maximum_time_search
INC SI									; Check the next row (if not the fourth already)
INC SI
CMP SI, 8
JS outer_maximum_time_search			; Jump back if the SF is set, which means we've not filled the 4 arrays


; ======================================
; ======================================

; LOOKING FOR CHARACTERS APPEARING HALF OF THE MAXIMUM TIME FOR EACH ROW
XOR SI, SI
outer_half_time_search:
MOV CX, DIM
XOR DI, DI
XOR AX, AX
; Compute half time of the row SI
MOV AL, BYTE PTR MOST_APPEAR[SI]
SHR AL, 1
CMP AL, 0
JE half_time_next_iteration
inner_half_time_search:
;Then, I check which character appears exactly that number of times and print them
MOV BX, REDIRECT_TABLE[SI+8]			; BX <- OFFSET CHARS_ARRAY_SI+8 = address of the row in position SI+8 (position 8 contains the address of CHARS_ARRAY_1 and so on)
MOV DL, [BX+DI]							; Access memory at address stored in BX + DI, and compare that value with the local maximum divided by 2
CMP AL, DL
JNE not_half_time_char
; Compute the ASCII of that character
XOR BX, BX
MOV DX, DI
CMP DX, 26
JB half_time_higher_char
SUB DX, 26								; Get relative position of lower case character (a -> 0, b -> 1, ... z -> 25)
ADD DX, 32								; Get ASCII of lower case character (a -> 97=32+HIGHER_CASE_OFFSET ... z -> 122=32+HIGHER_CASE_OFFSET+POSITION)
half_time_higher_char:
ADD DX, HIGHER_CASE_OFFSET
PUSH AX
PUSH DX
CALL new_line							; Print the ASCII value, after printing a new line and a message
LEA DX, HALF_TIME_MSG
MOV AH, 9
INT 21H
POP DX
MOV AH, 2
INT 21H
POP AX
not_half_time_char:	
INC DI
LOOP inner_half_time_search
half_time_next_iteration:
INC SI
INC SI
CMP SI, 8
JS outer_half_time_search				; Jump back if the SF is set, which means we've not filled the 4 arrays


; ======================================
; ======================================

; SECTION 3 - PRINT THE CHARACTER APPEARING THE MAXIMUM NUMBER OF TIME
; To compute which value appears the most across all of the four rows, I need to sum the relative appearings of each character.


XOR SI, SI
XOR AX, AX
outer_total_appearances:
XOR DI, DI
MOV CX, DIM
total_appearances_compute:
MOV BX, REDIRECT_TABLE[SI+8]			; BX <- OFFSET CHARS_ARRAY_SI+8 = address of the row in position SI+8 (position 8 contains the address of CHARS_ARRAY_1 and so on)
MOV AL, [BX+DI]							; Access memory at address stored in BX + DI, and store that value in AL (appearances of one character)
ADD TOTAL_APPEARANCES[DI], AL			; Increment the total enconunters of character in position DI by the number of its encounters in the CHARS_ARRAY_# (loaded in AL)
INC DI
LOOP total_appearances_compute
INC SI
INC SI
CMP SI, 8
JS outer_total_appearances

XOR DI, DI
XOR SI, SI
MOV CX, DIM
XOR AX, AX
search_for_maximum:						; Check which character appears the most.
CMP AL, TOTAL_APPEARANCES[DI]
JAE not_maximum
MOV AL, TOTAL_APPEARANCES[DI]
MOV DX, DI
not_maximum:
INC DI
LOOP search_for_maximum

; Print the character appearing the most, after converting it to ASCII: it is already stored in DX (its position, from which I can compute the value)
CMP DX, 26
JB print_lower_case
SUB DX, 26										; Get relative position of lower case character (a -> 0, b -> 1, ... z -> 25)
ADD DX, 32										; Get ASCII of lower case character (a -> 97=32+HIGHER_CASE_OFFSET ... z -> 122=32+HIGHER_CASE_OFFSET+POSITION)
print_lower_case:
ADD DX, HIGHER_CASE_OFFSET
PUSH DX
CALL new_line									; Print the ASCII value, after printing a new line and a message
LEA DX, GLOBAL_MAX_MSG
MOV AH, 9
INT 21H
POP DX
MOV AH, 2
INT 21H


; ======================================
; ======================================

; SECTION 4 - Caesar Cipher

; Print a message to define section 4
CALL new_line
LEA DX, CAESAR_TEXT
MOV AH, 9
INT 21H
CALL new_line

XOR SI, SI
caesar_row:
MOV CX, DIM_BUFF
XOR DX, DX
XOR DI, DI
caesar_char:
XOR BX, BX
XOR AX, AX
; Step 1 - Read the character from FIRST_ROW and store it in AL
MOV BX, REDIRECT_TABLE[SI]				; BX <- OFFSET SI_ROW = address of the row in position SI (position 0 contains the address of FIRST_ROW and so on)
MOV BYTE PTR AL, [BX+DI]				; Access memory at address stored in BX + DI, and load the content of AL
; Step 2 - Check if it's a character [65;90] OR [97;122]
CMP AL, CARRIAGE_RETURN
JE caesar_next_iteration
CMP AL, HIGHER_CASE_OFFSET
JAE check_above_higher_char
JMP print_char
check_above_higher_char:
CMP AL, 90
JBE check_is_a_char
CMP AL, 97
JAE check_above_lower_char
JMP print_char
check_above_lower_char:
CMP AL, LOWER_CASE_OFFSET
JBE check_is_a_char

check_is_a_char:
; Step 3 - Increment by CAESAR_FACTOR and DX [DX is the index of the row, ranging 0->3]
ADD AX, CAESAR_FACTOR
SHR SI, 1								; Dividing by 2 because the index on DW arrays is incremented by 2 each time
ADD AX, SI
SHL SI, 1
; Step 4 - Check again for the range
CMP AL, 90								; Compare with 90, upper bound of higher case characters
JBE print_char							; Jump if AL <= 90
CMP AL, 97								; Compare with 97, lower bound of lower case characters
JB caesar_print_lower					; Jump if AL < 97
CMP AL, 122								; Compare with 122, upper bound of lower case characters
JBE print_char							; Jump if AL <= 122
caesar_print_higher:					; Here if AL > 122
ADD AL, 65								; AL <- AL + 65
SUB AL, LOWER_CASE_OFFSET				; AL <- AL - 122
DEC AL									; Decrement in order not to consider the bounds of each character group
JMP print_char
caesar_print_lower:						; Here if 90 < AL < 97
ADD AL, 97								; AL <- AL + 97
SUB AL, 90								; AL <- AL - 90
DEC AL									; Decrement in order not to consider the bounds of each character group
; Step 5 - Print the character
print_char:
PUSH DX
MOV DL, AL
MOV AH, 2
INT 21h
POP DX

INC DI
LOOP caesar_char
caesar_next_iteration:
CALL new_line
INC SI
INC SI
CMP SI, 8
JS caesar_row

.EXIT

END
