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

  public static boolean isInt(Type type) {
    return Types.INT_T.isEqualTo(type.getName());
  }

  public static boolean isBool(Type type) {
    return Types.BOOL_T.isEqualTo(type.getName());
  }

  public static boolean isChar(Type type) {
    return Types.CHAR_T.isEqualTo(type.getName());
  }

  public static boolean isString(Type type) {
    return Types.STRING_T.isEqualTo(type.getName());
  }

  @Override
  public String toString() {
    return getName();
  }
}
