package bindings;

public class ArrayType extends Type {

  private Type type;
  private int dimensionality;

  public ArrayType(Type type) {
    super(type.getName());
    dimensionality = 1;
    if (type instanceof ArrayType) {
      dimensionality += ((ArrayType) type).getDimensionality();
    }
    this.type = type;
  }

  public ArrayType(Type type, int dimensionality) {
    super(type.getName());
    assert dimensionality > 0 : "Dimensionality must be greater than 0";
    this.dimensionality = dimensionality;
    this.type = type;
  }

  public static boolean isArray(Type type) {
    return type instanceof ArrayType;
  }

  @Override
  public String toString() {
    return type.toString() + "[]";
  }

  public int getDimensionality() {
    return dimensionality;
  }

}
