package bindings;

public class PointerType extends Type {

  private Type base;
  private int dimensionality;

  public PointerType(Type base) {
    super(base.getName());
    this.dimensionality = 1;
    if (base instanceof PointerType) {
      this.dimensionality += ((PointerType) base).getDimensionality();
      this.base = ((PointerType) base).base;
    } else {
      this.base = base;
    }
  }

  public PointerType(Type base, int dimensionality) {
    super(base.getName());
    assert dimensionality > 0 : "Dimensionality must be greater than 0";
    this.dimensionality = dimensionality;
    this.base = base;
  }


  public int getDimensionality() {
    return dimensionality;
  }

  public Type getBase() {
    return base;
  }

  public static boolean isPointer(Type type) {
    return type != null && type instanceof PointerType;
  }

  public static Type createPointer(Type base, int dimensionality) {
    if (dimensionality == 0) {
      return base;
    } else {
      return new PointerType(base, dimensionality);
    }
  }

  @Override
  public String toString() {
    String asterisks = new String(new char[dimensionality]).replace("\0", "*");
    return base.toString() + asterisks;
  }

  @Override
  public int getSize() {
    return SIZE_OF_ADDRESS;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof PointerType)) {
      return false;
    }
    PointerType that = (PointerType) o;
    return this.base.equals(that.base)
            && this.dimensionality == that.dimensionality;
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + (base != null ? base.hashCode() : 0);
    result = 31 * result + dimensionality;
    return result;
  }
}
