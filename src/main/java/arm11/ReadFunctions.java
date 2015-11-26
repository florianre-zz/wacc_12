package arm11;

public class ReadFunctions {

  public static InstructionList readInt(DataInstructions data) {
    InstructionList list = new InstructionList();

    Register r0 = ARM11Registers.getRegister(ARM11Registers.Reg.R0);
    Register r1 = ARM11Registers.getRegister(ARM11Registers.Reg.R1);
    Register lr = ARM11Registers.getRegister(ARM11Registers.Reg.LR);
    Register pc = ARM11Registers.getRegister(ARM11Registers.Reg.PC);
    Label formatterLabel
        = data.addPrintFormatter(IOFormatters.INT_FORMATTER);

    list.add(InstructionFactory.createLabel(new Label("p_read_int")));
    list.add(InstructionFactory.createPush(lr));
    list.add(InstructionFactory.createMov(r1, r0));
    list.add(InstructionFactory.createLoad(r0, formatterLabel));
    list.add(InstructionFactory.createAdd(r0, r0, new Immediate((long) 4)));
    list.add(InstructionFactory.createBranchLink(new Label("scanf")));
    list.add(InstructionFactory.createPop(pc));

    return list;
  }

  public static InstructionList readChar(DataInstructions data) {
    InstructionList list = new InstructionList();

    Register r0 = ARM11Registers.getRegister(ARM11Registers.Reg.R0);
    Register r1 = ARM11Registers.getRegister(ARM11Registers.Reg.R1);
    Register lr = ARM11Registers.getRegister(ARM11Registers.Reg.LR);
    Register pc = ARM11Registers.getRegister(ARM11Registers.Reg.PC);
    Label formatterLabel
        = data.addPrintFormatter(IOFormatters.CHAR_FORMATTER);

    list.add(InstructionFactory.createLabel(new Label("p_read_char")));
    list.add(InstructionFactory.createPush(lr));
    list.add(InstructionFactory.createMov(r1, r0));
    list.add(InstructionFactory.createLoad(r0, formatterLabel));
    list.add(InstructionFactory.createAdd(r0, r0, new Immediate((long) 4)));
    list.add(InstructionFactory.createBranchLink(new Label("scanf")));
    list.add(InstructionFactory.createPop(pc));

    return list;
  }

}
