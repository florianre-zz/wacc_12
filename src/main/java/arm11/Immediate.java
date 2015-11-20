package arm11;

public class Immediate extends Operand {

  private Long value;

  public Immediate(Long value) {
    this.value = value;
  }

  @Override
  boolean isImmediate() {
    return true;
  }
}
