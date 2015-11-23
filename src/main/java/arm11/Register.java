package arm11;

public class Register extends Operand {

  private final ARM11Registers.Reg register;
  private final String name;

  public Register(ARM11Registers.Reg register, String name) {
    this.register = register;
    this.name = name;
  }

  @Override
  boolean isRegister() {
    return true;
  }

  @Override
  public String toString() {
    return this.name;
  }

}
