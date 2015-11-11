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
    return type.getName().equals("INT_T");
  }

  public static boolean isBool(Type type) {
    return type.getName().equals("BOOL_T");
  }

  public static boolean isChar(Type type) {
    return type.getName().equals("CHAR_T");
  }

  public static boolean isString(Type type) {
    return type.getName().equals("STRING_T");
  }

  public static boolean isArray(Type type) {
    return type.getName().equals("ARRAY");
  }

  public static boolean isPair(Type type) {
    return type.getName().equals("PAIR");
  }
}
