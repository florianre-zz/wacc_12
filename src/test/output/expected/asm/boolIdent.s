
.text

.global main
main:
	PUSH {lr}
	SUB sp, sp, #2
	MOV r4, #1
	STRB r4, [sp, #1]
	LDRSB r4, [sp, #1]
	STRB r4, [sp]
	ADD sp, sp, #2
	LDR r0, =0
	POP {pc}
	.ltorg
