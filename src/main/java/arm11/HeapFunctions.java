package arm11;


import static arm11.InstructionFactory.*;

public class HeapFunctions {
  public static InstructionList freePair(DataInstructions data) {
    InstructionList list = new InstructionList();

    Label errMessage = data.addMessage("\"NullReferenceError: dereference a "
                                       + "null reference\\n\\0\"");
    Label throwRuntimeError = new Label("p_throw_runtime_error");

    list.add(createLabel(new Label("p_free_pair")))
        .add(createPush(ARM11Registers.LR))
        .add(createCompare(ARM11Registers.R0, new Immediate(0L)))
        .add(createLoadEqual(ARM11Registers.R0, errMessage))
        .add(createBranchEqual(throwRuntimeError))
        .add(createPush(ARM11Registers.R0))
        .add(createLoad(ARM11Registers.R0, new Address(ARM11Registers.R0)))
        .add(createBranchLink(new Label("free")))
        .add(createLoad(ARM11Registers.R0, new Address(ARM11Registers.SP)))
        .add(createLoad(ARM11Registers.R0, ARM11Registers.R0,
            new Immediate(4L)))
        .add(createBranchLink(new Label("free")))
        .add(createPop(ARM11Registers.R0))
        .add(createBranchLink(new Label("free")))
        .add(createPop(ARM11Registers.PC));
    return list;
  }
}
