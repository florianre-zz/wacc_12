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

    String fstString = (fst instanceof PairType ?
        Types.PAIR_T.toString() : fst.toString());

    String sndString = (snd instanceof PairType ?
        Types.PAIR_T.toString() : snd.toString());

    return super.toString() + "(" + fstString + ", " + sndString + ")";
  }

  public Type getFst() {
    return fst;
  }

  public Type getSnd() {
    return snd;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    PairType type = (PairType) o;

    if(type.generic || generic) {
      return true;
    }

    return super.equals(o);
  }

  @Override
  public int hashCode() {
    return super.hashCode();
  }
}
