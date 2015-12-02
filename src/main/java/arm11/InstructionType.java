package arm11;

public enum InstructionType {
  LTORG(".ltorg"),
  TEXT("\n.text"),
  GLOBAL("\n.global"),
  WORD(".word"),
  ASCII(".ascii"),
  DATA(".data"),

  B("B"),
  BL("BL"),
  BEQ("BEQ"),
  BNE("BNE"),
  LABEL("label"),

  MOV("MOV"),
  MOVEQ("MOVEQ"),
  MOVNE("MOVNE"),
  MOVGT("MOVGT"),
  MOVGE("MOVGE"),
  MOVLT("MOVLT"),
  MOVLE("MOVLE"),

  LDR("LDR"),
  LDRSB("LDRSB"),
  LDRNE("LDRNE"),
  LDREQ("LDREQ"),

  STR("STR"),
  STRB("STRB"),

  PUSH("PUSH"),
  POP("POP"),

  ADD("ADD"),
  ADDS("ADDS"),
  SUB("SUB"),
  SUBS("SUBS"),
  RSBS("RSBS"),
  SMULL("SMULL"),

  AND("AND"),
  ORR("ORR"),
  EOR("EOR"),
  CMP("CMP");

  private final String name;

  InstructionType(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return "\t" + this.name;
  }

  public boolean isArithmetic() {
    switch (this) {
      case ADD:
      case ADDS:
      case SUB:
      case SUBS:
      case RSBS:
      case SMULL: return true;
      default: return false;
    }
  }


  public boolean isLogical() {
    switch (this) {
      case AND:
      case ORR:
      case EOR:
      case CMP:
        return true;
      default:
        return false;
    }
  }


  public boolean isMove() {
    switch (this) {
      case MOV:
      case MOVEQ:
      case MOVGE:
      case MOVGT:
      case MOVLE:
      case MOVLT:
      case MOVNE:
        return true;
      default:
        return false;
    }
  }


  public boolean isLoad() {
    switch (this) {
      case LDR:
      case LDREQ:
      case LDRNE:
      case LDRSB:
        return true;
      default:
        return false;
    }
  }


  public boolean isStore() {
    switch (this) {
      case STR:
      case STRB:
        return true;
      default:
        return false;
    }
  }
}
