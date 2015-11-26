package arm11;

public enum IOFormatters {

  // TODO: Check all formatters are correct

  INT_FORMATTER("%.*d\0"),
  STRING_FORMATTER("%.*s\0"),
  REFERENCE_FORMATTER("%p\0"),
  BOOL_TRUE_FORMATTER("true\0"),
  BOOL_FALSE_FORMATTER("false\0"),
  LN_FORMATTER("\0");

  private InstructionList instructions;

  IOFormatters(String formatter) {
    this.instructions = new InstructionList();
    instructions.add(InstructionFactory.createWord(formatter.length()));
    instructions.add(InstructionFactory.createAscii(formatter));
  }

  public InstructionList getInstructions() {
    return instructions;
  }
}
