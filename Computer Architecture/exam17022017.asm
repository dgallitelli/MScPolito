; Computer Architectures (02LSEOV)
; Source code for the exam of 17/2/2017
; S241521 Gallitelli Davide
.MODEL small
.STACK
.DATA
	; variables
	; buffer for NUM_PUR input
	DST_LIST_PRICE DB 10,?, 10 DUP(?)
	DST_CATEGORY DB 3,?,3 DUP(?)
	DST_DISCOUNT DB 7,?, 7 DUP(0)
	CAT_TAX DB 4 DUP(?)						;array for tax % of category
	;------------
	;|xxx|hhhh|k|
	; three unused "x" bits
	; four "h" bits for integer part of tax (up to 12)
	; one "k" bit for fractional part of tak (either .0 or .5)
	;-----------
	NUM_PUR DB ?							;number of purchases
	PUR_OBJ DW 8 DUP(0)						;info about purchases
	;-----------
	;|zzzzzz|vv|cc|dddddd|
	; six "z" bits for integer part of list_price
	; two "v" bits for fractional part of list_price
	; two "c" bits for category ID
	; six "d" bits for discount fractional digits (max 0.984375)
	;-----------
	myAPPLDISC DB ?
	myNET DB ?
	myTAX DB ?
	myFINAL DB ?
	totAPPLDISC DW ?
	totNET DW ?
	totTAX DW ?
	totFINAL DW ?
	; variables for bonus item - 4 elements arrays, one for each category total
	totAPPLDISC_CAT DW 4 DUP(0)
	totNETPRICE_CAT DW 4 DUP(0)
	; Strings to be printed
	STRING_ASK_PUR_OBJ_LIST_PRICE DB "Insert the LIST_PRICE of the purchased object (less than 127.75): $"
	STRING_ERR_LIST_PRICE DB "The LIST_PRICE inserted is too high. Try again (less than 127.75): $"
	STRING_ASK_PUR_OBJ_CATEGORY DB "Insert the CATEGORY of the purchased object: $"
	STRING_ASK_PUR_OBJ_DISCOUNT DB "Insert the DISCOUNT FACTOR of the purchased object (ONLY FRACTIONAL PART): $"
	STRING_ASK_NUM_PUR DB "Insert the Number of Purchases (0 <= x <= 8): $"
	STRING_ERR1 DB "You inserted a value greater than 8. Try again. $"
	STRING_ERR2 DB "You inserted a value smaller than 0. Try again. $"
	STRING_APPL_DISC DB "Applied Discount: $"
	STRING_NET_PRICE DB "Net Price: $"
	STRING_TAX_DUE DB "Tax Due: $"
	STRING_FINAL_PRICE DB "Final Price: $"
	STRING_TOTAL_AD DB "Total Applied Discount: $"
	STRING_TOTAL_NP DB "Total Net Price: $"
	STRING_TOTAL_TAX DB "Total Tax Due: $"
	STRING_TOTAL_FINAL DB "Total Final Price: $"
	STRING_CAT DB "CATEGORY: $"
