
.data

msg_0:
	.word 12
	.ascii	"Hello World!"
msg_1:
	.word 8
	.ascii	"Goodbye!"

.text

.global main
main:
	PUSH {lr}
	SUB sp, sp, #8
	LDR r4, =0
	STR r4, [sp, #4]
	LDR r4, =5
	STR r4, [sp, #4]
	LDR r4, =msg_0
	STR r4, [sp]
	LDR r4, =msg_1
	STR r4, [sp]
	ADD sp, sp, #8
	LDR r0, =0
	POP {pc}
	.ltorg
