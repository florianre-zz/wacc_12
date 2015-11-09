package bindings;

import org.antlr.v4.runtime.ParserRuleContext;

public class Variable extends Binding {

  private Type type;

  public Variable(String name, Type type) {
    super(name);
    this.type = type;
  }

  public Type getType() {
    return type;
  }
}