.CODE
.STARTUP
MAIN PROC
	;-------------------------
	; Initialization section
	;-------------------------
	; Initialization of CAT_TAX
	mov CAT_TAX[0], 0							;tax of 0%
	mov CAT_TAX[1], 01011b						;tax of 5.5%
	mov CAT_TAX[2], 11001b						;tax of 12.5%
	mov CAT_TAX[3], 00101b						;tax of 2.5
	; Initialization of NUM_PUR
	call ASK_NUM_PUR
	; Initialization of PUR_OBJ at run-time
	; Pushing values manually for testing my code
	call PUR_OBJ_FILLING
	; mov PUR_OBJ[0], 0001110100110000b			;product with list_price 7.25, cat 00 and discount 0.75
	; mov PUR_OBJ[2], 0001111101100000b			;product with list_price 7.75, cat 01 and discount 0.5
	; mov PUR_OBJ[4], 0000011111111000b			;product with list_price 1.75, cat 11 and discount 0.875
	; mov PUR_OBJ[6], 1000101010101000b			;product with list_price 34.5, cat 10 and discount 0.625
	; mov PUR_OBJ[8], 0001110101110000b       	;000111.01 =7.25 / 01 110000 CT:1 DISCOUNT_FACTOR 0.110000=0.75
	; mov PUR_OBJ[10], 0000110111100000b			;000011.01 =3.25 / 11 100000 CT:3 DISCOUNT_FACTOR 0.100000=0.5
	; mov PUR_OBJ[12], 0000011110010000b        	;000001.11 =1.75 / 10 010000 CT:2 DISCOUNT_FACTOR 0.010000=0.25
	; mov PUR_OBJ[14], 1111111111111111b			;list_price = 127.75, cat 11, discount 0.984375

	;-------------------------
	; Solving ITEM B
		mov cl, NUM_PUR
		xor ch, ch								;setting up CX as loop counter
		xor si, si

		next_purchase:
		mov bl, byte ptr PUR_OBJ[si+1]			;storing list_price in BL
		mov al, byte ptr PUR_OBJ[si]			;storing category+discount in AL

		and al, 00111111b						;clean category bits
		mul bl									;AX <-- "dirty" applied_discount
		push cx
		mov cx, 6
		shr ax, cl								;clean applied_discount
		mov myAPPLDISC, al
		pop cx
		; here AL holds 6+2f bits for applied_discount
		; while BL holds 6+2f bits for list_price

		sub bl, al								;compute net_price
		mov myNET, bl

		; computing tax due
		mov al, byte ptr PUR_OBJ[si]
		push cx
		mov cx, 6
		shr al, cl								;read category -> used for later computations
		pop cx
		xor ah, ah
		mov di, ax								; DI <-- category

		mov al, CAT_TAX[di]						;access the category array to read tax percentage
		and al, 00011111b						;clean tax percentage (4+1f)
		;AL holds tax%
		;BL holds net_price
		mul bl									;AX <- tax_due (6+3f)
		push cx
		mov cx, 3
		shr ax, cl								; return to 6+2f form
		pop cx
		;now the division by 100 & truncation
		mov bl, 100
		div bl									; AL = Quotient | AH = Residual
		shl al, 1
		shl al, 1								; AL = keep 6 LSB of integer (cannot be more!)
		; Dividing the residual by 25, to compute tre fractional bits to add to AL
		push cx
		mov cl, al
		xchg ah, al
		xor ah, ah								; Get residual in AX
		mov bl, 25
		div bl									; AL = Quotient which is the fractional representation of the previous residual
		add cl, al
		mov al, cl								;AL has got the proper 6+2 tax_due
		pop cx
		mov myTAX, al

		;computation of final_price
		mov bl, myNET
		add al, bl								;final_price=net_price+tax_due
		mov myFINAL, al

		;compute totals
		mov al, myAPPLDISC
		xor ah, ah
		add totAPPLDISC, ax						;accumulate on the total of applied_discount
		mov al, myNET
		xor ah, ah
		add totNET, ax							;accumulate on the total of net_price
		mov al, myTAX
		xor ah, ah
		add totTAX, ax							;accumulate on the total of tax_due
		mov al, myFINAL
		xor ah, ah
		add totFINAL, ax						;accumulate on the total of final_price

		; -----------------
		; Printing outputs
		; -----------------
		call print_object_costs

		inc si
		inc si
		dec cx
		cmp cx, 0
		jne next_purchase

		; -----------------
		; Printing outputs
		; -----------------
		call print_totals
	; END OF ITEM B
	;-----------------------

	; Bonus Item
	;-----------------------
	call bonus_item

	; -----------------
	; Printing outputs
	; -----------------
	jmp stop_program
