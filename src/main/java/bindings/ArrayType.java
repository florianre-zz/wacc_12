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

}
