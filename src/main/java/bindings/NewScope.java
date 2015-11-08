package bindings;

import org.antlr.v4.runtime.ParserRuleContext;

public class NewScope extends Binding {

  private SymbolTable symbolTable;

  public NewScope(String name, ParserRuleContext ctx, SymbolTable symbolTable) {
    super(name, ctx);
    this.symbolTable = symbolTable;
  }

  public SymbolTable getSymbolTable() {
    return symbolTable;
  }
}
