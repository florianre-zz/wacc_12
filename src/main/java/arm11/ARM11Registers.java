package arm11;

public class ARM11Registers {

  protected enum Reg {
    R0, R1, R2, R3, R4, R5, R6, R7, R8, R9, R10, R11, R12,
    SP, LR, PC
  }

  public static final Register R0  = new Register(Reg.R0, "r0");
  public static final Register R1  = new Register(Reg.R1, "r1");
  public static final Register R2  = new Register(Reg.R2, "r2");
  public static final Register R3  = new Register(Reg.R3, "r3");
  public static final Register R4  = new Register(Reg.R4, "r4");
  public static final Register R5  = new Register(Reg.R5, "r5");
  public static final Register R6  = new Register(Reg.R6, "r6");
  public static final Register R7  = new Register(Reg.R7, "r7");
  public static final Register R8  = new Register(Reg.R8, "r8");
  public static final Register R9  = new Register(Reg.R9, "r9");
  public static final Register R10 = new Register(Reg.R10, "r10");
  public static final Register R11 = new Register(Reg.R11, "r11");
  public static final Register R12 = new Register(Reg.R12, "r12");
  public static final Register SP  = new Register(Reg.SP, "sp");
  public static final Register LR  = new Register(Reg.LR, "lr");
  public static final Register PC  = new Register(Reg.PC, "pc");

}
