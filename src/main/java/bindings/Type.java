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
    return type != null && type.equals(new Type(Types.INT_T));
  }

  public static boolean isBool(Type type) {
    return type != null && type.equals(new Type(Types.BOOL_T));
  }

  public static boolean isChar(Type type) {
    return type != null && type.equals(new Type(Types.CHAR_T));
  }

  public static boolean isString(Type type) {
    return type != null && (ArrayType.isCharArray(type)
        || Types.STRING_T.isEqualTo(type.toString()));
  }

  @Override
  public String toString() {
    return getName();
  }

  @Override
  public boolean equals(Object o) {

    if (o instanceof  Type) {
      Type type = (Type) o;

      if (isString(this) && isString(type)) {
        return true;
      }
    }

    return super.equals(o);

  }

  @Override
  public int hashCode() {
    return super.hashCode();
  }
}
