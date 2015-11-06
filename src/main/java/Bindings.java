import org.antlr.v4.runtime.ParserRuleContext;

public class Bindings {

  private String name;
  private ParserRuleContext ctx;

  public Bindings(String name, ParserRuleContext ctx) {
    this.name = name;
    this.ctx = ctx;
  }

  public ParserRuleContext getContext() {

    return ctx;
  }

  public String getName() {

    return name;
  }
}
