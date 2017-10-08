; Computer Architectures (02LSEOV)
; Problem n. 1
.MODEL small
.STACK
.DATA
	DIM EQU 10								;Dimension of the array myArrA
	myArrA DB 12,14,25,16,32,22,1,64,11,10	;Declaration of array myArrA
	myArrB DW DIM-1 DUP (0)					;Declaration of array myArrB
	M DD 9*9 DUP (?)						;Declaration of matrix M
.CODE
.STARTUP
		; //////////////////////////////////
		; PART 1 - Sum of consecutive values
		; //////////////////////////////////

		;Initialization

		xor si,si							;Initialize the index for array A
		xor di, di							;Initialize index for array B
		mov cx, DIM-1						;Counter for array filling (9 elements)
		xor ax,ax							;clean ax

sum_v1:	;from this point on, the sum is performed this way:
		;first, the element is extended to 16 bits in order to avoid overflow, then summed with the adjacent.
		;the result is then pushed to the array B which is made of words, therefore 16 bits elements.
		mov al, myArrA[si]						;copy the element in the LSB of ax
		mov bl, myArrA[si+1]
		xor ah,ah
		xor bh,bh
		add ax,bx
		mov myArrB[di], ax
		;then we cycle until the vector B is filled, which means until si = DIM-1 = 9
		inc si
		inc di
		inc di
		loop sum_v1

		xor si,si							;Initialize the index of the array
		xor ax,ax							;clean ax
		xor bx,bx							;clean bx
		xor cx,cx							;clean cx

		; //////////////////////////////////
		; PART 2 - Minimum Computation
		; //////////////////////////////////

		; MINIMUM COMPUTATION FOR ARRAY A.
		; The values considered are all bytes. I don't need to use the whole registers *X but their 8 LSBs are sufficient
		; I will use DL as the buffer for the minimum I am looking for
		mov dl, 0ffh						;setting the minimum to the highest possible value
		xor dh, dh							;cleaning the msbs of dx
		mov cx,DIM							;counter for the array A
min_A_loop:
		; loop for minimum value
		mov al,myArrA[si]
		cmp al, dl							;al-dl
		jae continue_min_A_loop				;jump out if AL-DL>0
		mov dl, al							;new minimum found
continue_min_A_loop:
		inc si
		loop min_A_loop
		;at the end of the loop, AL contains the minimum of the array

		xor si,si							;reset index
		xor ax,ax							;clean ax
		xor bx,bx							;clean bx
		xor cx,cx							;clean cx

		; MINIMUM COMPUTATION FOR ARRAY B.
		; The values considered are all words. The whole *X registers will be used.
		; I will use dx as the buffer for the minimum I am looking for
		mov dx, 0FFFFh						;setting the minimum to the highest possible value
		mov cx,DIM-1						;counter for the array B
min_B_loop:									; loop for minimum value
		mov bx, myArrB[si]
		cmp bx, dx							;bx-dx
		jae continue_min_B_loop				;jump out if bx-dx>0
		mov dx, bx							;new minimum found
continue_min_B_loop:
		inc si
		inc si 								;writing a word, need to increase index twice
		loop min_B_loop
		;at the end of the loop, bx contains the minimum of the array B

		xor cx, cx							;clean cx

		; //////////////////////////////////
		; PART 3 - Matrix M 9x9
		; //////////////////////////////////

		; The matrix will be generated from all possible products of an array of bytes (A) and an array of words (B).
		; Each product would not fit in 16 bits, as the worst case would be (2^8 - 1)(2^9 - 2) = 2^17 - 2*2^9 - 3 = 130045 --> needs 17 bits!
		; Each of the element will be a DOUBLE (4 bytes).

		xor di, di							; Initialization of the index for the inner loop
		xor si, si							; Initialization of the index for the outer loop

M_loop:
		; Outer Loop
		mov AL, myArrA[si]
		xor AH, AH							; AH is used for the MSB of myArrA[si] extended to word
		; At this point, ax is extended to 16-bits
		; Inner Loop
		push bp
		mov bp, di
		shl bp, 1
		mov bx, myArrB[bp]
		pop bp
		; The multiplication takes two 16-bits registers, one of which is IMPLICITELY ax
		; and produces a result in 32-bits stored in dx:ax
		mul bx
		; I need to calculate the offset in the matrix from the two indices.
		push bp
		mov bp, si
		shl bp, 1
		shl bp, 1
		shl bp, 1
		add bp, si
		add bp, di							; offset = outer_index * 9 + inner_index
		shl bp, 1
		shl bp, 1
		; I cannot move 32 bits directly in memory. So I need to move first the LSB in ax
		mov WORD PTR M[bp], ax
		; Then move the MSB in dx right next to the previously written position
		mov WORD PTR M[bp+2], dx
		pop bp
		; inner loop check
		inc di								; increment index for inner loop
		cmp di, DIM-1						; di-9
		jnz M_loop							; if di-9<>0 it means we still have iterations in the inner loop to go through
		; outer loop check
		xor di, di							; Reset di
		inc si								; increment index for outer loop
		cmp si, DIM-1						; si-9
		jnz M_loop							; if si-9<>0 it means we still have iterations in the outer loop to go through

		; Which means that the Matrix is now filled and ready, stored in main memory.

		; //////////////////////////////////
		; PART 4 - Matrix maximum computation
		; //////////////////////////////////

		; In this section, I need to find the maximum value of the previously created matrix.
		; At this moment, the matrix is stored in main memory.
		; I will need to load each element of the matrix and compare it with the previous to find the maximum.
		; First, I store in cx:bx the lowest value possible, which is 0, assuming the inserted number are binary.
		mov cx, 0
		mov bx, 0
		; Then I start looping the matrix looking for a bigger number
		xor di, di							; Initialization of the index for the inner loop
		xor si, si							; Initialization of the index for the outer loop
maxM_loop:
		; Before starting the loop, I push BP, as I will use it to calculate the offset in the matrix
		push bp
		mov bp, si
		shl bp, 1
		shl bp, 1
		shl bp, 1
		add bp, si
		add bp, di							; offset = outer_index * 9 + inner_index
		shl bp, 1
		shl bp, 1
		mov ax, WORD PTR M[bp]
		mov dx, WORD PTR M[bp+2]
		pop bp
		; Increase the offset to move to the next value
		; I now have store the element M[si][di] in dx:ax
		; I compare it with the local maximum found and then loop
		; I first compare the MSB. If dx > cx, there's no need for further checks as the number is bigger for sure
		cmp dx, cx
		ja new_maxM							; I jump if dx > cx because I have found a new maximum
		jnae next_iteration_M				; I jump if !(dx >= cx) because it's not a maximum for sure
		cmp ax, bx							; If dx = cx, then I need to check if ax > bx. If so, then I found a new maximum
		jna next_iteration_M				; Otherwise, jump to the next iteration
new_maxM:
		mov bx, ax
		mov cx, dx		
next_iteration_M:
		inc di
		cmp di, DIM-1						; di-9
		jnz maxM_loop						; if di-9<>0 it means we still have iterations in the inner loop to go through
		xor di, di							; reset di
		inc si								; increment index for outer loop
		cmp si, DIM-1						; si-9
		jnz maxM_loop						; if si-9<>0 it means we still have iterations in the outer loop to go through

.EXIT
END
