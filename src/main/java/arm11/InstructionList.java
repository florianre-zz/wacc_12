package arm11;

import java.util.ArrayList;
import java.util.List;

public class InstructionList  {

  private List<Instruction> instructions;

  public InstructionList() {
    this.instructions = new ArrayList<>();
  }

  public InstructionList add(Instruction instruction) {
    instructions.add(instruction);
    return this;
  }

  public InstructionList add(InstructionList instructionList) {
    if (instructionList != null) {
      this.instructions.addAll(instructionList.getInstructions());
    }
    return this;
  }

  private List<Instruction> getInstructions() {
    return instructions;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    for (Instruction instruction : instructions) {
      sb.append(instruction.printInstruction()).append("\n");
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
<<<<<<< HEAD

    return instructions != null ? instructions.toString().hashCode() : 0;
=======
    return this.toString().hashCode();
>>>>>>> be5b645188a70f15ac2f5d116225b58bbc3975e7
  }
}
