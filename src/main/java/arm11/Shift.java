package arm11;

public class Shift extends Operand {

  public enum Shifts {
    ASR("ASR", 0);

    private String name;
    private int value;

    Shifts(String name, int value) {
      this.name = name;
      this.value = value;
    }

    public String getName() {
      return name;
    }

    public int getValue() {
      return value;
    }

    public void setValue(int value) {
      this.value = value;
    }

  }

  private Shifts shift;

  public Shift(Shifts shift, int value) {
    this.shift = shift;
    this.shift.setValue(value);
  }

  @Override
  public String toString() {
    return shift.getName() + " #" + shift.getValue();
  }
}
