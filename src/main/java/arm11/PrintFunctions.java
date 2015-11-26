package arm11;

// TODO: in main move argument to r0

public class PrintFunctions {

    public static InstructionList printInt(DataInstructions data) {
      // Where label is the name of the data section which has the int
      // formatter string

      // formatterLabel:
      //        .word 3
      //		    .ascii	"%d\0"

      InstructionList list = new InstructionList();

      Label label = new Label("p_print_int");
      Label intFormatterLabel
          = data.addPrintFormatter(PrintFormatters.INT_PRINT_FORMATTER);
      Register r0 = ARM11Registers.getRegister(ARM11Registers.Reg.R0);
      Register r1 = ARM11Registers.getRegister(ARM11Registers.Reg.R1);
      Register lr = ARM11Registers.getRegister(ARM11Registers.Reg.LR);
      Register pc = ARM11Registers.getRegister(ARM11Registers.Reg.PC);

      //      p_print_int:
      //      PUSH {lr}
      //      MOV r1, r0
      //      LDR r0, =INT_FORMATTER_LABEL
      //      ADD r0, r0, #4
      //      BL printf
      //      MOV r0, #0
      //      BL fflush
      //      POP {pc}

      list.add(InstructionFactory.createLabel(label));
      list.add(InstructionFactory.createPush(lr));
      list.add(InstructionFactory.createMov(r1, r0));
      list.add(InstructionFactory.createLoad(r0, intFormatterLabel));
      list.add(InstructionFactory.createAdd(r0, r0, new Immediate((long) 4)));
      list.add(InstructionFactory.createBranchLink(new Label("printf")));
      list.add(InstructionFactory.createMov(r1, new Immediate((long) 0)));
      list.add(InstructionFactory.createBranchLink(new Label("fflush")));
      list.add(InstructionFactory.createPop(pc));

      return list;
    }

  public static InstructionList printString(DataInstructions data) {
    InstructionList list = new InstructionList();

    Label label = new Label("p_print_string");
    Label stringFormatterLabel
        = data.addPrintFormatter(PrintFormatters.STRING_PRINT_FORMATTER);
    Register r0 = ARM11Registers.getRegister(ARM11Registers.Reg.R0);
    Register r1 = ARM11Registers.getRegister(ARM11Registers.Reg.R1);
    Register r2 = ARM11Registers.getRegister(ARM11Registers.Reg.R2);
    Register lr = ARM11Registers.getRegister(ARM11Registers.Reg.LR);
    Register pc = ARM11Registers.getRegister(ARM11Registers.Reg.PC);

//    p_print_string:
//    PUSH {lr}
//    LDR r1, [r0]
//    ADD r2, r0, #4
//    LDR r0, =STRING_FORMATTER_LABEL
//        ADD r0, r0, #4
//    BL printf
//    MOV r0, #0
//    BL fflush
//    POP {pc}

    list.add(InstructionFactory.createLabel(label));
    list.add(InstructionFactory.createPush(lr));
    list.add(InstructionFactory.createLoad(r1, new Address(r0)));
    list.add(InstructionFactory.createAdd(r2, r0, new Immediate((long) 4)));
    list.add(InstructionFactory.createLoad(r0, stringFormatterLabel));
    list.add(InstructionFactory.createAdd(r0, r0, new Immediate((long) 4)));
    list.add(InstructionFactory.createBranchLink(new Label("printf")));
    list.add(InstructionFactory.createMov(r0, new Immediate((long) 0)));
    list.add(InstructionFactory.createBranchLink(new Label("fflush")));
    list.add(InstructionFactory.createPop(pc));

    return list;
  }

