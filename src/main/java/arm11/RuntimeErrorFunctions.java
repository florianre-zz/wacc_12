package arm11;

public class RuntimeErrorFunctions {

  public static InstructionList divideByZero(DataInstructions data) {
    InstructionList list = new InstructionList();

    Label checkDivideByZerolabel = new Label("p_check_divide_by_zero");
    Label throwRuntimeErrorLabel = new Label("p_throw_runtime_error");
    Label errMessage = data.addConstString("DivideByZeroError: divide or " +
                                             "modulo by zero\\n\\0");

    list.add(InstructionFactory.createLabel(checkDivideByZerolabel))
        .add(InstructionFactory.createPush(ARM11Registers.LR))
        .add(InstructionFactory.createCompare(ARM11Registers.R1,
                                              new Immediate((long) 0)))
        .add(InstructionFactory.createLoadEqual(ARM11Registers.R0, errMessage))
        .add(InstructionFactory.createBranchLinkEqual(throwRuntimeErrorLabel))
        .add(InstructionFactory.createPop(ARM11Registers.PC));

    return list;
  }

  public static InstructionList throwRuntimeError(DataInstructions data) {
    InstructionList list = new InstructionList();
    data.addPrintFormatter(IOFormatters.STRING_FORMATTER);

    Immediate exitCode = new Immediate((long) -1);

    list.add(InstructionFactory.createLabel(new Label("p_throw_runtime_error")))
         .add(InstructionFactory.createBranchLink(new Label("p_print_string")))
        .add(InstructionFactory.createMove(ARM11Registers.R0, exitCode))
        .add(InstructionFactory.createBranchLink(new Label("exit")));

    return list;
  }

}
