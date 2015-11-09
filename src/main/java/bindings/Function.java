package bindings;

import org.antlr.v4.runtime.ParserRuleContext;

import java.util.Dictionary;
import java.util.List;

public class Function extends NewScope {

  private Type type;
  private List<Variable> params;

  public Function(String name, ParserRuleContext ctx, List<Variable> params,
                  Dictionary<String, Binding> symbolTable, Type type) {
    super(name, ctx, symbolTable);
    this.params = params;
    this.type = type;
  }

  public Type getType() {
    return type;
  }

  public List<Variable> getParams() {
    return params;
  }
}
