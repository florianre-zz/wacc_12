package arm11;

public class Label extends Operand {

  private String value;

  public Label(String value) {
    this.value = value;
  }

  @Override
  boolean isLabel() {
    return true;
  }

  @Override
  public String toString() {
    return value;
  }
}
