
.text

.global main
main:
	PUSH {lr}
	SUB sp, sp, #17
	LDR r4, =1
	STR r4, [sp, #13]
	LDR r4, =2
	STR r4, [sp, #9]
	LDR r4, =3
	STR r4, [sp, #5]
	LDR r4, =4
	STR r4, [sp, #1]
	LDR r4, =97
	STRB r4, [sp]
	ADD sp, sp, #17
	LDR r0, =0
	POP {pc}
	.ltorg
