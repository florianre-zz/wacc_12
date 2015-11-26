package arm11;

public class PrintFunctions {

    public static InstructionList printInt(DataInstructions data) {
      InstructionList list = new InstructionList();

      Label label = new Label("p_print_int");
      Register r0 = ARM11Registers.getRegister(ARM11Registers.Reg.R0);
      Register r1 = ARM11Registers.getRegister(ARM11Registers.Reg.R1);
      Register lr = ARM11Registers.getRegister(ARM11Registers.Reg.LR);
      Register pc = ARM11Registers.getRegister(ARM11Registers.Reg.PC);
      Label intFormatterLabel
          = data.addPrintFormatter(IOFormatters.INT_FORMATTER);

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
    Register r0 = ARM11Registers.getRegister(ARM11Registers.Reg.R0);
    Register r1 = ARM11Registers.getRegister(ARM11Registers.Reg.R1);
    Register r2 = ARM11Registers.getRegister(ARM11Registers.Reg.R2);
    Register lr = ARM11Registers.getRegister(ARM11Registers.Reg.LR);
    Register pc = ARM11Registers.getRegister(ARM11Registers.Reg.PC);
    Label stringFormatterLabel
        = data.addPrintFormatter(IOFormatters.STRING_FORMATTER);

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
    Register r0 = ARM11Registers.getRegister(ARM11Registers.Reg.R0);
    Register lr = ARM11Registers.getRegister(ARM11Registers.Reg.LR);
    Register pc = ARM11Registers.getRegister(ARM11Registers.Reg.PC);
    Label lnFormatterLabel = data.addPrintFormatter(IOFormatters.LN_FORMATTER);

    list.add(InstructionFactory.createLabel(label));
    list.add(InstructionFactory.createPush(lr));
    list.add(InstructionFactory.createLoad(r0, lnFormatterLabel));
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
    Register r0 = ARM11Registers.getRegister(ARM11Registers.Reg.R0);
    Register lr = ARM11Registers.getRegister(ARM11Registers.Reg.LR);
    Register pc = ARM11Registers.getRegister(ARM11Registers.Reg.PC);
    Label trueFormatterLabel
        = data.addPrintFormatter(IOFormatters.BOOL_TRUE_FORMATTER);
    Label falseFormatterLabel
        = data.addPrintFormatter(IOFormatters.BOOL_FALSE_FORMATTER);

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
    InstructionList list = new InstructionList();

    Label label = new Label("p_print_reference");
    Register r0 = ARM11Registers.getRegister(ARM11Registers.Reg.R0);
    Register r1 = ARM11Registers.getRegister(ARM11Registers.Reg.R1);
    Register lr = ARM11Registers.getRegister(ARM11Registers.Reg.LR);
    Register pc = ARM11Registers.getRegister(ARM11Registers.Reg.PC);

    Label referenceFormatterLabel
        = data.addPrintFormatter(IOFormatters.REFERENCE_FORMATTER);
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