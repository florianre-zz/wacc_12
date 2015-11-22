package arm11;

public class ARM11Registers {

  public enum ARM11Register {
    R0, R1, R2, R3, R4, R5, R6, R7, R8, R9, R10, R11, R12,
    SP, LR, PC
  }

  private static final Register R0  = new Register(ARM11Register.R0, "r0");
  private static final Register R1  = new Register(ARM11Register.R1, "r1");
  private static final Register R2  = new Register(ARM11Register.R2, "r2");
  private static final Register R3  = new Register(ARM11Register.R3, "r3");
  private static final Register R4  = new Register(ARM11Register.R4, "r4");
  private static final Register R5  = new Register(ARM11Register.R5, "r5");
  private static final Register R6  = new Register(ARM11Register.R6, "r6");
  private static final Register R7  = new Register(ARM11Register.R7, "r7");
  private static final Register R8  = new Register(ARM11Register.R8, "r8");
  private static final Register R9  = new Register(ARM11Register.R9, "r9");
  private static final Register R10 = new Register(ARM11Register.R10, "r10");
  private static final Register R11 = new Register(ARM11Register.R11, "r11");
  private static final Register R12 = new Register(ARM11Register.R12, "r12");
  private static final Register SP  = new Register(ARM11Register.SP, "sp");
  private static final Register LR  = new Register(ARM11Register.LR, "lr");
  private static final Register PC  = new Register(ARM11Register.PC, "pc");

  private static Register[] registers;

  static {
    registers = new Register[]{R0, R1, R2, R3, R4, R5, R6, R7, R8, R9,
        R10, R11, R12, SP, LR, PC};
  }

  public static Register getRegister(ARM11Register register) {
    return registers[register.ordinal()];
  }
}
