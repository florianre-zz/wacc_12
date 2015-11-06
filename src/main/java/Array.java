import org.antlr.v4.runtime.ParserRuleContext;

public class Array extends Variable {

  private int dimensionality;

  public Array(String name, ParserRuleContext ctx, Type type,
               int dimensionality) {
    super(name, ctx, type);
    this.dimensionality = dimensionality;
  }

  public int getDimensionality() {
    return dimensionality;
  }
}
