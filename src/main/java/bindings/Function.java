package bindings;

import org.antlr.v4.runtime.ParserRuleContext;

public class Function extends Bindings {

  private Type type;
  private Variable[] params;
  private SymbolTable symbolTable;

  public Function(String name, ParserRuleContext ctx, Variable[] params,
                  SymbolTable symbolTable, Type type) {
    super(name, ctx);
    this.params = params;
    this.symbolTable = symbolTable;
    this.type = type;
  }

  public Type getType() {
    return type;
  }

  public Variable[] getParams() {
    return params;
  }

  public SymbolTable getSymbolTable() {
    return symbolTable;
  }
}
