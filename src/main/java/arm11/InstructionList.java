package arm11;

import java.util.ArrayList;
import java.util.List;

public class InstructionList  {

  private List<Instruction> instructions;

  public InstructionList() {
    this.instructions = new ArrayList<>();
  }

  public void add(Instruction instruction) {
    instructions.add(instruction);
  }

  public void add(InstructionList instructionList) {
    for (Instruction instruction : instructionList.getInstructions()) {
      this.instructions.add(instruction);
    }
  }

  private List<Instruction> getInstructions() {
    return instructions;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    for (Instruction instruction : instructions) {
      sb.append(instruction).append("\n");
    }
    return sb.toString();
  }
}
