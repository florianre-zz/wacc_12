package arm11;

public class PrintFunctions {


  private static final Register R0 =
      ARM11Registers.getRegister(ARM11Registers.Reg.R0);
  private static final Register R1 =
      ARM11Registers.getRegister(ARM11Registers.Reg.R1);
  private static final Register R2 =
      ARM11Registers.getRegister(ARM11Registers.Reg.R2);
  private static final Register LR =
      ARM11Registers.getRegister(ARM11Registers.Reg.LR);
  private static final Register PC =
      ARM11Registers.getRegister(ARM11Registers.Reg.PC);

  private static void printAsciiAndFlush(InstructionList list, Register R0) {
    list.add(InstructionFactory.createBranchLink(new Label("puts")));
    list.add(InstructionFactory.createMov(R0, new Immediate((long) 0)));
    list.add(InstructionFactory.createBranchLink(new Label("fflush")));
  }

  private static void saveLinkRegister(InstructionList list,
                                       Label printLabel,
                                       Register LR) {
    list.add(InstructionFactory.createLabel(printLabel));
    list.add(InstructionFactory.createPush(LR));
  }

  public static InstructionList printInt(DataInstructions data) {
    InstructionList list = new InstructionList();
    
    Label printLabel = new Label("p_print_int");
    Label intFormatterLabel
        = data.addPrintFormatter(IOFormatters.INT_FORMATTER);

    saveLinkRegister(list, printLabel, LR);
    list.add(InstructionFactory.createMov(R1, R0));
    list.add(InstructionFactory.createLoad(R0, intFormatterLabel));
    list.add(InstructionFactory.createAdd(R0, R0, new Immediate((long) 4)));
    printAsciiAndFlush(list, R0);

    list.add(InstructionFactory.createPop(PC));
    
    return list;
  }

  public static InstructionList printString(DataInstructions data) {
    InstructionList list = new InstructionList();

    Label printLabel = new Label("p_print_string");
    Label stringFormatterLabel
        = data.addPrintFormatter(IOFormatters.STRING_FORMATTER);

    saveLinkRegister(list, printLabel, LR);
    list.add(InstructionFactory.createLoad(R1, new Address(R0)));
    list.add(InstructionFactory.createAdd(R2, R0, new Immediate((long) 4)));
    list.add(InstructionFactory.createLoad(R0, stringFormatterLabel));
    list.add(InstructionFactory.createAdd(R0, R0, new Immediate((long) 4)));
    printAsciiAndFlush(list, R0);
    list.add(InstructionFactory.createPop(PC));

    return list;
  }

  public static InstructionList printLn(DataInstructions data) {
    InstructionList list = new InstructionList();

    Label printLabel = new Label("p_print_ln");
    Label LnFormatterLabel = data.addPrintFormatter(IOFormatters.LN_FORMATTER);

    saveLinkRegister(list, printLabel, LR);
    list.add(InstructionFactory.createLoad(R0, LnFormatterLabel));
    list.add(InstructionFactory.createAdd(R0, R0, new Immediate((long) 4)));
    printAsciiAndFlush(list, R0);
    list.add(InstructionFactory.createPop(PC));

    return list;
  }

  public static InstructionList printBool(DataInstructions data) {
    InstructionList list = new InstructionList();

    Label printLabel = new Label("p_print_bool");
    Label trueFormatterLabel
        = data.addPrintFormatter(IOFormatters.BOOL_TRUE_FORMATTER);
    Label falseFormatterLabel
        = data.addPrintFormatter(IOFormatters.BOOL_FALSE_FORMATTER);

    saveLinkRegister(list, printLabel, LR);
    list.add(InstructionFactory.createCompare(R0, 0));
    list.add(InstructionFactory.createLoadNotEqual(R0, trueFormatterLabel));
    list.add(InstructionFactory.createLoadEqual(R0, falseFormatterLabel));
    list.add(InstructionFactory.createAdd(R0, R0, new Immediate((long) 4)));
    printAsciiAndFlush(list, R0);
    list.add(InstructionFactory.createPop(PC));

    return list;
  }

  public static InstructionList printReference(DataInstructions data) {
    InstructionList list = new InstructionList();

    Label printLabel = new Label("p_print_reference");
    Label referenceFormatterLabel
        = data.addPrintFormatter(IOFormatters.REFERENCE_FORMATTER);

    saveLinkRegister(list, printLabel, LR);
    list.add(InstructionFactory.createLoad(R1, R0));
    list.add(InstructionFactory.createLoad(R0, referenceFormatterLabel));
    list.add(InstructionFactory.createAdd(R0, R0, new Immediate((long) 4)));
    printAsciiAndFlush(list, R0);
    list.add(InstructionFactory.createPop(PC));

    return list;
  }

}