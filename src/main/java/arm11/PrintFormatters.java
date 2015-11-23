package arm11;

public enum PrintFormatters {

  INT_PRINT_FORMATTER("%d\0"),
  CHAR_PRINT_FORMATTER("%c\0"),
  STRING_PRINT_FORMATTER("%s\0"),
  REFERENCE_PRINT_FORMATTER("%p\0"),
  BOOL_TRUE_PRINT_FORMATTER("true\0"),
  BOOL_FALSE_PRINT_FORMATTER("false\0"),
  LN_PRINT_FORMATTER("\0");

  private InstructionList instructions;

  PrintFormatters(String formatter) {
    this.instructions = new InstructionList();
    instructions.add(InstructionFactory.createWord(formatter.length()));
    instructions.add(InstructionFactory.createAscii(formatter));
  }

}
