
.data

msg_0:
	.word 44
	.ascii "ArrayIndexOutOfBoundsError: negative index\n\0"
msg_1:
	.word 45
	.ascii "ArrayIndexOutOfBoundsError: index too large\n\0"
msg_2:
	.word 5
	.ascii "%.*s\0"
msg_3:
	.word 82
	.ascii "OverflowError: the result is too small/large to store in a 4-byte signed-integer.\n"
msg_4:
	.word 50
	.ascii "NullReferenceError: dereference a null reference\n\0"
msg_5:
	.word 0
	.ascii ""
msg_6:
	.word 1
	.ascii "\0"
msg_7:
	.word 45
	.ascii "DivideByZeroError: divide or modulo by zero\n\0"
msg_8:
	.word 3
	.ascii "%d\0"
msg_9:
	.word 43
	.ascii "==========================================="
msg_10:
	.word 43
	.ascii "========== Hash Table Program ============="
msg_11:
	.word 43
	.ascii "==========================================="
msg_12:
	.word 43
	.ascii "=                                         ="
msg_13:
	.word 43
	.ascii "= Please choose the following options:    ="
msg_14:
	.word 43
	.ascii "=                                         ="
msg_15:
	.word 43
	.ascii "= a: insert an integer                    ="
msg_16:
	.word 43
	.ascii "= b: find an integer                      ="
msg_17:
	.word 43
	.ascii "= c: count the integers                   ="
msg_18:
	.word 43
	.ascii "= d: print all integers                   ="
msg_19:
	.word 43
	.ascii "= e: remove an integer                    ="
msg_20:
	.word 43
	.ascii "= f: remove all integers                  ="
msg_21:
	.word 43
	.ascii "= g: exit                                 ="
msg_22:
	.word 43
	.ascii "=                                         ="
msg_23:
	.word 43
	.ascii "==========================================="
msg_24:
	.word 15
	.ascii "Your decision: "
msg_25:
	.word 4
	.ascii " %c\0"
msg_26:
	.word 18
	.ascii "You have entered: "
msg_27:
	.word 36
	.ascii " which is invalid, please try again."
msg_28:
	.word 18
	.ascii "You have entered: "
msg_29:
	.word 35
	.ascii "Please enter an integer to insert: "
msg_30:
	.word 43
	.ascii "Successfully insert it. The integer is new."
msg_31:
	.word 51
	.ascii "The integer is already there. No insertion is made."
msg_32:
	.word 33
	.ascii "Please enter an integer to find: "
msg_33:
	.word 17
	.ascii "Find the integer."
msg_34:
	.word 25
	.ascii "The integer is not found."
msg_35:
	.word 24
	.ascii "There is only 1 integer."
msg_36:
	.word 10
	.ascii "There are "
msg_37:
	.word 10
	.ascii " integers."
msg_38:
	.word 23
	.ascii "Here are the integers: "
msg_39:
	.word 35
	.ascii "Please enter an integer to remove: "
msg_40:
	.word 29
	.ascii "The integer has been removed."
msg_41:
	.word 25
	.ascii "The integer is not found."
msg_42:
	.word 31
	.ascii "All integers have been removed."
msg_43:
	.word 13
	.ascii "Goodbye Human"
msg_44:
	.word 23
	.ascii "Error: unknown choice ("
msg_45:
	.word 1
	.ascii ")"

.text

