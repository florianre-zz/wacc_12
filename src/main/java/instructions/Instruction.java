package instructions;

import java.util.List;

public class Instruction {

  private InstructionType type;
  private List<Argument> arguments;

  public Instruction(InstructionType type, List<Argument> arguments) {
    this.type = type;
    this.arguments = arguments;
  }

  @Override
  public String toString() {
    return "Instruction";
  }

}