MAIN ENDP
; -----------------
; Procedures
; -----------------
new_line PROC
	; Print new line and carriage return
	push ax										; save registers
	push bx										; save registers
	push cx										; save registers
	push dx										; save registers

	mov ah,02h									; prepare ah for character output
	mov dl,0dh 									; dl = carriage return
	int 21h										; call interrupt
	mov dl,0ah									; dl = line feed
	int 21h										; call interrupt

	pop dx									; retrieve registers
	pop cx									; retrieve registers
	pop bx									; retrieve registers
	pop ax									; retrieve registers
	ret
new_line ENDP

itoa PROC NEAR
	; Prints any positive 16-bit [0 - 65535]
	; Value to print is stored in stack
	push bp
	mov bp, sp
	push ax
	push cx
	push dx
	push si
	mov ax, [bp+4]			; bx has the value to be printed
	mov si, 10				; i will have to divide by 10 each time, until the quotient is not zero
	mov cx, 0
	loop_divide_10:
	inc cx					; keep track of how many digits we have
	mov dx, 0				; i know the number i want to print is on 16 bits, but the division by 10 may not fit 8 bits, so 32/16 is required
	div si					; quotient in ax, residual in dx. residual is [0-9], so it is in dl
	mov dh, 0
	add dl, 30h				; number to ascii code
	push dx					; store ascii code in stack
	cmp ax, 0				; if ax = 0, this was the last digit
	je loop_print_number
	jmp loop_divide_10
	loop_print_number:
	pop ax
	mov dl, al
	mov ah, 02h
	int 21h
	loop loop_print_number
	pop si
	pop dx
	pop cx
	pop ax
	pop bp
	ret
itoa ENDP

atoi PROC NEAR
	; reads any positive value into a 16-bit integer [0 - 65535]
	; params: pointer to the buffer, from which i read how many chars were read
	; 1 char = 1 digit
	push bp
	mov bp, sp
	push ax
	push bx
	push cx
	push dx
	push si
	push di
	xor ax, ax
	xor bx, bx
	xor cx, cx
	xor dx, dx
	mov si, [bp+4]			; in main: lea dx, buf ---- push dx
	mov cl, [si+1]			; characters actually read
	inc si
	add si, cx				; place pointer to last digit
	mov di, 1				; will store the power of 10
	loop_multiply:
	xor ax, ax
	mov al, [si]			; put character in dl
	sub al, 30h				; make it a number
	mul di 					; result in dx:ax, but we know we only care about ax
	add bx, ax				; temporarily store the result in bx
	mov ax, di
	shl ax, 1
	shl di, 1
	shl di, 1
	shl di, 1
	add di, ax				; di = di * 10
	dec si					; move to previous digit
	loop loop_multiply		; based on cx, which has the characters actually read
	mov [bp+4], bx
	pop di
	pop si
	pop dx
	pop cx
	pop bx
	pop ax
	pop bp
	ret
atoi ENDP

print_object_costs PROC
	push ax									; save registers
	push bx									; save registers
	push cx									; save registers
	push dx									; save registers
	call new_line
		; ----------------------
		; Print applied_discount
		; ----------------------
		mov ah,09h 							; prepare ah for string output
		mov dx, offset STRING_APPL_DISC		; prepare dx with offset of string to display
		int 21h								; call interrupt
		mov al, myAPPLDISC					; AL = iiiiii.ff
		xor ah, ah
		push ax
		call print_a_byte
		pop ax

	call new_line

		; ----------------------
		; Print net_price
		; ----------------------
		mov ah,09h 							; prepare ah for string output
		mov dx, offset STRING_NET_PRICE		; prepare dx with offset of string to display
		int 21h								; call interrupt
		mov al, myNET						; AL = iiiiii.ff
		xor ah, ah
		push ax
		call print_a_byte
		pop ax

	call new_line

		; ----------------------
		; Print tax_due
		; ----------------------
		mov ah,09h 							; prepare ah for string output
		mov dx, offset STRING_TAX_DUE		; prepare dx with offset of string to display
		int 21h								; call interrupt
		mov al, myTAX						; AL = iiiiii.ff
		xor ah, ah
		push ax
		call print_a_byte
		pop ax

	call new_line

		; ----------------------
		; Print final_price
		; ----------------------
		mov ah,09h 							; prepare ah for string output
		mov dx, offset STRING_FINAL_PRICE	; prepare dx with offset of string to display
		int 21h								; call interrupt
		mov al, myFINAL						; AL = iiiiii.ff
		xor ah, ah
		push ax
		call print_a_byte
		pop ax

	call new_line

	pop dx									; retrieve registers
	pop cx									; retrieve registers
	pop bx									; retrieve registers
	pop ax									; retrieve registers
	ret
