package bindings;

public class ArrayType extends Type {

  private Type type;

  public ArrayType(Type type) {
    super(type.getName());
    this.type = type;
  }

  public static boolean isArray(Type type) {
    return type instanceof ArrayType;
  }

  @Override
  public String toString() {
    return type.toString() + "[]";
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;

    ArrayType arrayType = (ArrayType) o;

    return type == arrayType.type;

  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + type.hashCode();
    return result;
  }
}
