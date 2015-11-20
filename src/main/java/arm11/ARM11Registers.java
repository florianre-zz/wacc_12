package arm11;

public class ARM11Registers {
  private static final Register R0  = new Register(0, "r0");
  private static final Register R1  = new Register(1, "r1");
  private static final Register R2  = new Register(2, "r2");
  private static final Register R3  = new Register(3, "r3");
  private static final Register R4  = new Register(4, "r4");
  private static final Register R5  = new Register(5, "r5");
  private static final Register R6  = new Register(6, "r6");
  private static final Register R7  = new Register(7, "r7");
  private static final Register R8  = new Register(8, "r8");
  private static final Register R9  = new Register(9, "r9");
  private static final Register R10 = new Register(10, "r10");
  private static final Register R11 = new Register(11, "r11");
  private static final Register R12 = new Register(12, "r12");
  private static final Register SP  = new Register(13, "sp");
  private static final Register LR  = new Register(14, "lr");
  private static final Register PC  = new Register(15, "pc");

  private static Register[] registers;

  static {
    registers = new Register[]{R0, R1, R2, R3, R4, R5, R6, R7, R8, R9,
        R10, R11, R12, SP, LR, PC};
  }

  public static Register getRegister(int i) {
    return registers[i];
  }
}