print_object_costs ENDP

print_a_byte PROC
	push bp
	mov bp, sp
	push ax
	push cx
	push dx
	push si
	mov ax, [bp+4]			; bx has the value to be printed

	mov dl, al
	xor ah, ah
	; Generate decimal value of integer part
	shr al, 1
	shr al, 1							; AL = 00iiiiii
	; mov BYTE_VALUE_TO_PRINT, al
	; call decimal_print
	xor ah, ah
	push ax
	call itoa
	pop ax

	; Print comma as integer - fractional separator
	push dx
	mov ah, 2
	mov dl, '.'
	int 21h
	pop dx

	; Generate decimal value of fractional part
	and dx, 3							; DX = ff (fractional bits)
	mov ax, dx
	and ax, 1							; AX = less significant fractional bit
	mov bl, 25
	mul bl								; obtain decimal value of less significant fractional bit
	xchg ax, dx							; AX <-> DX
	and ax, 10b							; AX = most significant fractional bit
	shr ax, 1
	mov bl, 50
	mul bl								; obtain decimal value of most significant fractional bit
	add dx, ax							; DX holds decimal value of fractional bits
										; as max value is 125, it fits in 8 bits (AL)
	xor dh, dh
	push dx
	call itoa
	pop dx

	pop si
	pop dx
	pop cx
	pop ax
	pop bp
	ret
print_a_byte ENDP

print_a_word PROC
	push bp
	mov bp, sp
	push ax
	push cx
	push dx
	push si
	mov ax, [bp+4]			; bx has the value to be printed

	mov dx, ax
	; Generate decimal value of integer part
	shr ax, 1
	shr ax, 1							; AX = 00iiiiiiiiiiiiii
	push ax
	call itoa
	pop ax

	; Print comma as integer - fractional separator
	push dx
	mov ah, 2
	mov dl, '.'
	int 21h
	pop dx

	; Generate decimal value of fractional part
	and dx, 3							; DX = ff (fractional bits)
	mov ax, dx
	and ax, 1							; AX = less significant fractional bit
	mov bl, 25
	mul bl								; obtain decimal value of less significant fractional bit
	xchg ax, dx							; AX <-> DX
	and ax, 10b							; AX = most significant fractional bit
	shr ax, 1
	mov bl, 50
	mul bl								; obtain decimal value of most significant fractional bit
	add dx, ax							; DX holds decimal value of fractional bits
										; as max value is 125, it fits in 8 bits (AL)
	push dx
	call itoa
	pop dx

	pop si
	pop dx
	pop cx
	pop ax
	pop bp
	ret
print_a_word ENDP

