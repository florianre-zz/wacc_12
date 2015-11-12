package bindings;

public class Array extends Variable {

  private int dimensionality;

  public Array(String name, Type type, int dimensionality) {
    super(name, type);
    this.dimensionality = dimensionality;
  }

  public int getDimensionality() {
    return dimensionality;
  }
}
