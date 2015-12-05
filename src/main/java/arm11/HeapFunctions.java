package arm11;


public class HeapFunctions {
  public static InstructionList freePair(DataInstructions data) {
    InstructionList list = new InstructionList();

    Label errMessage = data.addMessage("NullReferenceError: dereference a "
                                       + "null reference\\n\\0");
    Label throwRuntimeError = new Label("p_throw_runtime_error");

    list.add(InstructionFactory.createLabel(new Label("p_free_pair")))
        .add(InstructionFactory.createPush(ARM11Registers.LR))
        .add(InstructionFactory.createCompare(ARM11Registers.R0,
            new Immediate(0L)))
        .add(InstructionFactory.createLoadEqual(ARM11Registers.R0, errMessage))
        .add(InstructionFactory.createBranchEqual(throwRuntimeError))
        .add(InstructionFactory.createPush(ARM11Registers.R0))
        .add(InstructionFactory.createLoad(ARM11Registers.R0,
            new Address(ARM11Registers.R0)))
        .add(InstructionFactory.createBranchLink(new Label("free")))
        .add(InstructionFactory.createLoad(ARM11Registers.R0,
            new Address(ARM11Registers.SP)))
        .add(InstructionFactory.createBranchLink(new Label("free")))
        .add(InstructionFactory.createLoad(ARM11Registers.R0, ARM11Registers.R0,
            new Immediate(4L)))
        .add(InstructionFactory.createPop(ARM11Registers.R0))
        .add(InstructionFactory.createBranchLink(new Label("free")))
        .add(InstructionFactory.createPop(ARM11Registers.PC));
    return list;
  }
}
