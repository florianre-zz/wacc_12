package arm11;

public class Register extends Operand {

  private final int number;
  private final String name;

  public Register(int number, String name) {
    this.number = number;
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

  // TODO: implement is stack pointer or is the other one...
}
