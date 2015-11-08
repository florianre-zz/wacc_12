package bindings;

import org.antlr.v4.runtime.ParserRuleContext;

public class Program extends Binding {

  private SymbolTable symbolTable;

  public Program(String name, ParserRuleContext ctx, SymbolTable symbolTable) {
    super(name, ctx);
    this.symbolTable = symbolTable;
  }
}
