package arm11;

import java.util.ArrayList;
import java.util.List;

public class Instruction {

  private InstructionType type;
  private List<Operand> operands;

  public Instruction(InstructionType type, List<Operand> operands) {
    this.type = type;
    this.operands = operands;
  }

  public Instruction(InstructionType type) {
    this(type, new ArrayList<Operand>());
  }

  // TODO: make an abstract method that toString calls then in the factory
  // make an subclass of this overriding that method for custom toStrings
  @Override
  public String toString() {
    return "Instruction";
  }

}
