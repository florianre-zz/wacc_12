package arm11;

import java.util.HashMap;
import java.util.Map;
public class DataInstructions {

  public static final int EMPTY = -1;
  private InstructionList instructionList;
  private Map<PrintFormatters, Label> printFormattersMap;
  private Map<String, Label> constStringMap;
  private int labelCounter;

  public DataInstructions() {
    this.instructionList = new InstructionList();
    instructionList.add(InstructionFactory.createData());
    this.printFormattersMap = new HashMap<>();
    this.constStringMap = new HashMap<>();
    this.labelCounter = EMPTY;
  }

  public Map<String, Label> getConstStringMap() {
    return constStringMap;
  }

  public Label addPrintFormatter(PrintFormatters printFormatter) {
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

    if (!constStringMap.containsKey(string)) {
      labelCounter++;
      Label label = new Label("msg_" + labelCounter);
      constStringMap.put(string, label);

      instructionList.add(InstructionFactory.createLabel(label));
      instructionList.add(InstructionFactory.createWord(string.length()));
      instructionList.add(InstructionFactory.createAscii(string));
    }
    return constStringMap.get(string);
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
