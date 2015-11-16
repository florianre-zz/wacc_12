package bindings;

public class ArrayType extends Type {

  private Type base;
  private int dimensionality;
  private boolean generic = true;

  public ArrayType() {
    super(Types.GENERIC_ARRAY_T);
  }

  public ArrayType(Type base) {
    // Created array from type
    // T -> T[], or T[] -> T[][]

    super(base.getName());
    generic = false;
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
    generic = false;
    this.dimensionality = dimensionality;
    this.base = base;
  }

  public static boolean isArray(Type type) {
    return (type instanceof ArrayType) || Type.isString(type);
  }

  @Override
  public String toString() {

    if (generic) {
      return super.toString();
    }

    String brackets = new String(new char[dimensionality]).replace("\0", "[]");
    return base.toString() + brackets;
  }

  public int getDimensionality() {
    return dimensionality;
  }

  public Type getBase() {
    return base;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    ArrayType arrayType = (ArrayType) o;

    if(arrayType.generic || generic) {
      return true;
    }

    return super.equals(o);
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + (base != null ? base.hashCode() : 0);
    result = 31 * result + dimensionality;
    return result;
  }
}
