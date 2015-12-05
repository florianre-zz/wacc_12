
.data

msg_0:
	.word 82
	.ascii	"OverflowError: the result is too small/large to store in a 4-byte signed-integer.\n"
msg_1:
	.word 45
	.ascii	"DivideByZeroError: divide or modulo by zero\n\0"
msg_2:
	.word 5
	.ascii	"%.*s\0"

.text

.global main
main:
	PUSH {lr}
	SUB sp, sp, #8
	LDR r4, =10
	STR r4, [sp, #4]
	LDR r4, [sp, #4]
	LDR r5, =2
	LDR r6, =5
	SMULL r5, r6, r5, r6
	CMP r6, r5, ASR #31
	BLNE p_throw_overflow_error
	ADDS r4, r4, r5
	BLVS p_throw_overflow_error
	LDR r5, =4
	LDR r6, =2
	MOV r0, r5
	MOV r1, r6
	BL p_check_divide_by_zero
	BL __aeabi_idivmod
	MOV r5, r1
	SUBS r4, r4, r5
	BLVS p_throw_overflow_error
	STR r4, [sp]
	ADD sp, sp, #8
	LDR r0, =0
	POP {pc}
	.ltorg
p_throw_overflow_error:
	LDR r0, =msg_0
	BL p_throw_runtime_error
p_check_divide_by_zero:
	PUSH {lr}
	CMP r1, #0
	LDREQ r0, =msg_1
	BLEQ p_throw_runtime_error
	POP {pc}
p_throw_runtime_error:
	BL p_print_string
	MOV r0, #-1
	BL exit
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
