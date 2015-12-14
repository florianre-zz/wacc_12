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

    String fstString = (fst instanceof PairType
        ? Types.PAIR_T.toString() : fst.toString());

    String sndString = (snd instanceof PairType
        ? Types.PAIR_T.toString() : snd.toString());

    return super.toString() + "(" + fstString + ", " + sndString + ")";
  }

  @Override
  public String toLabel() {
    String fstLabel = (fst instanceof PairType
            ? TypeLabels.PAIR_L.toString() : fst.toLabel());

    String sndLabel = (snd instanceof PairType
            ? TypeLabels.PAIR_L.toString() : snd.toLabel());

    return TypeLabels.PAIR_L.toString() + "_" + fstLabel + "_" + sndLabel;
  }

  public Type getFst() {
    return fst;
  }

  public Type getSnd() {
    return snd;
  }

  public static boolean isPair(Type type) {
    return type != null && type.equals(new PairType());
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
