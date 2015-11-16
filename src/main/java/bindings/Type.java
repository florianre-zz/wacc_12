package bindings;

public class Type extends Binding {

  private Integer min, max;

  public Type(String name) {
    super(name);
  }

  public Type(Types type) {
    this(type.toString());
  }

  public Type(Types type, int min, int max) {
    this(type);
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
    return type.equals(new Type(Types.INT_T));
  }

  public static boolean isBool(Type type) {
    return type.equals(new Type(Types.BOOL_T));
  }

  public static boolean isChar(Type type) {
    return type.equals(new Type(Types.CHAR_T));
  }

  public static boolean isString(Type type) {
    return type.equals(new Type(Types.STRING_T));
  }

  @Override
  public String toString() {
    return getName();
  }
}
