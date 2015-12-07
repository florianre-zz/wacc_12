
.text

.global main
main:
	PUSH {lr}
	SUB sp, sp, #6
	LDR r4, =42
	STR r4, [sp, #2]
	MOV r4, #1
	STRB r4, [sp, #1]
	MOV r4, #1
	EOR r4, r4, #1
	STRB r4, [sp]
	LDR r4, =3
	MOV r0, r4
	BL exit
	ADD sp, sp, #6
	LDR r0, =0
	POP {pc}
	.ltorg
