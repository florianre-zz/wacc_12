package arm11;

import java.util.ArrayList;
import java.util.List;

public abstract class Instruction {

  // TODO: Just the ability to print and equals

  protected InstructionType type;
  protected List<Operand> operands;

  public Instruction(InstructionType type, List<Operand> operands) {
    this.type = type;
    this.operands = operands;
  }

  public Instruction(InstructionType type) {
    this(type, new ArrayList<Operand>());
  }

  protected abstract String printInstruction();

  @Override
  public String toString() {
    return printInstruction();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    Instruction that = (Instruction) o;
    return this.toString().equals(that.toString());
  }

  @Override
  public int hashCode() {
    return this.toString().hashCode();
  }

}
