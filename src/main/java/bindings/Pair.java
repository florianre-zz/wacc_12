package bindings;

// TODO: delete this class

public class Pair extends Variable {

  private Type sndType;

  public Pair(String name, Type fstType,
              Type sndType) {
    super(name, fstType);
    this.sndType = sndType;
  }

  public Type getFstType() {
    return super.getType();
  }

  public Type getSndType() {
    return sndType;
  }
}