print_totals PROC
	push ax										; save registers
	push bx										; save registers
	push cx										; save registers
	push dx										; save registers

	call new_line

	mov ah,09h 							; prepare ah for string output
	mov dx, offset STRING_TOTAL_AD		; prepare dx with offset of string to display
	int 21h								; call interrupt
	mov ax, totAPPLDISC
	;First print 14 bits (integer), then the other 2
	push ax
	call print_a_word
	pop ax

	call new_line

	mov ah,09h 							; prepare ah for string output
	mov dx, offset STRING_TOTAL_NP		; prepare dx with offset of string to display
	int 21h								; call interrupt
	mov ax, totNET
	;First print 14 bits (integer), then the other 2
	push ax
	call print_a_word
	pop ax

	call new_line

	mov ah,09h 							; prepare ah for string output
	mov dx, offset STRING_TOTAL_TAX		; prepare dx with offset of string to display
	int 21h								; call interrupt
	mov ax, totTAX
	;First print 14 bits (integer), then the other 2
	push ax
	call print_a_word
	pop ax

	call new_line

	mov ah,09h 							; prepare ah for string output
	mov dx, offset STRING_TOTAL_FINAL	; prepare dx with offset of string to display
	int 21h								; call interrupt
	mov ax, totFINAL
	;First print 14 bits (integer), then the other 2
	push ax
	call print_a_word
	pop ax

	call new_line

	pop dx									; retrieve registers
	pop cx									; retrieve registers
	pop bx									; retrieve registers
	pop ax									; retrieve registers
	ret
print_totals ENDP

ASK_NUM_PUR PROC
	push ax
	push dx

	ask_value_num_pur:
	call new_line
	mov ah,09h 							; prepare ah for string output
	mov dx, offset STRING_ASK_NUM_PUR	; prepare dx with offset of string to display
	int 21h								; call interrupt
	mov ah, 1							; read character from standard input
	int 21h
	sub al, 30h

	cmp al, 8
	ja err1_more_8
	cmp al, 0
	jb err2_less_0
	mov NUM_PUR, al
	jmp got_num_pur

	err1_more_8:
	call new_line
	mov ah,09h 							; prepare ah for string output
	mov dx, offset STRING_ERR1			; prepare dx with offset of string to display
	int 21h								; call interrupt
	jmp ask_value_num_pur

	err2_less_0:
	call new_line
	mov ah,09h 							; prepare ah for string output
	mov dx, offset STRING_ERR2			; prepare dx with offset of string to display
	int 21h								; call interrupt
	jmp ask_value_num_pur

	got_num_pur:
	pop dx
	pop ax
	ret
ASK_NUM_PUR ENDP

