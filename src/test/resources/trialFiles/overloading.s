
.data

msg_0:
	.word 5
	.ascii "%.*s\0"
msg_1:
	.word 3
	.ascii "1/5"
msg_2:
	.word 1
	.ascii "\0"
msg_3:
	.word 3
	.ascii "2/5"
msg_4:
	.word 3
	.ascii "3/5"
msg_5:
	.word 3
	.ascii "4/5"
msg_6:
	.word 3
	.ascii "5/5"
msg_7:
	.word 1
	.ascii "H"
msg_8:
	.word 1
	.ascii "H"
msg_9:
	.word 1
	.ascii "H"

.text

.global main
f_f.charArr.Pr_int_Pr.:
	PUSH {lr}
	LDR r4, =msg_1
	MOV r0, r4
	BL p_print_string
	BL p_print_ln
	LDR r4, =1
	MOV r0, r4
	POP {pc}
	POP {pc}
	.ltorg
f_f.int.:
	PUSH {lr}
	LDR r4, =msg_3
	MOV r0, r4
	BL p_print_string
	BL p_print_ln
	LDR r4, =1
	MOV r0, r4
	POP {pc}
	POP {pc}
	.ltorg
f_f.int.intArrArr.:
	PUSH {lr}
	LDR r4, =msg_4
	MOV r0, r4
	BL p_print_string
	BL p_print_ln
	MOV r4, #1
	MOV r0, r4
	POP {pc}
	POP {pc}
	.ltorg
f_f.charArr.Pr_int_int.:
	PUSH {lr}
	SUB sp, sp, #4
	LDR r4, =msg_5
	MOV r0, r4
	BL p_print_string
	BL p_print_ln
	LDR r0, =12
	BL malloc
	MOV r4, r0
	LDR r5, =1
	STR r5, [r4, #4]
	LDR r5, =2
	STR r5, [r4, #8]
	LDR r5, =2
	STR r5, [r4]
	STR r4, [sp]
	LDR r4, [sp]
	MOV r0, r4
	ADD sp, sp, #4
	POP {pc}
	POP {pc}
	.ltorg
f_f.charArr.Pr_int_Pr.int.:
	PUSH {lr}
	LDR r4, =msg_6
	MOV r0, r4
	BL p_print_string
	BL p_print_ln
	MOV r4, #'c'
	MOV r0, r4
	POP {pc}
	POP {pc}
	.ltorg
main:
	PUSH {lr}
	SUB sp, sp, #22
	LDR r0, =8
	BL malloc
	MOV r4, r0
	LDR r5, =2
	LDR r0, =4
	BL malloc
	STR r5, [r0]
	STR r0, [r4]
	LDR r5, =3
	LDR r0, =4
	BL malloc
	STR r5, [r0]
	STR r0, [r4, #4]
	STR r4, [sp, #18]
	LDR r0, =8
	BL malloc
	MOV r4, r0
	LDR r5, =1
	LDR r0, =4
	BL malloc
	STR r5, [r0]
	STR r0, [r4]
	LDR r5, [sp, #18]
	LDR r0, =4
	BL malloc
	STR r5, [r0]
	STR r0, [r4, #4]
	STR r4, [sp, #14]
	LDR r4, [sp, #14]
	STR r4, [sp, #-4]!
	LDR r4, =msg_7
	STR r4, [sp, #-4]!
	BL f_f.charArr.Pr_int_Pr.
	ADD sp, sp, #8
	MOV r4, r0
	STR r4, [sp, #10]
	LDR r4, [sp, #10]
	STR r4, [sp, #-4]!
	BL f_f.int.
	ADD sp, sp, #4
	MOV r4, r0
	STR r4, [sp, #10]
	LDR r0, =8
	BL malloc
	MOV r4, r0
	LDR r5, [sp, #10]
	STR r5, [r4, #4]
	LDR r5, =1
	STR r5, [r4]
	STR r4, [sp, #6]
	LDR r0, =8
	BL malloc
	MOV r4, r0
	LDR r5, [sp, #6]
	STR r5, [r4, #4]
	LDR r5, =1
	STR r5, [r4]
	STR r4, [sp, #2]
	LDR r4, [sp, #2]
	STR r4, [sp, #-4]!
	LDR r4, [sp, #14]
	STR r4, [sp, #-4]!
	BL f_f.int.intArrArr.
	ADD sp, sp, #8
	MOV r4, r0
	STRB r4, [sp, #1]
	LDR r4, [sp, #18]
	STR r4, [sp, #-4]!
	LDR r4, =msg_8
	STR r4, [sp, #-4]!
	BL f_f.charArr.Pr_int_int.
	ADD sp, sp, #8
	MOV r4, r0
	STR r4, [sp, #6]
	LDR r4, [sp, #10]
	STR r4, [sp, #-4]!
	LDR r4, [sp, #18]
	STR r4, [sp, #-4]!
	LDR r4, =msg_9
	STR r4, [sp, #-4]!
	BL f_f.charArr.Pr_int_Pr.int.
	ADD sp, sp, #12
	MOV r4, r0
	STRB r4, [sp]
	ADD sp, sp, #22
	LDR r0, =0
	POP {pc}
	.ltorg
p_print_string:
	PUSH {lr}
	LDR r1, [r0]
	ADD r2, r0, #4
	LDR r0, =msg_0
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

