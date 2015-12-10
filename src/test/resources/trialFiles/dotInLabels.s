.data

msg_0:
	.word 11
	.ascii	"Hello World"
msg_1:
  .word 5
  .ascii	"%.*s\0"
msg_2:
.word 1
	.ascii	"\0"

.text

.global main
f_f.4.5:
	PUSH {lr}
	LDR r4, =msg_0
	MOV r0, r4
	BL p_print_string
	BL p_print_ln
	MOV r4, #1
	MOV r0, r4
	POP {pc}
	POP {pc}
	.ltorg
main:
	PUSH {lr}
	SUB sp, sp, #1
	BL f_f.4.5
	MOV r4, r0
	STRB r4, [sp]
	ADD sp, sp, #1
	LDR r0, =0
	POP {pc}
	.ltorg
p_print_string:
	PUSH {lr}
	LDR r1, [r0]
	ADD r2, r0, #4
	LDR r0, =msg_1
	ADD r0, r0, #4
	BL printf
	MOV r0, #0
	BL fflush
	POP {pc}
p_print_ln:
	PUSH {lr}
	LDR r0, =msg_2
	ADD r0, r0, #4
	BL puts
	MOV r0, #0
	BL fflush
	POP {pc}
