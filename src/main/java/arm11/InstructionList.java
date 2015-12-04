package arm11;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class InstructionList  {

  private List<IInstruction> instructions;

  public InstructionList() {
    this.instructions = new ArrayList<>();
  }

  public InstructionList add(IInstruction instruction) {
    instructions.add(instruction);
    return this;
  }

  public InstructionList add(InstructionList instructionList) {
    if (instructionList != null) {
      this.instructions.addAll(instructionList.getInstructions());
    }
    return this;
  }

  private List<IInstruction> getInstructions() {
    return instructions;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    for (IInstruction instruction : instructions) {
      sb.append(instruction).append("\n");
    }
    return sb.toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    InstructionList that = (InstructionList) o;
    return this.toString().equals(that.toString());
  }

  @Override
  public int hashCode() {
    return instructions != null ? instructions.toString().hashCode() : 0;
  }
}
