
.text

.global main
main:
	PUSH {lr}
	SUB sp, sp, #9
	LDR r4, =99
	STR r4, [sp, #5]
	LDR r4, [sp, #5]
	STRB r4, [sp, #4]
	LDRSB r4, [sp, #4]
	STR r4, [sp]
	ADD sp, sp, #9
	LDR r0, =0
	POP {pc}
	.ltorg
