
.data

msg_0:
	.word 61
	.ascii	"This program calculates the nth fibonacci number iteratively."
msg_1:
	.word 42
	.ascii	"Please enter n (should not be too large): "
msg_2:
	.word 15
	.ascii	"The input n is "
msg_3:
	.word 28
	.ascii	"The nth fibonacci number is "
msg_4:
	.word 5
	.ascii	"%.*s\0"
msg_5:
	.word 1
	.ascii	"\0"
msg_6:
	.word 3
	.ascii	"%d\0"
msg_7:
	.word 3
	.ascii	"%d\0"
msg_8:
	.word 82
	.ascii	"OverflowError: the result is too small/large to store in a 4-byte signed-integer.\n"

.text

.global main
main:
	PUSH {lr}
	SUB sp, sp, #16
	LDR r4, =msg_0
	MOV r0, r4
	BL p_print_string
	BL p_print_ln
	LDR r4, =msg_1
	MOV r0, r4
	BL p_print_string
	LDR r4, =0
	STR r4, [sp, #12]
	ADD r4, sp, #12
	MOV r0, r4
	BL p_read_int
	LDR r4, =msg_2
	MOV r0, r4
	BL p_print_string
	LDR r4, [sp, #12]
	MOV r0, r4
	BL p_print_int
	BL p_print_ln
	LDR r4, =msg_3
	MOV r0, r4
	BL p_print_string
	LDR r4, =0
	STR r4, [sp, #8]
	LDR r4, =1
	STR r4, [sp, #4]
	LDR r4, =0
	STR r4, [sp]
	B L0
L1:
	LDR r4, [sp, #8]
	STR r4, [sp]
	LDR r4, [sp, #4]
	STR r4, [sp, #8]
	LDR r4, [sp]
	LDR r5, [sp, #4]
	ADDS r4, r4, r5
	BLVS p_throw_overflow_error
	STR r4, [sp, #4]
	LDR r4, [sp, #12]
	LDR r5, =1
	SUBS r4, r4, r5
	BLVS p_throw_overflow_error
	STR r4, [sp, #12]
L0:
	LDR r4, [sp, #12]
	LDR r5, =0
	CMP r4, r5
	MOVGT r4, #1
	MOVLE r4, #0
	CMP r4, #1
	BEQ L1
	LDR r4, [sp, #8]
	MOV r0, r4
	BL p_print_int
	BL p_print_ln
	ADD sp, sp, #16
	LDR r0, =0
	POP {pc}
	.ltorg
p_print_string:
	PUSH {lr}
	LDR r1, [r0]
	ADD r2, r0, #4
	LDR r0, =msg_4
	ADD r0, r0, #4
	BL printf
	MOV r0, #0
	BL fflush
	POP {pc}
p_print_ln:
	PUSH {lr}
	LDR r0, =msg_5
	ADD r0, r0, #4
	BL puts
	MOV r0, #0
	BL fflush
	POP {pc}
p_read_int:
	PUSH {lr}
	MOV r1, r0
	LDR r0, =msg_6
	ADD r0, r0, #4
	BL scanf
	POP {pc}
p_print_int:
	PUSH {lr}
	MOV r1, r0
	LDR r0, =msg_7
	ADD r0, r0, #4
	BL printf
	MOV r0, #0
	BL fflush
	POP {pc}
p_throw_overflow_error:
	LDR r0, =msg_8
	BL p_throw_runtime_error
p_throw_runtime_error:
	BL p_print_string
	MOV r0, #-1
	BL exit
