package arm11;


public class HeapFunctions {
  public static InstructionList freePair(DataInstructions data) {
    InstructionList list = new InstructionList();
    list.add(InstructionFactory.createLabel(new Label("p_free_pair")))
        .add(InstructionFactory.createPush(ARM11Registers.LR))
        .add(InstructionFactory.createCompare(ARM11Registers.R0,
            new Immediate(0L)))
        .add(InstructionFactory.createPush(ARM11Registers.R0))
        .add(InstructionFactory.createLoad(ARM11Registers.R0,
            ARM11Registers.R0))
        .add(InstructionFactory.createBranchLink(new Label("free")))
        .add(InstructionFactory.createLoad(ARM11Registers.R0,
            ARM11Registers.SP))
        .add(InstructionFactory.createBranchLink(new Label("free")))
        .add(InstructionFactory.createLoad(ARM11Registers.R0, ARM11Registers.R0,
            new Immediate(4L)))
        .add(InstructionFactory.createPop(ARM11Registers.R0))
        .add(InstructionFactory.createBranchLink(new Label("free")))
        .add(InstructionFactory.createPop(ARM11Registers.PC));
    return list;
  }
}
