package bindings;

public class PairType extends Type {

  private Type fst, snd;

  public PairType(Type fst, Type snd) {
    super(Types.PAIR_T.toString());
    this.fst = fst;
    this.snd = snd;
  }

  public static boolean isPair(Type type) {
    return type instanceof PairType;
  }

  @Override
  public String toString() {
    return super.toString() + "(" + fst + ", " + snd + ")";
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;

    PairType pairType = (PairType) o;

    return !(!fst.equals(pairType.fst) || !snd.equals(pairType.snd));

  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + fst.hashCode();
    result = 31 * result + snd.hashCode();
    return result;
  }
}
