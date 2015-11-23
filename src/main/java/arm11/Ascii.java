package arm11;

public class Ascii extends Operand {

  private String ascii;

  public Ascii(String ascii) {
    this.ascii = ascii;
  }

  @Override
  boolean isAscii() {
    return true;
  }

  @Override
  public String toString() {
    return ascii;
  }
}
