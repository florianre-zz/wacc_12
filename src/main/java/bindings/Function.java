package bindings;

import org.antlr.v4.runtime.ParserRuleContext;

public class Function extends NewScope {

  private Type type;
  private Variable[] params;

  public Function(String name, ParserRuleContext ctx, Variable[] params,
                  SymbolTable symbolTable, Type type) {
    super(name, ctx, symbolTable);
    this.params = params;
    this.type = type;
  }

  public Type getType() {
    return type;
  }

  public Variable[] getParams() {
    return params;
  }
}
