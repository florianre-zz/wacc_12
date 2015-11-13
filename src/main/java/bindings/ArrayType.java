package bindings;

public class ArrayType extends Type {

  private Type base;
  private int dimensionality;

  public ArrayType(Type base) {
    // Created array from type
    // T -> T[], or T[] -> T[][]

    super(base.getName());
    this.dimensionality = 1;
    if (base instanceof ArrayType) {
      this.dimensionality += ((ArrayType) base).getDimensionality();
      this.base = ((ArrayType) base).base;
    } else {
      this.base = base;
    }
  }

  public ArrayType(Type base, int dimensionality) {
    super(base.getName());
    assert dimensionality > 0 : "Dimensionality must be greater than 0";
    this.dimensionality = dimensionality;
    this.base = base;
  }

  public static boolean isArray(Type type) {
    return type instanceof ArrayType;
  }

  @Override
  public String toString() {
    String brackets = new String(new char[dimensionality]).replace("\0", "[]");
    return base.toString() + brackets;
  }

  public int getDimensionality() {
    return dimensionality;
  }

  public Type getBase() {
    return base;
  }
}
