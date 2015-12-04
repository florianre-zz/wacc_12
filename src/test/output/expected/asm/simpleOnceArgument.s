
.text

.global main
f_f:
	PUSH {lr}
	SUB sp, sp, #24
	LDR r4, =0
	STR r4, [sp, #20]
	LDR r4, =0
	STR r4, [sp, #16]
	LDR r4, =0
	STR r4, [sp, #12]
	LDR r4, =0
	STR r4, [sp, #8]
	LDR r4, [sp, #32]
	STR r4, [sp, #4]
	LDR r4, [sp, #36]
	STR r4, [sp]
	LDR r4, [sp, #28]
	MOV r0, r4
	ADD sp, sp, #24
	POP {pc}
	POP {pc}
	.ltorg
main:
	PUSH {lr}
	SUB sp, sp, #4
	LDR r4, =999
	STR r4, [sp, #-4]!
	LDR r4, =666
	STR r4, [sp, #-4]!
	LDR r4, =420
	STR r4, [sp, #-4]!
	BL f_f
	ADD sp, sp, #12
	MOV r4, r0
	STR r4, [sp]
	ADD sp, sp, #4
	LDR r0, =0
	POP {pc}
	.ltorg
