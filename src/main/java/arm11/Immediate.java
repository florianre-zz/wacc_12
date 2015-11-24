package arm11;

public class Immediate extends Operand {

  private Long value;
  private String chr;

  public Immediate(Long value) {
    this.value = value;
  }

  public Immediate(String chr) {
    this.chr = chr;
  }

  @Override
  boolean isImmediate() {
    return true;
  }

  @Override
  public String toString() {
    return (value != null) ? value.toString() : chr;
  }
}
