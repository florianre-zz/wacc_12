
.text

.global main
main:
	PUSH {lr}
	SUB sp, sp, #4
	LDR r4, =0
	STR r4, [sp]
	LDR r4, =0
	LDR r5, =0
	CMP r4, r5
	MOVEQ r4, #1
	MOVNE r4, #0
	CMP r4, #0
	BEQ L0
	LDR r4, =22
	STR r4, [sp]
	B L1
L0:
	LDR r4, =44
	STR r4, [sp]
L1:
	ADD sp, sp, #4
	LDR r0, =0
	POP {pc}
	.ltorg
