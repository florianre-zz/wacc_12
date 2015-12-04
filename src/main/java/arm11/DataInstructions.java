package arm11;

import java.util.HashMap;
import java.util.Map;
public class DataInstructions {

  public static final int EMPTY = -1;
  public static final int QUOTE_LENGTH = 2;
  private InstructionList instructionList;
  private Map<IOFormatters, Label> printFormattersMap;
  private int labelCounter;

  public DataInstructions() {
    this.instructionList = new InstructionList();
    instructionList.add(InstructionFactory.createData());
    this.printFormattersMap = new HashMap<>();
    this.labelCounter = EMPTY;
  }

  public Label addPrintFormatter(IOFormatters printFormatter) {
    if (!printFormattersMap.containsKey(printFormatter)) {
      labelCounter++;
      Label label = new Label("msg_" + labelCounter);
      printFormattersMap.put(printFormatter, label);

      instructionList.add(InstructionFactory.createLabel(label));
      instructionList.add(printFormatter.getInstructions());
    }
    return printFormattersMap.get(printFormatter);
  }

  public Label addConstString(String string) {
    labelCounter++;
    Label label = new Label("msg_" + labelCounter);

    instructionList.add(InstructionFactory.createLabel(label));
    int length = string.length() - QUOTE_LENGTH;
    instructionList.add(InstructionFactory.createWord(length));
    instructionList.add(InstructionFactory.createAscii(string));
    return label;
  }

  @Override
  public String toString() {
    return instructionList.toString();
  }

  private boolean hasData() {
    return labelCounter != EMPTY;
  }

  public InstructionList getInstructionList() {
    return hasData() ? instructionList : new InstructionList();
  }

}