PUR_OBJ_FILLING PROC
	push ax									; save registers
	push bx									; save registers
	push cx									; save registers
	push dx									; save registers

	xor ax, ax
	xor bx, bx
	xor cx, cx
	xor dx, dx
	xor si, si
	xor di, di

	mov cl, NUM_PUR

	next_pur_obj:
	push cx
	call new_line
	mov ah,09h 												; prepare ah for string output
	mov dx, offset STRING_ASK_PUR_OBJ_LIST_PRICE			; prepare dx with offset of string to display
	int 21h													; call interrupt
	call new_line

	fill_pur_obj:
	mov dx, offset DST_LIST_PRICE
	mov ah, 0ah
	int 21h													;Input in buffer 10 chars: up to 10 chars, including "." and "CR"

	mov di, 2
	xor ch, ch
	mov cl, DST_LIST_PRICE[di-1]								; CX = number of cells read
	look_for_comma:
	cmp DST_LIST_PRICE[di], '.'									; Compare current char in input with "."
	je comma_found											; If found, previous chars are for the integer
	inc di
	loop look_for_comma

	no_comma: 												; here if no comma was found
	push dx
	call atoi												; atoi uses pointer to buffer as input, output in stack (DX)
	pop dx
	cmp dx, 128
	jae not_good_list_price
	shl dl, 1
	shl dl, 1
	jmp push_to_pur_obj

	not_good_list_price:
	mov ah, 09h
	mov dx, offset STRING_ERR_LIST_PRICE
	int 21h
	jmp fill_pur_obj

	comma_found:											; Here if the current number is a straight integer
	sub di, 2
	mov ax, di
	mov DST_LIST_PRICE[1], al									; Consider as "correct chars read" the index DI
	push dx
	call atoi												; atoi uses pointer to buffer as input, output in stack (DX)
	pop dx
	shl dl, 1
	shl dl, 1												; Leave 2 bits empty for fractional representation
	add di, 3
	mov al, DST_LIST_PRICE[di]									; AL = first fractional digit
	sub al, 30h
	mov bl, 10												; As it is the first digit, multiply by 10 (decine)
	mul bl
	cmp DST_LIST_PRICE[di+1], 0Dh							; Compare with CR symbol
	je no_second_fract_digit
	add al, DST_LIST_PRICE[di+1]								; Second fractional digit, sum to the previous one * 10
	sub al, 30h
	no_second_fract_digit:
	xor ah, ah
	mov bl, 25												; Divide by 25 to round the fractional digits
	div bl													; AL is remainder, represents the rounding in multiples of 25
	add dl, al												; DL now has the 6+2 bits representing the list_price
	cmp dx, 11111111b
	ja not_good_list_price

	push_to_pur_obj:
	mov byte ptr PUR_OBJ[si+1], dl

	call new_line
	mov ah,09h 												; prepare ah for string output
	mov dx, offset STRING_ASK_PUR_OBJ_CATEGORY				; prepare dx with offset of string to display
	int 21h													; call interrupt
	call new_line
	mov dx, offset DST_CATEGORY
	mov ah, 0ah
	int 21h

	push dx
	call atoi
	pop dx

	mov al, dl
	push cx
	mov cx, 6
	shl al, cl												;AL = CC000000
	pop cx

	xor ah, ah
	push ax													; Push AL = CC000000

	call new_line
	mov ah,09h 												; prepare ah for string output
	mov dx, offset STRING_ASK_PUR_OBJ_DISCOUNT				; prepare dx with offset of string to display
	int 21h													; call interrupt
	call new_line
	mov dx, offset DST_DISCOUNT
	mov ah, 0ah
	int 21h

	push cx
	xor di, di
	xor cx, cx
	mov cl, DST_DISCOUNT[1]
	mov di, cx
	cmp cl, 7									; If I read 7 digits, perfect!
	je full_fractional
	mov cx, 7
	sub cx, di
	fill_with_0:
	mov DST_DISCOUNT[di+2], '0'					; Fill other fractional bits to 0
	inc di
	loop fill_with_0
	pop cx

	full_fractional:
	mov dx, 0									; TO be used as accumulator
	mov ax, 0									; To be used to load values from array
	mov al, DST_DISCOUNT[2]
	sub al, '0'									; Convert from ASCII
	mov bl, 100
	mul bl
	add dx, ax									; Get the first digit and mulitply by 100
	xor ax, ax
	mov al, DST_DISCOUNT[3]
	sub al, '0'									; Convert from ASCII
	mov bl, 10
	mul bl
	add dx, ax									; Get the second digit and mulitply by 10
	xor ax, ax
	mov al, DST_DISCOUNT[4]
	sub al, '0'									; Convert from ASCII
	add dx, ax									; DX = value of first three digits of fractionals
	xor ax, ax
	xchg dx, ax									; DX:AX holds the previous DX value
	mov bx, 1000
	mul bx										; DX:AX holds previous DX * 1000
	mov cx, ax									; DX:CX is the new DX:AX

	xor ax, ax
	mov al, DST_DISCOUNT[5]
	sub al, '0'									; Convert from ASCII
	mov bl, 100
	mul bl
	add cx, ax									; Get the fourth digit and mulitply by 100
	adc dx, 0									; Sum carry, if any
	xor ax, ax
	mov al, DST_DISCOUNT[6]
	sub al, '0'									; Convert from ASCII
	mov bl, 10
	mul bl
	add cx, ax									; Get the fifht digit and mulitply by 10
	adc dx, 0									; Sum carry, if any
	xor ax, ax
	mov al, DST_DISCOUNT[7]
	sub al, '0'									; Convert from ASCII
	add cx, ax									; Get the sixth digit
	adc dx, 0									; Sum carry, if any
	mov ax, cx
	; DX:AX holds the 32-bit value identified by the 6 decimal digits in input.
	; My algorithm divides the number in input by 1000, then multiplies it by 64, then divides it by 1000 again.
	; Keep considering only the quotient. Does not provide the maximum precision.
	; An example: 984375 * 64 = 63.000.000 --> 63 --> 111111b
	mov bx, 1000
	div bx										; DX = remainder | AX = quotient
	xor dx, dx
	mov bx, 64
	mul bx
	mov bx, 1000
	div bx										; AL holds the decimal representation of the fractional part (at most 6 LSB!)

	mov bx, ax									; Move this result to BX
	pop ax										; Recover old AL = CC000000
	add ax, bx									; AL holds the correct TAG for OBJ_PUR

	push_to_pur_obj_2:
	mov byte ptr PUR_OBJ[si], al

	pop cx
	add si, 2
	dec cx
	cmp cx, 0
	jne next_pur_obj

	pop dx									; retrieve registers
	pop cx									; retrieve registers
	pop bx									; retrieve registers
	pop ax									; retrieve registers
	ret