.global main
f_init.Pr_int_PrArr.:
	PUSH {lr}
	SUB sp, sp, #8
	LDR r4, [sp, #12]
	LDR r4, [r4]
	STR r4, [sp, #4]
	LDR r4, =0
	STR r4, [sp]
	B predicate_1
while_body_1:
	LDR r4, =0
	LDR r5, [sp, #12]
	LDR r6, [sp]
	MOV r0, r6
	MOV r1, r5
	BL p_check_array_bounds
	ADD r5, r5, #4
	ADD r5, r5, r6, LSL #2
	STR r4, [r5]
	LDR r4, [sp]
	LDR r5, =1
	ADDS r4, r4, r5
	BLVS p_throw_overflow_error
	STR r4, [sp]
predicate_1:
	LDR r4, [sp]
	LDR r5, [sp, #4]
	CMP r4, r5
	MOVLT r4, #1
	MOVGE r4, #0
	CMP r4, #1
	BEQ while_body_1
	MOV r4, #1
	MOV r0, r4
	ADD sp, sp, #8
	POP {pc}
	POP {pc}
	.ltorg
f_contain.Pr_int_PrArr.int.:
	PUSH {lr}
	SUB sp, sp, #8
	LDR r4, [sp, #16]
	STR r4, [sp, #-4]!
	LDR r4, [sp, #16]
	STR r4, [sp, #-4]!
	BL f_calculateIndex.Pr_int_PrArr.int.
	ADD sp, sp, #8
	MOV r4, r0
	STR r4, [sp, #4]
	LDR r4, [sp, #16]
	STR r4, [sp, #-4]!
	LDR r4, [sp, #16]
	LDR r5, [sp, #8]
	MOV r0, r5
	MOV r1, r4
	BL p_check_array_bounds
	ADD r4, r4, #4
	ADD r4, r4, r5, LSL #2
	LDR r4, [r4]
	STR r4, [sp, #-4]!
	BL f_findNode.Pr_int_Pr.int.
	ADD sp, sp, #8
	MOV r4, r0
	STR r4, [sp]
	LDR r4, [sp]
	LDR r5, =0
	CMP r4, r5
	MOVEQ r4, #0
	MOVNE r4, #1
	MOV r0, r4
	ADD sp, sp, #8
	POP {pc}
	POP {pc}
	.ltorg
f_insertIfNotContain.Pr_int_PrArr.int.:
	PUSH {lr}
	SUB sp, sp, #8
	LDR r4, [sp, #16]
	STR r4, [sp, #-4]!
	LDR r4, [sp, #16]
	STR r4, [sp, #-4]!
	BL f_calculateIndex.Pr_int_PrArr.int.
	ADD sp, sp, #8
	MOV r4, r0
	STR r4, [sp, #4]
	LDR r4, [sp, #16]
	STR r4, [sp, #-4]!
	LDR r4, [sp, #16]
	LDR r5, [sp, #8]
	MOV r0, r5
	MOV r1, r4
	BL p_check_array_bounds
	ADD r4, r4, #4
	ADD r4, r4, r5, LSL #2
	LDR r4, [r4]
	STR r4, [sp, #-4]!
	BL f_findNode.Pr_int_Pr.int.
	ADD sp, sp, #8
	MOV r4, r0
	STR r4, [sp]
	LDR r4, [sp]
	LDR r5, =0
	CMP r4, r5
	MOVEQ r4, #0
	MOVNE r4, #1
	CMP r4, #0
	BEQ else_1
	MOV r4, #0
	MOV r0, r4
	ADD sp, sp, #8
	POP {pc}
	B fi_1
else_1:
	SUB sp, sp, #4
	LDR r0, =8
	BL malloc
	MOV r4, r0
	LDR r5, [sp, #20]
	LDR r0, =4
	BL malloc
	STR r5, [r0]
	STR r0, [r4]
	LDR r5, [sp, #16]
	LDR r6, [sp, #8]
	MOV r0, r6
	MOV r1, r5
	BL p_check_array_bounds
	ADD r5, r5, #4
	ADD r5, r5, r6, LSL #2
	LDR r5, [r5]
	LDR r0, =4
	BL malloc
	STR r5, [r0]
	STR r0, [r4, #4]
	STR r4, [sp]
	LDR r4, [sp]
	LDR r5, [sp, #16]
	LDR r6, [sp, #8]
	MOV r0, r6
	MOV r1, r5
	BL p_check_array_bounds
	ADD r5, r5, #4
	ADD r5, r5, r6, LSL #2
	STR r4, [r5]
	MOV r4, #1
	MOV r0, r4
	ADD sp, sp, #12
	POP {pc}
	ADD sp, sp, #4
fi_1:
	POP {pc}
	.ltorg
f_remove.Pr_int_PrArr.int.:
	PUSH {lr}
	SUB sp, sp, #8
	LDR r4, [sp, #16]
	STR r4, [sp, #-4]!
	LDR r4, [sp, #16]
	STR r4, [sp, #-4]!
	BL f_calculateIndex.Pr_int_PrArr.int.
	ADD sp, sp, #8
	MOV r4, r0
	STR r4, [sp, #4]
	LDR r4, [sp, #16]
	STR r4, [sp, #-4]!
	LDR r4, [sp, #16]
	LDR r5, [sp, #8]
	MOV r0, r5
	MOV r1, r4
	BL p_check_array_bounds
	ADD r4, r4, #4
	ADD r4, r4, r5, LSL #2
	LDR r4, [r4]
	STR r4, [sp, #-4]!
	BL f_findNode.Pr_int_Pr.int.
	ADD sp, sp, #8
	MOV r4, r0
	STR r4, [sp]
	LDR r4, [sp]
	LDR r5, =0
	CMP r4, r5
	MOVEQ r4, #1
	MOVNE r4, #0
	CMP r4, #0
	BEQ else_2
	MOV r4, #0
	MOV r0, r4
	ADD sp, sp, #8
	POP {pc}
	B fi_2
else_2:
	LDR r4, [sp]
	STR r4, [sp, #-4]!
	LDR r4, [sp, #16]
	LDR r5, [sp, #8]
	MOV r0, r5
	MOV r1, r4
	BL p_check_array_bounds
	ADD r4, r4, #4
	ADD r4, r4, r5, LSL #2
	LDR r4, [r4]
	STR r4, [sp, #-4]!
	BL f_removeNode.Pr_int_Pr.Pr_int_Pr.
	ADD sp, sp, #8
	MOV r4, r0
	LDR r5, [sp, #12]
	LDR r6, [sp, #4]
	MOV r0, r6
	MOV r1, r5
	BL p_check_array_bounds
	ADD r5, r5, #4
	ADD r5, r5, r6, LSL #2
	STR r4, [r5]
	MOV r4, #1
	MOV r0, r4
	ADD sp, sp, #8
	POP {pc}
fi_2:
	POP {pc}
	.ltorg
f_removeAll.Pr_int_PrArr.:
	PUSH {lr}
	SUB sp, sp, #8
	LDR r4, [sp, #12]
	LDR r4, [r4]
	STR r4, [sp, #4]
	LDR r4, =0
	STR r4, [sp]
	B predicate_2
while_body_2:
	SUB sp, sp, #4
	LDR r4, [sp, #16]
	LDR r5, [sp, #4]
	MOV r0, r5
	MOV r1, r4
	BL p_check_array_bounds
	ADD r4, r4, #4
	ADD r4, r4, r5, LSL #2
	LDR r4, [r4]
	STR r4, [sp]
	B predicate_3
while_body_3:
	SUB sp, sp, #4
	LDR r4, [sp, #4]
	MOV r0, r4
	BL p_check_null_pointer
	LDR r4, [r4, #4]
	LDR r4, [r4]
	STR r4, [sp]
	LDR r4, [sp, #4]
	MOV r0, r4
	BL p_free_pair
	LDR r4, [sp]
	STR r4, [sp, #4]
	ADD sp, sp, #4
predicate_3:
	LDR r4, [sp]
	LDR r5, =0
	CMP r4, r5
	MOVEQ r4, #0
	MOVNE r4, #1
	CMP r4, #1
	BEQ while_body_3
	LDR r4, =0
	LDR r5, [sp, #16]
	LDR r6, [sp, #4]
	MOV r0, r6
	MOV r1, r5
	BL p_check_array_bounds
	ADD r5, r5, #4
	ADD r5, r5, r6, LSL #2
	STR r4, [r5]
	LDR r4, [sp, #4]
	LDR r5, =1
	ADDS r4, r4, r5
	BLVS p_throw_overflow_error
	STR r4, [sp, #4]
	ADD sp, sp, #4
predicate_2:
	LDR r4, [sp]
	LDR r5, [sp, #4]
	CMP r4, r5
	MOVLT r4, #1
	MOVGE r4, #0
	CMP r4, #1
	BEQ while_body_2
	MOV r4, #1
	MOV r0, r4
	ADD sp, sp, #8
	POP {pc}
	POP {pc}
	.ltorg
f_count.Pr_int_PrArr.:
	PUSH {lr}
	SUB sp, sp, #12
	LDR r4, [sp, #16]
	LDR r4, [r4]
	STR r4, [sp, #8]
	LDR r4, =0
	STR r4, [sp, #4]
	LDR r4, =0
	STR r4, [sp]
	B predicate_4
while_body_4:
	SUB sp, sp, #4
	LDR r4, [sp, #20]
	LDR r5, [sp, #4]
	MOV r0, r5
	MOV r1, r4
	BL p_check_array_bounds
	ADD r4, r4, #4
	ADD r4, r4, r5, LSL #2
	LDR r4, [r4]
	STR r4, [sp, #-4]!
	BL f_countNodes.Pr_int_Pr.
	ADD sp, sp, #4
	MOV r4, r0
	STR r4, [sp]
	LDR r4, [sp, #8]
	LDR r5, [sp]
	ADDS r4, r4, r5
	BLVS p_throw_overflow_error
	STR r4, [sp, #8]
	LDR r4, [sp, #4]
	LDR r5, =1
	ADDS r4, r4, r5
	BLVS p_throw_overflow_error
	STR r4, [sp, #4]
	ADD sp, sp, #4
predicate_4:
	LDR r4, [sp]
	LDR r5, [sp, #8]
	CMP r4, r5
	MOVLT r4, #1
	MOVGE r4, #0
	CMP r4, #1
	BEQ while_body_4
	LDR r4, [sp, #4]
	MOV r0, r4
	ADD sp, sp, #12
	POP {pc}
	POP {pc}
	.ltorg
f_printAll.Pr_int_PrArr.:
	PUSH {lr}
	SUB sp, sp, #8
	LDR r4, [sp, #12]
	LDR r4, [r4]
	STR r4, [sp, #4]
	LDR r4, =0
	STR r4, [sp]
	B predicate_5
while_body_5:
	SUB sp, sp, #1
	LDR r4, [sp, #13]
	LDR r5, [sp, #1]
	MOV r0, r5
	MOV r1, r4
	BL p_check_array_bounds
	ADD r4, r4, #4
	ADD r4, r4, r5, LSL #2
	LDR r4, [r4]
	STR r4, [sp, #-4]!
	BL f_printAllNodes.Pr_int_Pr.
	ADD sp, sp, #4
	MOV r4, r0
	STRB r4, [sp]
	LDR r4, [sp, #1]
	LDR r5, =1
	ADDS r4, r4, r5
	BLVS p_throw_overflow_error
	STR r4, [sp, #1]
	ADD sp, sp, #1
predicate_5:
	LDR r4, [sp]
	LDR r5, [sp, #4]
	CMP r4, r5
	MOVLT r4, #1
	MOVGE r4, #0
	CMP r4, #1
	BEQ while_body_5
	LDR r4, =msg_5
	MOV r0, r4
	BL p_print_string
	BL p_print_ln
	MOV r4, #1
	MOV r0, r4
	ADD sp, sp, #8
	POP {pc}
	POP {pc}
	.ltorg
f_calculateIndex.Pr_int_PrArr.int.:
	PUSH {lr}
	SUB sp, sp, #4
	LDR r4, [sp, #8]
	LDR r4, [r4]
	STR r4, [sp]
	LDR r4, [sp, #12]
	LDR r5, [sp]
	MOV r0, r4
	MOV r1, r5
	BL p_check_divide_by_zero
	BL __aeabi_idivmod
	MOV r4, r1
	MOV r0, r4
	ADD sp, sp, #4
	POP {pc}
	POP {pc}
	.ltorg
f_findNode.Pr_int_Pr.int.:
	PUSH {lr}
	B predicate_6
while_body_6:
	SUB sp, sp, #4
	LDR r4, [sp, #8]
	MOV r0, r4
	BL p_check_null_pointer
	LDR r4, [r4]
	LDR r4, [r4]
	STR r4, [sp]
	LDR r4, [sp]
	LDR r5, [sp, #12]
	CMP r4, r5
	MOVEQ r4, #1
	MOVNE r4, #0
	CMP r4, #0
	BEQ else_3
	LDR r4, [sp, #8]
	MOV r0, r4
	ADD sp, sp, #4
	POP {pc}
	B fi_3
else_3:
	LDR r4, [sp, #8]
	MOV r0, r4
	BL p_check_null_pointer
	LDR r4, [r4, #4]
	LDR r4, [r4]
	STR r4, [sp, #8]
fi_3:
	ADD sp, sp, #4
predicate_6:
	LDR r4, [sp, #4]
	LDR r5, =0
	CMP r4, r5
	MOVEQ r4, #0
	MOVNE r4, #1
	CMP r4, #1
	BEQ while_body_6
	LDR r4, =0
	MOV r0, r4
	POP {pc}
	POP {pc}
	.ltorg
f_removeNode.Pr_int_Pr.Pr_int_Pr.:
	PUSH {lr}
	LDR r4, [sp, #4]
	LDR r5, =0
	CMP r4, r5
	MOVEQ r4, #1
	MOVNE r4, #0
	CMP r4, #0
	BEQ else_4
	LDR r4, =0
	MOV r0, r4
	POP {pc}
	B fi_4
else_4:
	LDR r4, [sp, #4]
	LDR r5, [sp, #8]
	CMP r4, r5
	MOVEQ r4, #1
	MOVNE r4, #0
	CMP r4, #0
	BEQ else_5
	LDR r4, [sp, #4]
	MOV r0, r4
	BL p_check_null_pointer
	LDR r4, [r4, #4]
	LDR r4, [r4]
	STR r4, [sp, #4]
	LDR r4, [sp, #8]
	MOV r0, r4
	BL p_free_pair
	LDR r4, [sp, #4]
	MOV r0, r4
	POP {pc}
	B fi_5
else_5:
	SUB sp, sp, #4
	LDR r4, [sp, #8]
	MOV r0, r4
	BL p_check_null_pointer
	LDR r4, [r4, #4]
	LDR r4, [r4]
	STR r4, [sp]
	LDR r4, [sp, #12]
	STR r4, [sp, #-4]!
	LDR r4, [sp, #4]
	STR r4, [sp, #-4]!
	BL f_removeNode.Pr_int_Pr.Pr_int_Pr.
	ADD sp, sp, #8
	MOV r4, r0
	LDR r5, [sp, #8]
	MOV r0, r5
	BL p_check_null_pointer
	LDR r5, [r5, #4]
	STR r4, [r5]
	LDR r4, [sp, #8]
	MOV r0, r4
	ADD sp, sp, #4
	POP {pc}
	ADD sp, sp, #4
fi_5:
fi_4:
	POP {pc}
	.ltorg
f_countNodes.Pr_int_Pr.:
	PUSH {lr}
	SUB sp, sp, #4
	LDR r4, =0
	STR r4, [sp]
	B predicate_7
while_body_7:
	LDR r4, [sp]
	LDR r5, =1
	ADDS r4, r4, r5
	BLVS p_throw_overflow_error
	STR r4, [sp]
	LDR r4, [sp, #8]
	MOV r0, r4
	BL p_check_null_pointer
	LDR r4, [r4, #4]
	LDR r4, [r4]
	STR r4, [sp, #8]
predicate_7:
	LDR r4, [sp, #8]
	LDR r5, =0
	CMP r4, r5
	MOVEQ r4, #0
	MOVNE r4, #1
	CMP r4, #1
	BEQ while_body_7
	LDR r4, [sp]
	MOV r0, r4
	ADD sp, sp, #4
	POP {pc}
	POP {pc}
	.ltorg
f_printAllNodes.Pr_int_Pr.:
	PUSH {lr}
	B predicate_8
while_body_8:
	SUB sp, sp, #4
	LDR r4, [sp, #8]
	MOV r0, r4
	BL p_check_null_pointer
	LDR r4, [r4]
	LDR r4, [r4]
	STR r4, [sp]
	LDR r4, [sp]
	MOV r0, r4
	BL p_print_int
	MOV r4, #' '
	MOV r0, r4
	BL putchar
	LDR r4, [sp, #8]
	MOV r0, r4
	BL p_check_null_pointer
	LDR r4, [r4, #4]
	LDR r4, [r4]
	STR r4, [sp, #8]
	ADD sp, sp, #4
predicate_8:
	LDR r4, [sp, #4]
	LDR r5, =0
	CMP r4, r5
	MOVEQ r4, #0
	MOVNE r4, #1
	CMP r4, #1
	BEQ while_body_8
	MOV r4, #1
	MOV r0, r4
	POP {pc}
	POP {pc}
	.ltorg
f_printMenu.:
	PUSH {lr}
	SUB sp, sp, #8
	LDR r4, =msg_9
	MOV r0, r4
	BL p_print_string
	BL p_print_ln
	LDR r4, =msg_10
	MOV r0, r4
	BL p_print_string
	BL p_print_ln
	LDR r4, =msg_11
	MOV r0, r4
	BL p_print_string
	BL p_print_ln
	LDR r4, =msg_12
	MOV r0, r4
	BL p_print_string
	BL p_print_ln
	LDR r4, =msg_13
	MOV r0, r4
	BL p_print_string
	BL p_print_ln
	LDR r4, =msg_14
	MOV r0, r4
	BL p_print_string
	BL p_print_ln
	LDR r4, =msg_15
	MOV r0, r4
	BL p_print_string
	BL p_print_ln
	LDR r4, =msg_16
	MOV r0, r4
	BL p_print_string
	BL p_print_ln
	LDR r4, =msg_17
	MOV r0, r4
	BL p_print_string
	BL p_print_ln
	LDR r4, =msg_18
	MOV r0, r4
	BL p_print_string
	BL p_print_ln
	LDR r4, =msg_19
	MOV r0, r4
	BL p_print_string
	BL p_print_ln
	LDR r4, =msg_20
	MOV r0, r4
	BL p_print_string
	BL p_print_ln
	LDR r4, =msg_21
	MOV r0, r4
	BL p_print_string
	BL p_print_ln
	LDR r4, =msg_22
	MOV r0, r4
	BL p_print_string
	BL p_print_ln
	LDR r4, =msg_23
	MOV r0, r4
	BL p_print_string
	BL p_print_ln
	LDR r4, =97
	STR r4, [sp, #4]
	LDR r4, =103
	STR r4, [sp]
	B predicate_9
while_body_9:
	SUB sp, sp, #5
	LDR r4, =msg_24
	MOV r0, r4
	BL p_print_string
	MOV r4, #0
	STRB r4, [sp, #4]
	ADD r4, sp, #4
	MOV r0, r4
	BL p_read_char
	LDRSB r4, [sp, #4]
	STR r4, [sp]
	LDR r4, [sp, #9]
	LDR r5, [sp]
	CMP r4, r5
	MOVLE r4, #1
	MOVGT r4, #0
	LDR r5, [sp]
	LDR r6, [sp, #5]
	CMP r5, r6
	MOVLE r5, #1
	MOVGT r5, #0
	AND r4, r4, r5
	CMP r4, #0
	BEQ else_6
	LDRSB r4, [sp, #4]
	MOV r0, r4
	ADD sp, sp, #13
	POP {pc}
	B fi_6
else_6:
	LDR r4, =msg_26
	MOV r0, r4
	BL p_print_string
	LDRSB r4, [sp, #4]
	MOV r0, r4
	BL putchar
	LDR r4, =msg_27
	MOV r0, r4
	BL p_print_string
	BL p_print_ln
fi_6:
	ADD sp, sp, #5
predicate_9:
	MOV r4, #1
	CMP r4, #1
	BEQ while_body_9
	MOV r4, #0
	MOV r0, r4
	ADD sp, sp, #8
	POP {pc}
	POP {pc}
	.ltorg
f_askForInt.charArr.:
	PUSH {lr}
	SUB sp, sp, #4
	LDR r4, [sp, #8]
	MOV r0, r4
	BL p_print_string
	LDR r4, =0
	STR r4, [sp]
	ADD r4, sp, #0
	MOV r0, r4
	BL p_read_int
	LDR r4, =msg_28
	MOV r0, r4
	BL p_print_string
	LDR r4, [sp]
	MOV r0, r4
	BL p_print_int
	BL p_print_ln
	LDR r4, [sp]
	MOV r0, r4
	ADD sp, sp, #4
	POP {pc}
	POP {pc}
	.ltorg
f_handleMenuInsert.Pr_int_PrArr.:
	PUSH {lr}
	SUB sp, sp, #5
	LDR r4, =msg_29
	STR r4, [sp, #-4]!
	BL f_askForInt.charArr.
	ADD sp, sp, #4
	MOV r4, r0
	STR r4, [sp, #1]
	LDR r4, [sp, #1]
	STR r4, [sp, #-4]!
	LDR r4, [sp, #13]
	STR r4, [sp, #-4]!
	BL f_insertIfNotContain.Pr_int_PrArr.int.
	ADD sp, sp, #8
	MOV r4, r0
	STRB r4, [sp]
	LDRSB r4, [sp]
	CMP r4, #0
	BEQ else_7
	LDR r4, =msg_30
	MOV r0, r4
	BL p_print_string
	BL p_print_ln
	B fi_7
else_7:
	LDR r4, =msg_31
	MOV r0, r4
	BL p_print_string
	BL p_print_ln
fi_7:
	MOV r4, #1
	MOV r0, r4
	ADD sp, sp, #5
	POP {pc}
	POP {pc}
	.ltorg
f_handleMenuFind.Pr_int_PrArr.:
	PUSH {lr}
	SUB sp, sp, #5
	LDR r4, =msg_32
	STR r4, [sp, #-4]!
	BL f_askForInt.charArr.
	ADD sp, sp, #4
	MOV r4, r0
	STR r4, [sp, #1]
	LDR r4, [sp, #1]
	STR r4, [sp, #-4]!
	LDR r4, [sp, #13]
	STR r4, [sp, #-4]!
	BL f_contain.Pr_int_PrArr.int.
	ADD sp, sp, #8
	MOV r4, r0
	STRB r4, [sp]
	LDRSB r4, [sp]
	CMP r4, #0
	BEQ else_8
	LDR r4, =msg_33
	MOV r0, r4
	BL p_print_string
	BL p_print_ln
	B fi_8
else_8:
	LDR r4, =msg_34
	MOV r0, r4
	BL p_print_string
	BL p_print_ln
fi_8:
	MOV r4, #1
	MOV r0, r4
	ADD sp, sp, #5
	POP {pc}
	POP {pc}
	.ltorg
f_handleMenuCount.Pr_int_PrArr.:
	PUSH {lr}
	SUB sp, sp, #4
	LDR r4, [sp, #8]
	STR r4, [sp, #-4]!
	BL f_count.Pr_int_PrArr.
	ADD sp, sp, #4
	MOV r4, r0
	STR r4, [sp]
	LDR r4, [sp]
	LDR r5, =1
	CMP r4, r5
	MOVEQ r4, #1
	MOVNE r4, #0
	CMP r4, #0
	BEQ else_9
	LDR r4, =msg_35
	MOV r0, r4
	BL p_print_string
	BL p_print_ln
	B fi_9
else_9:
	LDR r4, =msg_36
	MOV r0, r4
	BL p_print_string
	LDR r4, [sp]
	MOV r0, r4
	BL p_print_int
	LDR r4, =msg_37
	MOV r0, r4
	BL p_print_string
	BL p_print_ln
fi_9:
	MOV r4, #1
	MOV r0, r4
	ADD sp, sp, #4
	POP {pc}
	POP {pc}
	.ltorg
f_handleMenuPrint.Pr_int_PrArr.:
	PUSH {lr}
	SUB sp, sp, #1
	LDR r4, =msg_38
	MOV r0, r4
	BL p_print_string
	LDR r4, [sp, #5]
	STR r4, [sp, #-4]!
	BL f_printAll.Pr_int_PrArr.
	ADD sp, sp, #4
	MOV r4, r0
	STRB r4, [sp]
	MOV r4, #1
	MOV r0, r4
	ADD sp, sp, #1
	POP {pc}
	POP {pc}
	.ltorg
f_handleMenuRemove.Pr_int_PrArr.:
	PUSH {lr}
	SUB sp, sp, #5
	LDR r4, =msg_39
	STR r4, [sp, #-4]!
	BL f_askForInt.charArr.
	ADD sp, sp, #4
	MOV r4, r0
	STR r4, [sp, #1]
	LDR r4, [sp, #1]
	STR r4, [sp, #-4]!
	LDR r4, [sp, #13]
	STR r4, [sp, #-4]!
	BL f_remove.Pr_int_PrArr.int.
	ADD sp, sp, #8
	MOV r4, r0
	STRB r4, [sp]
	LDRSB r4, [sp]
	CMP r4, #0
	BEQ else_10
	LDR r4, =msg_40
	MOV r0, r4
	BL p_print_string
	BL p_print_ln
	B fi_10
else_10:
	LDR r4, =msg_41
	MOV r0, r4
	BL p_print_string
	BL p_print_ln
fi_10:
	MOV r4, #1
	MOV r0, r4
	ADD sp, sp, #5
	POP {pc}
	POP {pc}
	.ltorg
f_handleMenuRemoveAll.Pr_int_PrArr.:
	PUSH {lr}
	SUB sp, sp, #1
	LDR r4, [sp, #5]
	STR r4, [sp, #-4]!
	BL f_removeAll.Pr_int_PrArr.
	ADD sp, sp, #4
	MOV r4, r0
	STRB r4, [sp]
	LDR r4, =msg_42
	MOV r0, r4
	BL p_print_string
	BL p_print_ln
	MOV r4, #1
	MOV r0, r4
	ADD sp, sp, #1
	POP {pc}
	POP {pc}
	.ltorg
main:
	PUSH {lr}
	SUB sp, sp, #6
	LDR r0, =56
	BL malloc
	MOV r4, r0
	LDR r5, =0
	STR r5, [r4, #4]
	LDR r5, =0
	STR r5, [r4, #8]
	LDR r5, =0
	STR r5, [r4, #12]
	LDR r5, =0
	STR r5, [r4, #16]
	LDR r5, =0
	STR r5, [r4, #20]
	LDR r5, =0
	STR r5, [r4, #24]
	LDR r5, =0
	STR r5, [r4, #28]
	LDR r5, =0
	STR r5, [r4, #32]
	LDR r5, =0
	STR r5, [r4, #36]
	LDR r5, =0
	STR r5, [r4, #40]
	LDR r5, =0
	STR r5, [r4, #44]
	LDR r5, =0
	STR r5, [r4, #48]
	LDR r5, =0
	STR r5, [r4, #52]
	LDR r5, =13
	STR r5, [r4]
	STR r4, [sp, #2]
	LDR r4, [sp, #2]
	STR r4, [sp, #-4]!
	BL f_init.Pr_int_PrArr.
	ADD sp, sp, #4
	MOV r4, r0
	STRB r4, [sp, #1]
	MOV r4, #1
	STRB r4, [sp]
	B predicate_10
while_body_10:
	SUB sp, sp, #1
	BL f_printMenu.
	MOV r4, r0
	STRB r4, [sp]
	LDRSB r4, [sp]
	MOV r5, #'a'
	CMP r4, r5
	MOVEQ r4, #1
	MOVNE r4, #0
	CMP r4, #0
	BEQ else_11
	SUB sp, sp, #1
	LDR r4, [sp, #4]
	STR r4, [sp, #-4]!
	BL f_handleMenuInsert.Pr_int_PrArr.
	ADD sp, sp, #4
	MOV r4, r0
	STRB r4, [sp]
	ADD sp, sp, #1
	B fi_11
else_11:
	LDRSB r4, [sp]
	MOV r5, #'b'
	CMP r4, r5
	MOVEQ r4, #1
	MOVNE r4, #0
	CMP r4, #0
	BEQ else_12
	SUB sp, sp, #1
	LDR r4, [sp, #4]
	STR r4, [sp, #-4]!
	BL f_handleMenuFind.Pr_int_PrArr.
	ADD sp, sp, #4
	MOV r4, r0
	STRB r4, [sp]
	ADD sp, sp, #1
	B fi_12
else_12:
	LDRSB r4, [sp]
	MOV r5, #'c'
	CMP r4, r5
	MOVEQ r4, #1
	MOVNE r4, #0
	CMP r4, #0
	BEQ else_13
	SUB sp, sp, #1
	LDR r4, [sp, #4]
	STR r4, [sp, #-4]!
	BL f_handleMenuCount.Pr_int_PrArr.
	ADD sp, sp, #4
	MOV r4, r0
	STRB r4, [sp]
	ADD sp, sp, #1
	B fi_13
else_13:
	LDRSB r4, [sp]
	MOV r5, #'d'
	CMP r4, r5
	MOVEQ r4, #1
	MOVNE r4, #0
	CMP r4, #0
	BEQ else_14
	SUB sp, sp, #1
	LDR r4, [sp, #4]
	STR r4, [sp, #-4]!
	BL f_handleMenuPrint.Pr_int_PrArr.
	ADD sp, sp, #4
	MOV r4, r0
	STRB r4, [sp]
	ADD sp, sp, #1
	B fi_14
else_14:
	LDRSB r4, [sp]
	MOV r5, #'e'
	CMP r4, r5
	MOVEQ r4, #1
	MOVNE r4, #0
	CMP r4, #0
	BEQ else_15
	SUB sp, sp, #1
	LDR r4, [sp, #4]
	STR r4, [sp, #-4]!
	BL f_handleMenuRemove.Pr_int_PrArr.
	ADD sp, sp, #4
	MOV r4, r0
	STRB r4, [sp]
	ADD sp, sp, #1
	B fi_15
else_15:
	LDRSB r4, [sp]
	MOV r5, #'f'
	CMP r4, r5
	MOVEQ r4, #1
	MOVNE r4, #0
	CMP r4, #0
	BEQ else_16
	SUB sp, sp, #1
	LDR r4, [sp, #4]
	STR r4, [sp, #-4]!
	BL f_handleMenuRemoveAll.Pr_int_PrArr.
	ADD sp, sp, #4
	MOV r4, r0
	STRB r4, [sp]
	ADD sp, sp, #1
	B fi_16
else_16:
	LDRSB r4, [sp]
	MOV r5, #'g'
	CMP r4, r5
	MOVEQ r4, #1
	MOVNE r4, #0
	CMP r4, #0
	BEQ else_17
	LDR r4, =msg_43
	MOV r0, r4
	BL p_print_string
	BL p_print_ln
	MOV r4, #0
	STRB r4, [sp, #1]
	B fi_17
else_17:
	LDR r4, =msg_44
	MOV r0, r4
	BL p_print_string
	LDRSB r4, [sp]
	MOV r0, r4
	BL putchar
	LDR r4, =msg_45
	MOV r0, r4
	BL p_print_string
	BL p_print_ln
	LDR r4, =-1
	MOV r0, r4
	BL exit
fi_17:
fi_16:
fi_15:
fi_14:
fi_13:
fi_12:
fi_11:
	ADD sp, sp, #1
predicate_10:
	LDRSB r4, [sp]
	CMP r4, #1
	BEQ while_body_10
	ADD sp, sp, #6
	LDR r0, =0
	POP {pc}
	.ltorg
p_print_ln:
	PUSH {lr}
	LDR r0, =msg_6
	ADD r0, r0, #4
	BL puts
	MOV r0, #0
	BL fflush
	POP {pc}
p_throw_runtime_error:
	BL p_print_string
	MOV r0, #-1
	BL exit
p_print_int:
	PUSH {lr}
	MOV r1, r0
	LDR r0, =msg_8
	ADD r0, r0, #4
	BL printf
	MOV r0, #0
	BL fflush
	POP {pc}
p_check_array_bounds:
	PUSH {lr}
	CMP r0, #0
	LDRLT r0, =msg_0
	BLLT p_throw_runtime_error
	LDR r1, [r1]
	CMP r0, r1
	LDRCS r0, =msg_1
	BLCS p_throw_runtime_error
	POP {pc}
p_check_null_pointer:
	PUSH {lr}
	CMP r0, #0
	LDREQ r0, =msg_4
	BEQ p_throw_runtime_error
	POP {pc}
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
p_check_divide_by_zero:
	PUSH {lr}
	CMP r1, #0
	LDREQ r0, =msg_7
	BLEQ p_throw_runtime_error
	POP {pc}
p_read_char:
	PUSH {lr}
	MOV r1, r0
	LDR r0, =msg_25
	ADD r0, r0, #4
	BL scanf
	POP {pc}
p_read_int:
	PUSH {lr}
	MOV r1, r0
	LDR r0, =msg_8
	ADD r0, r0, #4
	BL scanf
	POP {pc}
p_throw_overflow_error:
	LDR r0, =msg_3
	BL p_throw_runtime_error
p_free_pair:
	PUSH {lr}
	CMP r0, #0
	LDREQ r0, =msg_4
	BEQ p_throw_runtime_error
	PUSH {r0}
	LDR r0, [r0]
	BL free
	LDR r0, [sp]
	LDR r0, [r0, #4]
	BL free
	POP {r0}
	BL free
	POP {pc}

