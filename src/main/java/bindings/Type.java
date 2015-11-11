package bindings;

public class Type extends Binding {

  private Integer min, max;

  public Type(String name) {
    super(name);
  }

  public Type(String name, int min, int max) {
    this(name);
    this.min = min;
    this.max = max;
  }

  public int getMin() {
    return min;
  }

  public int getMax() {
    return max;
  }
}
