package arm11;

public class RuntimeErrorFunctions {

    public static final Label P_PRINT_STRING_LABEL = new Label("p_print_string");
    public static final Label EXIT_LABEL = new Label("exit");

    public enum Error {
        DIVIDE_BY_ZERO_ERROR("p_check_divide_by_zero",
                "\"DivideByZeroError: divide or modulo by zero\\n\\0\""),
        THROW_RUNTIME_ERROR("p_throw_runtime_error", ""),
        THROW_OVERFLOW_ERROR("p_throw_overflow_error",
                "\"OverflowError: the result is too small/large to store in " +
                        "a 4-byte signed-integer.\\n\""),
        CHECK_ARRAY_BOUNDS_NEG_ERROR("p_check_array_bounds",
                "\"ArrayIndexOutOfBoundsError: negative index\\n\\0\""),
        CHECK_ARRAY_BOUNDS_TOO_LARGE_ERROR("p_check_array_bounds",
                "\"ArrayIndexOutOfBoundsError: index too large\\n\\0\""),
        CHECK_NULL_POINTER_ERROR("p_check_null_pointer",
                "\"NullReferenceError: dereference a null reference\\n\\0\"");

        private Label label;
        private String message;
        Error(String label, String message) {
            this.label = new Label(label);
            this.message = message;
        }
    }

  public static InstructionList divideByZero(DataInstructions data) {
    InstructionList list = new InstructionList();

    Label message = data.addUniqueString(Error.DIVIDE_BY_ZERO_ERROR.message);

    list.add(InstructionFactory.createLabel(Error.DIVIDE_BY_ZERO_ERROR.label))
        .add(InstructionFactory.createPush(ARM11Registers.LR))
        .add(InstructionFactory.createCompare(ARM11Registers.R1,
                                              new Immediate(0L)))
        .add(InstructionFactory.createLoadEqual(ARM11Registers.R0, message))
            .add(InstructionFactory.createBranchLinkEqual(
                Error.THROW_RUNTIME_ERROR.label))
        .add(InstructionFactory.createPop(ARM11Registers.PC));

    return list;
  }

  public static InstructionList overflowError(DataInstructions data) {
    InstructionList list = new InstructionList();

    Label message = data.addUniqueString(Error.THROW_OVERFLOW_ERROR.message);

    list.add(InstructionFactory.createLabel(Error.THROW_OVERFLOW_ERROR.label))
            .add(InstructionFactory.createLoad(ARM11Registers.R0, message))
        .add(InstructionFactory.createBranchLink(
                Error.THROW_RUNTIME_ERROR.label));

    return list;
  }

  public static InstructionList checkArrayBounds(DataInstructions data) {
    InstructionList list = new InstructionList();

    Label negErrMessage = data.addUniqueString(
            Error.CHECK_ARRAY_BOUNDS_NEG_ERROR.message);
    Label oufOfBoundIndexErrMessage = data.addUniqueString(
            Error.CHECK_ARRAY_BOUNDS_TOO_LARGE_ERROR.message);

    list.add(InstructionFactory.createLabel(
            Error.CHECK_ARRAY_BOUNDS_NEG_ERROR.label))
        .add(InstructionFactory.createPush(ARM11Registers.LR))
        .add(InstructionFactory.createCompare(ARM11Registers.R0,
                                              new Immediate(0L)))
        .add(InstructionFactory.createLoadLessThan(ARM11Registers.R0,
                                                   negErrMessage))
        .add(InstructionFactory.createBranchLinkLT(
                Error.THROW_RUNTIME_ERROR.label))
        .add(InstructionFactory.createLoad(ARM11Registers.R1,
                                           new Address(ARM11Registers.R1)))
        .add(InstructionFactory.createCompare(ARM11Registers.R0,
                                              ARM11Registers.R1))
        .add(InstructionFactory.createLoadCS(ARM11Registers.R0,
                                             oufOfBoundIndexErrMessage))
        .add(InstructionFactory.createBranchLinkCS(
                Error.THROW_RUNTIME_ERROR.label))
        .add(InstructionFactory.createPop(ARM11Registers.PC));

    return list;
  }

  public static InstructionList throwRuntimeError(DataInstructions data) {
    InstructionList list = new InstructionList();
    data.addPrintFormatter(IOFormatters.STRING_FORMATTER);

    Immediate exitCode = new Immediate(-1L);

    list.add(InstructionFactory.createLabel(Error.THROW_RUNTIME_ERROR.label))
         .add(InstructionFactory.createBranchLink(P_PRINT_STRING_LABEL))
        .add(InstructionFactory.createMove(ARM11Registers.R0, exitCode))
        .add(InstructionFactory.createBranchLink(EXIT_LABEL));

    return list;
  }

  public static InstructionList checkNullPointer(DataInstructions data) {
    InstructionList list = new InstructionList();
    data.addPrintFormatter(IOFormatters.STRING_FORMATTER);

    Label errMessage = data.addUniqueString(
            Error.CHECK_NULL_POINTER_ERROR.message);

    list.add(InstructionFactory.createLabel(
            Error.CHECK_NULL_POINTER_ERROR.label))
        .add(InstructionFactory.createPush(ARM11Registers.LR))
        .add(InstructionFactory.createCompare(ARM11Registers.R0,
                                              new Immediate(0L)))
        .add(InstructionFactory.createLoadEqual(ARM11Registers.R0, errMessage))
        .add(InstructionFactory.createBranchEqual(
                Error.THROW_RUNTIME_ERROR.label))
        .add(InstructionFactory.createPop(ARM11Registers.PC));

    return list;
  }

}
