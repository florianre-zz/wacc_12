import org.antlr.v4.runtime.ParserRuleContext;

public class Variable extends Bindings{

  private Type type;

  public Variable(String name, ParserRuleContext ctx, Type type) {
    super(name, ctx);
    this.type = type;
  }

  public Type getType() {
    return type;
  }
}
