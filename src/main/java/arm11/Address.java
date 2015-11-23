package arm11;

public class Address extends Operand {

  private Register reg;

  public Address(Register reg) {
    this.reg = reg;
  }

  @Override
  boolean isAddress() {
    return true;
  }

  @Override
  public String toString() {
    return "[" + reg.toString() + "]";
  }

}