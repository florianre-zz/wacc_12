package bindings;


public class Pair extends Variable {

  private Type fstType;
  private Type sndType;

  public Pair(String name, Type type, Type fstType,
              Type sndType) {
    super(name, type);
    this.fstType = fstType;
    this.sndType = sndType;
  }

  public Type getFstType() {
    return fstType;
  }

  public Type getSndType() {
    return sndType;
  }
}
