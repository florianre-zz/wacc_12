package arm11;

import java.util.HashMap;
import java.util.Map;
public class DataInstructions {

  public static final int EMPTY = -1;
  public static final int QUOTE_LENGTH = 2;
  private InstructionList instructionList;
  private Map<String, Label> messagesMap;
  private Map<IOFormatters, Label> printFormattersMap;
  private int labelCounter;

  public DataInstructions() {
    this.instructionList = new InstructionList();
    this.printFormattersMap = new HashMap<>();
    this.messagesMap = new HashMap<>();
    this.labelCounter = EMPTY;
    instructionList.add(InstructionFactory.createData());
  }

  public Label addUniqueString(String message) {
    if (!messagesMap.containsKey(message)) {
      labelCounter++;
      Label label = new Label("msg_" + labelCounter);
      messagesMap.put(message, label);

      instructionList.add(InstructionFactory.createLabel(label));
      String s = message.replaceAll("\\\\([0btnfr\"'])", "$1");
      int length = s.length() - QUOTE_LENGTH;
      instructionList.add(InstructionFactory.createWord(length));
      instructionList.add(InstructionFactory.createAscii(message));
    }
    return messagesMap.get(message);
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
    String s = string.replaceAll("\\\\([0btnfr\"'])", "$1");
    int length = s.length() - QUOTE_LENGTH;
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
