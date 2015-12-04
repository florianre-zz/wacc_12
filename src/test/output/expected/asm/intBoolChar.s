
.text

.global main
main:
	PUSH {lr}
	SUB sp, sp, #8
	LDR r4, =42
	STR r4, [sp, #4]
	LDR r4, =71
	STRB r4, [sp, #3]
	MOV r4, #1
	STRB r4, [sp, #2]
	MOV r4, #'a'
	STRB r4, [sp, #1]
	LDRSB r4, [sp, #1]
	STRB r4, [sp]
	ADD sp, sp, #8
	LDR r0, =0
	POP {pc}
	.ltorg
