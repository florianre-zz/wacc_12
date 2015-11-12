package bindings;

public class PairType extends Type {

  private Type fst, snd;

  public PairType(Type fst, Type snd) {
    super(Types.PAIR_T);
    this.fst = fst;
    this.snd = snd;
  }

  @Override
  public String toString() {
    return super.toString() + "(" + fst + ", " + snd + ")";
  }

  public Type getFst() {
    return fst;
  }

  public Type getSnd() {
    return snd;
  }
}
