package bindings;

public class PointerType extends Type{

  private Type base;
  private int dimensionality;

  public PointerType(Type base) {
    super(base.getName());
    this.dimensionality = 1;
    if (this.base instanceof PointerType) {
      this.dimensionality += ((PointerType) this.base).getDimensionality();
      this.base = ((PointerType) this.base).base;
    } else {
      this.base = base;
    }
  }

  public int getDimensionality() {
    return dimensionality;
  }
}
