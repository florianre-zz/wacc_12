package bindings;

public class PairType extends Type {

  private Type fst, snd;
  private boolean parameterised = false;

  public PairType() {
    super(Types.PAIR_T);
  }

  public PairType(Type fst, Type snd) {
    this();
    parameterised = true;
    this.fst = fst;
    this.snd = snd;
  }

  @Override
  public String toString() {

    if (!parameterised) {
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
    if (!super.equals(o)) return false;

    PairType type = (PairType) o;

    if(!(type.parameterised && parameterised)) {
      return true;
    }

    if (fst != null ? !fst.equals(type.fst) : type.fst != null) return false;
    if (snd != null ? !snd.equals(type.snd) : type.snd != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + (fst != null ? fst.hashCode() : 0);
    result = 31 * result + (snd != null ? snd.hashCode() : 0);
    result = 31 * result + (parameterised ? 1 : 0);
    return result;
  }
}
