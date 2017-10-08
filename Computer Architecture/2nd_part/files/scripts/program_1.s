.data
myV: .word 1, 10, 3, 4, 8, 6, 7, 8, 15, 2 	; 64-bit integers
result:  .space 8 			; 8 empty bytes for result

.code
ld R2, myV(R0) 				; first item as current maximum
daddui R1, R0, 0			; R1 <- 0
daddui R5, R0, 80			; 10 values , each 64 bits
loop: 						; from the second to the last item
daddui R1, R1, 8 			; get ready for next iteration
slt R4, R1, R5 				; R4 = 1 if R1 < 80, otherwise R4 = 0
beqz R4, end 				; if R4 = 0 jump to end

ld R3, myV(R1)				; load next item
slt R4, R2, R3 				; R4 = 1 if R2 < R3, otherwise R4 = 0
beqz R4, loop 				; if R4 = 0 jump to loop

dadd R2, R0, R3				; we found a new maximum value
J loop
end:
sd R2, result(R0) 			; store the global maximum in result var

HALT
