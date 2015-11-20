package arm11;

import java.util.ArrayList;
import java.util.List;

public abstract class Instruction {

  protected InstructionType type;
  // TODO: change to a map for more readable toString functions
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

}