  public static InstructionList printLn(DataInstructions data) {
    InstructionList list = new InstructionList();

    Label label = new Label("p_print_ln");
    Label LnFormatterLabel
        = data.addPrintFormatter(PrintFormatters.LN_PRINT_FORMATTER);
    Register r0 = ARM11Registers.getRegister(ARM11Registers.Reg.R0);
    Register lr = ARM11Registers.getRegister(ARM11Registers.Reg.LR);
    Register pc = ARM11Registers.getRegister(ARM11Registers.Reg.PC);

//    p_print_ln:
//    PUSH {lr}
//    LDR r0, =LN_FORMATTER_LABEL
//        ADD r0, r0, #4
//    BL puts
//    MOV r0, #0
//    BL fflush
//    POP {pc}

    list.add(InstructionFactory.createLabel(label));
    list.add(InstructionFactory.createPush(lr));
    list.add(InstructionFactory.createLoad(r0, LnFormatterLabel));
    list.add(InstructionFactory.createAdd(r0, r0, new Immediate((long) 4)));
    list.add(InstructionFactory.createBranchLink(new Label("puts")));
    list.add(InstructionFactory.createMov(r0, new Immediate((long) 0)));
    list.add(InstructionFactory.createBranchLink(new Label("fflush")));
    list.add(InstructionFactory.createPop(pc));

    return list;
  }

  public static InstructionList printBool(DataInstructions data) {
    InstructionList list = new InstructionList();

    Label label = new Label("p_print_bool");
    Label trueFormatterLabel
        = data.addPrintFormatter(PrintFormatters.BOOL_TRUE_PRINT_FORMATTER);
    Label falseFormatterLabel
        = data.addPrintFormatter(PrintFormatters.BOOL_FALSE_PRINT_FORMATTER);
    Register r0 = ARM11Registers.getRegister(ARM11Registers.Reg.R0);
    Register lr = ARM11Registers.getRegister(ARM11Registers.Reg.LR);
    Register pc = ARM11Registers.getRegister(ARM11Registers.Reg.PC);

//    p_print_bool:
//    PUSH {lr}
//    CMP r0, #0
//    LDRNE r0, =TRUE_FORMATTER_LABEL
//    LDREQ r0, =FALSE_FORMATTER_LABEL
//    ADD r0, r0, #4
//    BL printf
//    MOV r0, #0
//    BL fflush
//    POP {pc}

    list.add(InstructionFactory.createLabel(label));
    list.add(InstructionFactory.createPush(lr));
    list.add(InstructionFactory.createCompare(r0, 0));
    list.add(InstructionFactory.createLoadNotEqual(r0, trueFormatterLabel));
    list.add(InstructionFactory.createLoadEqual(r0, falseFormatterLabel));
    list.add(InstructionFactory.createAdd(r0, r0, new Immediate((long) 4)));
    list.add(InstructionFactory.createBranchLink(new Label("printf")));
    list.add(InstructionFactory.createMov(r0, new Immediate((long) 0)));
    list.add(InstructionFactory.createBranchLink(new Label("fflush")));
    list.add(InstructionFactory.createPop(pc));

    return list;
  }

  public static InstructionList printReference(DataInstructions data) {

//    formatter:
//    .word 3
//        .ascii	"%p\0"

    InstructionList list = new InstructionList();

    Label label = new Label("p_print_reference");
    Register r0 = ARM11Registers.getRegister(ARM11Registers.Reg.R0);
    Register r1 = ARM11Registers.getRegister(ARM11Registers.Reg.R1);
    Register lr = ARM11Registers.getRegister(ARM11Registers.Reg.LR);
    Register pc = ARM11Registers.getRegister(ARM11Registers.Reg.PC);

    //  p_print_reference:
    //  PUSH {lr}
    //  MOV r1, r0
    //  LDR r0, =REFERENCE_FORMATTER_LABEL
    //  ADD r0, r0, #4
    //  BL printf
    //  MOV r0, #0
    //  BL fflush
    //  POP {pc}

    Label referenceFormatterLabel
        = data.addPrintFormatter(PrintFormatters.LN_PRINT_FORMATTER);
    list.add(InstructionFactory.createLabel(label));
    list.add(InstructionFactory.createPush(lr));
    list.add(InstructionFactory.createLoad(r1, r0));
    list.add(InstructionFactory.createLoad(r0, referenceFormatterLabel));
    list.add(InstructionFactory.createAdd(r0, r0, new Immediate((long) 4)));
    list.add(InstructionFactory.createBranchLink(new Label("printf")));
    list.add(InstructionFactory.createMov(r0, new Immediate((long) 0)));
    list.add(InstructionFactory.createBranchLink(new Label("fflush")));
    list.add(InstructionFactory.createPop(pc));

    return list;
  }

}