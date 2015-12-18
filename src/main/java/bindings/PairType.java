package bindings;

public class PairType extends Type {

  private Type fst, snd;
  private boolean generic = true;

  public PairType() {
    super(Types.PAIR_T);
  }

  public PairType(Type fst, Type snd) {
    this();
    generic = false;
    this.fst = fst;
    this.snd = snd;
  }

  @Override
  public String toString() {
    if (generic) {
      return super.toString();
    }

    String fstString = fst.toString();

    String sndString = snd.toString();

    return super.toString() + "(" + fstString + ", " + sndString + ")";
  }

  @Override
  public String toLabel() {
    return TypeLabels.PAIR_L.toString();
  }

  public Type getFst() {
    return fst;
  }

  public Type getSnd() {
    return snd;
  }

  public static boolean isPair(Type type) {
    return type != null && (type instanceof PairType);
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
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    PairType p = (PairType) o;

    if (this.generic || p.generic) {
      return true;
    }
    if (!(this.fst.equals(p.fst) && this.snd.equals(p.snd))) {
      return false;
    }
    return true;

  }

  @Override
  public int hashCode() {
    return super.hashCode();
  }
}
