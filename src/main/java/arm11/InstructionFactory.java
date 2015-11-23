package arm11;

import java.util.ArrayList;
import java.util.List;

public class InstructionFactory {
  public static Instruction createLoad(Register dst, Operand op) {
    //TODO: Check if it can really be any operand
    List<Operand> operands = new ArrayList<>(2);
    operands.add(dst);
    operands.add(op);
    return new Instruction(InstructionType.LDR, operands) {
      @Override
      protected String printInstruction() {
        return type.toString() + " " + operands.get(0)
               + ", " + "=" + operands.get(1);
      }
    };
  }

  public static Instruction createBranchLink(final Label label) {
    List<Operand> operands = new ArrayList<>(1);
    operands.add(label);
    return new Instruction(InstructionType.BL, operands) {
      @Override
      protected String printInstruction() {
        return type.toString() + " " + operands.get(0);
      }
    };
  }

  public static Instruction createLabel(Label label) {
    List<Operand> operands = new ArrayList<>(1);
    operands.add(label);
    return new Instruction(InstructionType.LABEL, operands) {
      @Override
      protected String printInstruction() {
        return operands.get(0) + ":";
      }
    };
  }

  public static Instruction createPush(Register register) {
    List<Operand> operands = new ArrayList<>(1);
    operands.add(register);
    return new Instruction(InstructionType.PUSH, operands) {
      @Override
      protected String printInstruction() {
        return type.toString() + " {" + operands.get(0) + "}";
      }
    };
  }

  public static Instruction createPop(Register register) {
    List<Operand> operands = new ArrayList<>(1);
    operands.add(register);
    return new Instruction(InstructionType.POP, operands) {
      @Override
      protected String printInstruction() {
        return type.toString() + " {" + operands.get(0) + "}";
      }
    };
  }

  public static Instruction createLTORG() {
    return new Instruction(InstructionType.LTORG) {
      @Override
      protected String printInstruction() {
        return type.toString();
      }
    };
  }

  public static Instruction createText() {
    return new Instruction(InstructionType.TEXT) {
      @Override
      protected String printInstruction() {
        return type.toString();
      }
    };
  }

  public static Instruction createGlobal(Label mainLabel) {
    List<Operand> operands = new ArrayList<>(1);
    operands.add(mainLabel);
    return new Instruction(InstructionType.GLOBAL, operands) {
      @Override
      protected String printInstruction() {
        return type.toString() + " " + operands.get(0);
      }
    };
  }

  public static Instruction createMov(Register dst, Register src) {
    List<Operand> operands = new ArrayList<>(2);
    operands.add(dst);
    operands.add(src);
    return new Instruction(InstructionType.MOV, operands) {
      @Override
      protected String printInstruction() {
        return type.toString() + " " + operands.get(0) + " " + operands.get(1);
      }
    };
  }

  public static Instruction createMov(Register dst, long imm) {
    List<Operand> operands = new ArrayList<>(2);
    operands.add(dst);
    operands.add(new Immediate(imm));
    return new Instruction(InstructionType.MOV, operands) {
      @Override
      protected String printInstruction() {
        return type.toString() + " " + operands.get(0) + " #" + operands.get(1);
      }
    };
  }



  public static Instruction createAdd(Register dst, Register src, long imm) {
    List<Operand> operands = new ArrayList<>(3);
    operands.add(dst);
    operands.add(src);
    operands.add(new Immediate(imm));
    return new Instruction(InstructionType.ADD, operands) {
      @Override
      protected String printInstruction() {
        return type.toString() + " "
               + operands.get(0) + " "
               + operands.get(1) + " #"
               + operands.get(2);
      }
    };
  }

  public static Instruction createCompare(Register reg, long imm) {
    List<Operand> operands = new ArrayList<>(2);
    operands.add(reg);
    operands.add(new Immediate(imm));
    return new Instruction(InstructionType.CMP, operands) {
      @Override
      protected String printInstruction() {
        return type.toString() + " "
               + operands.get(0) + " "
               + operands.get(1) + " #"
               + operands.get(2);
      }
    };
  }

  public static Instruction createLoadNotEqual(Register register, Label label) {
    List<Operand> operands = new ArrayList<>(2);
    operands.add(register);
    operands.add(label);
    return new Instruction(InstructionType.LDRNE, operands) {
      @Override
      protected String printInstruction() {
        return type.toString() + " " + operands.get(0) + " " + operands.get(1);
      }
    };
  }

  public static Instruction createLoadEqual(Register register, Label label) {
    List<Operand> operands = new ArrayList<>(2);
    operands.add(register);
    operands.add(label);
    return new Instruction(InstructionType.LDREQ, operands) {
      @Override
      protected String printInstruction() {
        return type.toString() + " " + operands.get(0) + " " + operands.get(1);
      }
    };
  }

  public static Instruction createWord(int length) {
    List<Operand> operands = new ArrayList<>(1);
    operands.add(new Word(length));
    return new Instruction(InstructionType.WORD, operands) {
      @Override
      protected String printInstruction() {
        return type.toString() + " " + operands.get(0);
      }
    };
  }

  public static Instruction createAscii(String ascii) {
    List<Operand> operands = new ArrayList<>(1);
    operands.add(new Ascii(ascii));
    return new Instruction(InstructionType.ASCII, operands) {
      @Override
      protected String printInstruction() {
        return type.toString() + " \"" + operands.get(0) + "\"";
      }
    };
  }
}