PUR_OBJ_FILLING ENDP

BONUS_ITEM PROC
	push ax									; save registers
	push bx									; save registers
	push cx									; save registers
	push dx									; save registers

	xor ax, ax
	xor bx, bx
	xor cx, cx
	xor dx, dx
	xor si, si
	xor di, di

	mov cl, NUM_PUR
	xor ch, ch
	cmp cl, 0
	je no_items_bonus

	next_purchase_bonus:
	xor bx, bx
	mov ax, PUR_OBJ[si]
	push cx
	mov cx, 6
	shr al, cl								;get category into al
	pop cx
	xor ah, ah
	mov di, ax								;use category in DI to access in arrays for categories
	shl di, 1								; used as index on word arrays - needs to be doubled

	mov bl, byte ptr PUR_OBJ[si+1]				;storing list_price in BL
	mov al, byte ptr PUR_OBJ[si]				;storing category+discount in AL

	and al, 00111111b							;clean category bits
	mul bl										;AX <-- "dirty" applied_discount
	push cx
	mov cx, 6
	shr ax, cl									;clean applied_discount
	pop cx
	; here AL holds 6+2f bits for applied_discount
	; while BL holds 6+2f bits for list_price
	add byte ptr totAPPLDISC_CAT[di], al
	adc byte ptr totAPPLDISC_CAT[di+1], 0		;sum to total_CAT the current applDiscount

	sub bl, al									;bl<--current net price

	add byte ptr totNETPRICE_CAT[di], bl
	adc byte ptr totNETPRICE_CAT[di+1], 0		;accumulate on totNETPRICE_CAT

	inc si
	inc si
	loop next_purchase_bonus

	mov cx, 4
	mov di, 0

	next_category:
		call new_line
		mov ah, 09h
		mov dx, offset STRING_CAT
		int 21h
		call new_line

		mov ah,09h 							; prepare ah for string output
		mov dx, offset STRING_APPL_DISC		; prepare dx with offset of string to display
		int 21h								; call interrupt
		mov ax, totAPPLDISC_CAT[di]			; AX = iiiiiiiiiiiiii.ff
		push ax
		call print_a_word
		pop ax
		call new_line
		mov ah,09h 							; prepare ah for string output
		mov dx, offset STRING_NET_PRICE		; prepare dx with offset of string to display
		int 21h								; call interrupt
		mov ax, totNETPRICE_CAT[di]			; AL = iiiiii.ff
		push ax
		call print_a_word
		pop ax
		call new_line
	inc di
	inc di
	loop next_category

	no_items_bonus:
	;nothing to do here
	nop

	pop dx									; retrieve registers
	pop cx									; retrieve registers
	pop bx									; retrieve registers
	pop ax									; retrieve registers
	ret
BONUS_ITEM ENDP

stop_program:
.EXIT
END
