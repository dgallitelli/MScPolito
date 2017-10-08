.data
v1: .double 1,2,3,4,5,6,7,8,9,10
v2: .double 1,2,3,4,5,6,7,8,9,10
v3: .double 1,2,3,4,5,6,7,8,9,10
v4: .double 1,2,3,4,5,6,7,8,9,10
v5: .double 1,2,3,4,5,6,7,8,9,10
v6: .double 1,2,3,4,5,6,7,8,9,10
v7: .double 1,2,3,4,5,6,7,8,9,10
.text

		daddu r1, r0, r0
		daddui r20, r0, 100
loop: l.d f1, v1(r1)
			l.d f2, v2(r1)
			l.d f3, v3(r1)
			l.d f4, v4(r1)
			mul.d f5, f1, f2
			div.d f6, f2, f3
			add.d f7, f4, f1
			s.d f5, v5(r1)
			s.d f6, v6(r1)
			s.d f7, v7(r1)
			daddui r1, r1, 8
			daddi r20, r20, -1
			bnez r20, loop

			HALT
