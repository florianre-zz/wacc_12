package arm11;

import java.util.HashMap;
import java.util.Map;
public class DataInstructions {

  private InstructionList instructionList;
  private Map<PrintFormatters, Label> printFormattersMap;
  private Map<String, Label> constStringMap;
  private int labelCounter;

  public DataInstructions() {
    this.instructionList = new InstructionList();
    this.printFormattersMap = new HashMap<>();
    this.constStringMap = new HashMap<>();
    this.labelCounter = 0;
  }

  public Label addPrintFormatter(PrintFormatters printFormatter) {
    if (!printFormattersMap.containsKey(printFormatter)) {
      printFormattersMap.put(printFormatter, new Label("msg_" + labelCounter));
      labelCounter++;
    }
    return printFormattersMap.get(printFormatter);
  }

  public Label addConstString(String constString) {
    if (!constStringMap.containsKey(constString)) {
      constStringMap.put(constString, new Label("msg_" + labelCounter));
      labelCounter++;
    }
    return constStringMap.get(constString);
  }

  @Override
  public String toString() {
    return instructionList.toString();
  }
}
