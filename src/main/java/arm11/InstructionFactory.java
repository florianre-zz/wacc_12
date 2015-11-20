package arm11;

import java.util.ArrayList;
import java.util.List;

public class InstructionFactory {
  public static Instruction createLoad(Operand register, Operand expr) {
    List<Operand> operands = new ArrayList<>(2);
    operands.add(register);
    operands.add(expr);
    return new Instruction(InstructionType.LDR, operands);
  }

  public static Instruction createBranchLink(Label label) {
    List<Operand> operands = new ArrayList<>(1);
    operands.add(label);
    return new Instruction(InstructionType.BL, operands);
  }

  public static Instruction createLabel(Label label) {
    List<Operand> operands = new ArrayList<>(1);
    operands.add(label);
    return new Instruction(InstructionType.LABEL, operands);
  }

  public static Instruction createPush(Register register) {
    List<Operand> operands = new ArrayList<>(1);
    operands.add(register);
    return new Instruction(InstructionType.PUSH, operands);
  }

  public static Instruction createPop(Register register) {
    List<Operand> operands = new ArrayList<>(1);
    operands.add(register);
    return new Instruction(InstructionType.POP, operands);
  }

  public static Instruction createLTORG() {
    return new Instruction(InstructionType.LTORG);
  }
}
