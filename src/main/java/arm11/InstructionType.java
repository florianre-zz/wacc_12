package arm11;

public enum InstructionType {
  LDR("LDR"),
  BL("BL"),
  LABEL("label"),
  PUSH("PUSH"),
  POP("POP"),
  LTORG(".ltorg");

  private final String name;

  InstructionType(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return this.name;
  }
}
