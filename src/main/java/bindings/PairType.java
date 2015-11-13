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
}
