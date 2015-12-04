
.data

msg_0:
	.word 12
	.ascii	"hello world!"
msg_1:
	.word 3
	.ascii	"Hi!"
msg_2:
	.word 5
	.ascii	"%.*s\0"
msg_3:
	.word 1
	.ascii	"\0"
msg_4:
	.word 44
	.ascii	"ArrayIndexOutOfBoundsError: negative index\n\0"
msg_5:
	.word 45
	.ascii	"ArrayIndexOutOfBoundsError: index too large\n\0"

.text

.global main
main:
	PUSH {lr}
	SUB sp, sp, #4
	LDR r4, =msg_0
	STR r4, [sp]
	LDR r4, [sp]
	MOV r0, r4
	BL p_print_string
	BL p_print_ln
	MOV r4, #'H'
	ADD r5, sp, #0
	LDR r6, =0
	LDR r5, [r5]
	MOV r0, r6
	MOV r1, r5
	BL p_check_array_bounds
	ADD r5, r5, #4
	ADD r5, r5, r6
	STRB r4, [r5]
	LDR r4, [sp]
	MOV r0, r4
	BL p_print_string
	BL p_print_ln
	LDR r4, =msg_1
	STR r4, [sp]
	LDR r4, [sp]
	MOV r0, r4
	BL p_print_string
	BL p_print_ln
	ADD sp, sp, #4
	LDR r0, =0
	POP {pc}
	.ltorg
p_print_string:
	PUSH {lr}
	LDR r1, [r0]
	ADD r2, r0, #4
	LDR r0, =msg_2
	ADD r0, r0, #4
	BL printf
	MOV r0, #0
	BL fflush
	POP {pc}
p_print_ln:
	PUSH {lr}
	LDR r0, =msg_3
	ADD r0, r0, #4
	BL puts
	MOV r0, #0
	BL fflush
	POP {pc}
p_check_array_bounds:
	PUSH {lr}
	CMP r0, #0
	LDRLT r0, =msg_4
	BLLT p_throw_runtime_error
	LDR r1, [r1]
	CMP r0, r1
	LDRCS r0, =msg_5
	BLCS p_throw_runtime_error
	POP {pc}
p_throw_runtime_error:
	BL p_print_string
	MOV r0, #-1
	BL exit
