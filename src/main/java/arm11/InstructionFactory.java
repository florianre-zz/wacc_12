package arm11;

import static arm11.InstructionType.*;

public class InstructionFactory {

  public static  String AEABI_IDIV = "__aeabi_idiv";
  private static  String AEABI_IDIVMOD = AEABI_IDIV + "mod";

  private static String getOptionalHash(Operand op) {
    return op.isImmediate() ? "#" : "";
  }

  public static IInstruction createLoad(Register dst,  Operand op) {
    return () -> {
      String optionalEquals = op instanceof Address ? "=" : "";
      return LDR + " " + dst + ", " + optionalEquals + op;
    };
  }

  public static IInstruction createBranch( Label label) {
    return () -> B + " " + label;
  }

  public static IInstruction createBranchLink(Label label) {
    return () -> BL + " " + label;
  }

  public static IInstruction createBranchEqual(Label label) {
    return () -> BEQ + " " + label;
  }

  public static IInstruction createLabel(Label label) {
    return () -> label + ":";
  }

  public static IInstruction createPush(Register register) {
    return () -> PUSH + " {" + register + "}";
  }

  public static IInstruction createPop(Register register) {
    return () -> POP + " {" + register + "}";
  }

  public static IInstruction createLTORG() {
    return LTORG::toString;
  }

  public static IInstruction createText() {
    return TEXT::toString;
  }

  public static IInstruction createGlobal(Label mainLabel) {
    return () -> GLOBAL + " " + mainLabel;
  }

  public static IInstruction createSub(Register dst, Register rn, Operand imm) {
    return () -> SUB + " " + dst + ", " + rn + ", #" + imm;
  }

  public static IInstruction createMove(Register dst, Operand op) {
    return () -> MOV + " " + dst + ", " + getOptionalHash(op) + op;
  }

  public static IInstruction createAdd(Register dst, Register src, Operand op) {
    return () -> ADD + " " + dst + ", " + src + ", " + getOptionalHash(op) + op;
  }

  public static IInstruction createCompare(Register reg, Operand op) {
    return () -> CMP + " " + reg + ", " + getOptionalHash(op) + op;
  }

  public static IInstruction createLoadNotEqual(Register register,
                                                Label label) {
    return () -> LDRNE + " " + register + ", =" + label;
  }

  public static IInstruction createLoadEqual(Register register,
                                             Label label) {
    return () -> LDREQ + " " + register + ", =" + label;
  }

  public static IInstruction createWord(int length) {
    return () -> WORD + " " + new Word(length);
  }

  public static IInstruction createAscii(String ascii) {
    return () -> ASCII + " " + ascii;
  }

  public static IInstruction createStore(Register src, Register base,
                                        Operand offset) {
    return () -> {
      int offsetValue = Integer.parseInt(offset.toString());
      String forceIncrement = offsetValue < 0 ? "!" : "";
      String addOffset = offsetValue != 0 ? ", #" + offset : "";
      return STR + " " + src + ", [" + base +
          addOffset + "]" + forceIncrement;
    };
  }

  public static IInstruction createStoreByte(Register src, Register base,
                                             Operand offset) {
    return () -> {
      int offsetValue = Integer.parseInt(offset.toString());
      String forceIncrement = offsetValue < 0 ? "!" : "";
      String addOffset = offsetValue != 0 ? ", #" + offset : "";
      return STRB + " " + src + ", [" + base +
          addOffset + "]" + forceIncrement;
    };
  }

  public static IInstruction createData() {
    return DATA::toString;
  }

  public static IInstruction createLoad(Register dst, Register base,
                                        Immediate offset) {
    return () -> {
      String addOffset = !offset.toString().equals("0") ? ", #" + offset : "";
      return LDR + " " + dst + ", [" + base + "]" + addOffset;
    };
  }

  public static IInstruction createLoadStoredByte(Register dst, Register base,
                                                  Immediate offset) {
    return () -> {
      String addOffset = !offset.toString().equals("0") ? ", #" + offset : "";
      return LDRSB + " " + dst + ", [" + base + "]" + addOffset;
    };
  }

  public static IInstruction createEOR(Register dst, Register src,
                                       Operand imm) {
    return () -> EOR + " " + dst + ", " + src + ", #" + imm;
  }

  public static IInstruction createRSBS(Register dst,
                                        Register src,
                                        Operand imm) {
    return () -> RSBS + " " + dst + ", " + src + ", #" + imm;
  }

  public static IInstruction createAdds(Register dst, Register src,
                                        Operand op) {
    return () -> ADDS + " " + dst + ", " + src + ", "
        + getOptionalHash(op) + op;
  }

  public static IInstruction createMovEq(Register dst,  Operand op) {
    return () -> MOVEQ + " " + dst + ", " + getOptionalHash(op) + op;
  }

  public static IInstruction createMovNe(Register dst,  Operand op) {
    return () -> MOVNE + " " + dst + ", " + getOptionalHash(op) + op;
  }

  public static IInstruction createMovGt(Register dst,  Operand op) {
    return () -> MOVGT + " " + dst + ", " + getOptionalHash(op) + op;
  }

  public static IInstruction createMovLe(Register dst,  Operand op) {
    return () -> MOVLE + " " + dst + ", " + getOptionalHash(op) + op;
  }

  public static IInstruction createMovGe(Register dst,  Operand op) {
    return () -> MOVGE + " " + dst + ", " + getOptionalHash(op) + op;
  }

  public static IInstruction createMovLt(Register dst,  Operand op) {
    return () -> MOVLT + " " + dst + ", " + getOptionalHash(op) + op;
  }

  public static IInstruction createAnd(Register dst,  Register src1,
                                       Register src2) {
    return () -> AND + " " + dst + ", " + src1 + ", " + src2;
  }

  public static IInstruction createOrr(Register dst, Register src1,
                                       Register src2) {
    return () -> ORR + " " + dst + ", " + src1 + ", " + src2;
  }

  public static IInstruction createSubs(Register dst,  Register src,
                                        Operand op) {
    return () -> SUBS + " " + dst + ", " + src + ", "
        + getOptionalHash(op) + op;
  }

  public static IInstruction createSmull(Register dst1, Register dst2,
                                         Register src1, Register src2) {
    return () -> SMULL + " " + dst1 + ", " + dst2 + ", " + src1 + ", " + src2;
  }

  public static IInstruction createDiv() {
    return () -> BL + " " + AEABI_IDIV;
  }

  public static IInstruction createMod() {
    return () -> BL + " " + AEABI_IDIVMOD;
  }

}