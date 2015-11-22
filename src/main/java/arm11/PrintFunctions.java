package arm11;

// TODO: in main move argument to r0

public class PrintFunctions {

    public static InstructionList printInt(String formatterLabel) {
      // Where label is the name of the data section which has the int
      // formatter string

      // formatterLabel:
      //        .word 3
      //		    .ascii	"%d\0"

      InstructionList list = new InstructionList();

      Label label = new Label("p_print_int");
      Register r0 = ARM11Registers.getRegister(ARM11Registers.ARM11Register.R0);
      Register r1 = ARM11Registers.getRegister(ARM11Registers.ARM11Register.R1);
      Register lr = ARM11Registers.getRegister(ARM11Registers.ARM11Register.LR);
      Register pc = ARM11Registers.getRegister(ARM11Registers.ARM11Register.PC);

      //      p_print_int:
      //      PUSH {lr}
      //      MOV r1, r0
      //      LDR r0, =label
      //      ADD r0, r0, #4
      //      BL printf
      //      MOV r0, #0
      //      BL fflush
      //      POP {pc}

      list.add(InstructionFactory.createLabel(label));
      list.add(InstructionFactory.createPush(lr));
      list.add(InstructionFactory.createMov(r1, r0));
      list.add(InstructionFactory.createLoad(r0, new Label(formatterLabel)));
      list.add(InstructionFactory.createAdd(r0, r0, 4));
      list.add(InstructionFactory.createBranchLink(new Label("printf")));
      list.add(InstructionFactory.createMov(r1, 0));
      list.add(InstructionFactory.createBranchLink(new Label("fflush")));
      list.add(InstructionFactory.createPop(pc));

      return list;
    }

  public static InstructionList printString(String formatterLabel) {
    InstructionList list = new InstructionList();

    Label label = new Label("p_print_string");
    Register r0 = ARM11Registers.getRegister(ARM11Registers.ARM11Register.R0);
    Register r1 = ARM11Registers.getRegister(ARM11Registers.ARM11Register.R1);
    Register r2 = ARM11Registers.getRegister(ARM11Registers.ARM11Register.R2);
    Register lr = ARM11Registers.getRegister(ARM11Registers.ARM11Register.LR);
    Register pc = ARM11Registers.getRegister(ARM11Registers.ARM11Register.PC);

//    p_print_string:
//    PUSH {lr}
//    LDR r1, [r0]
//    ADD r2, r0, #4
//    LDR r0, =!msg_2!
//        ADD r0, r0, #4
//    BL printf
//    MOV r0, #0
//    BL fflush
//    POP {pc}

    list.add(InstructionFactory.createLabel(label));
    list.add(InstructionFactory.createPush(lr));
    list.add(InstructionFactory.createLoad(r1, new Address(r0)));
    list.add(InstructionFactory.createAdd(r2, r0, 4));
    list.add(InstructionFactory.createLoad(r0, new Label(formatterLabel)));
    list.add(InstructionFactory.createAdd(r0, r0, 4));
    list.add(InstructionFactory.createBranchLink(new Label("printf")));
    list.add(InstructionFactory.createMov(r0, 0));
    list.add(InstructionFactory.createBranchLink(new Label("fflush")));
    list.add(InstructionFactory.createPop(pc));

    return list;
  }

  public static InstructionList printLn(String formatterLabel) {
    InstructionList list = new InstructionList();

    Label label = new Label("p_print_ln");
    Register r0 = ARM11Registers.getRegister(ARM11Registers.ARM11Register.R0);
    Register lr = ARM11Registers.getRegister(ARM11Registers.ARM11Register.LR);
    Register pc = ARM11Registers.getRegister(ARM11Registers.ARM11Register.PC);

//    p_print_ln:
//    PUSH {lr}
//    LDR r0, =!msg_3!
//        ADD r0, r0, #4
//    BL puts
//    MOV r0, #0
//    BL fflush
//    POP {pc}

    list.add(InstructionFactory.createLabel(label));
    list.add(InstructionFactory.createPush(lr));
    list.add(InstructionFactory.createLoad(r0, new Label(formatterLabel)));
    list.add(InstructionFactory.createAdd(r0, r0, 4));
    list.add(InstructionFactory.createBranchLink(new Label("puts")));
    list.add(InstructionFactory.createMov(r0, 0));
    list.add(InstructionFactory.createBranchLink(new Label("fflush")));
    list.add(InstructionFactory.createPop(pc));

    return list;
  }

  public static InstructionList printBool(String formatterLabel1,
                                          String formatterLabel2) {
    InstructionList list = new InstructionList();

    Label label = new Label("p_print_bool");
    Register r0 = ARM11Registers.getRegister(ARM11Registers.ARM11Register.R0);
    Register lr = ARM11Registers.getRegister(ARM11Registers.ARM11Register.LR);
    Register pc = ARM11Registers.getRegister(ARM11Registers.ARM11Register.PC);

//    p_print_bool:
//    PUSH {lr}
//    CMP r0, #0
//    LDRNE r0, =msg_3
//    LDREQ r0, =msg_4
//    ADD r0, r0, #4
//    BL printf
//    MOV r0, #0
//    BL fflush
//    POP {pc}

    list.add(InstructionFactory.createLabel(label));
    list.add(InstructionFactory.createPush(lr));
    list.add(InstructionFactory.createCompare(r0, 0));
    list.add(InstructionFactory.createLoadNotEqual(r0,
                                                   new Label(formatterLabel1)));
    list.add(InstructionFactory.createLoadEqual(r0,
                                                new Label(formatterLabel2)));
    list.add(InstructionFactory.createAdd(r0, r0, 4));
    list.add(InstructionFactory.createBranchLink(new Label("printf")));
    list.add(InstructionFactory.createMov(r0, 0));
    list.add(InstructionFactory.createBranchLink(new Label("fflush")));
    list.add(InstructionFactory.createPop(pc));

    return list;
  }

}
