package wacc.ast;

public enum UnaryOp {
  NOT,
  MINUS,
  LEN,
  ORD,
  FST,
  SND,
  CHR;

  @Override
  public String toString() {
    switch (this) {
      case NOT:
        return "!";
      case MINUS:
        return "-";
      case LEN:
        return "len";
      case ORD:
        return "ord";
      case FST:
        return "fst";
      case SND:
        return "snd";
      case CHR:
        return "chr";
      default:
        return "Unsupported Unary Operation";
    }
  }
}
