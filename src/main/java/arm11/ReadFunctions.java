package arm11;

public class ReadFunctions {

  private static InstructionList readInstructions(InstructionList list,
                                                  Label readLabel,
                                                  Label formatterLabel) {
    return list.add(InstructionFactory.createLabel(readLabel))
               .add(InstructionFactory.createPush(ARM11Registers.LR))
               .add(InstructionFactory.createMov(ARM11Registers.R1,
                                                 ARM11Registers.R0))
               .add(InstructionFactory.createLoad(ARM11Registers.R0,
                                                  formatterLabel))
               .add(InstructionFactory.createAdd(ARM11Registers.R0,
                                                 ARM11Registers.R0,
                                                 new Immediate((long) 4)))
               .add(InstructionFactory.createBranchLink(new Label("scanf")));
  }

  public static InstructionList readInt(DataInstructions data) {
    InstructionList list = new InstructionList();
    Label formatterLabel
        = data.addPrintFormatter(IOFormatters.INT_FORMATTER);
    list.add(readInstructions(list, new Label("p_read_int"), formatterLabel))
        .add(InstructionFactory.createPop(ARM11Registers.PC));
    return list;
  }

  public static InstructionList readChar(DataInstructions data) {
    InstructionList list = new InstructionList();
    Label formatterLabel
        = data.addPrintFormatter(IOFormatters.CHAR_FORMATTER);
    list.add(readInstructions(list, new Label("p_read_char"), formatterLabel))
        .add(InstructionFactory.createPop(ARM11Registers.PC));
    return list;
  }


}
