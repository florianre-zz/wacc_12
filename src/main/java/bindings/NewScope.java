package bindings;

import org.antlr.v4.runtime.ParserRuleContext;

import java.util.Hashtable;

public class NewScope extends Binding {

  private Hashtable<String, Binding> symbolTable;

  public NewScope(String name, ParserRuleContext ctx, Hashtable<String, Binding> symbolTable) {
    super(name, ctx);
    this.symbolTable = symbolTable;
  }

  public Hashtable<String, Binding> getSymbolTable() {
    return symbolTable;
  }
}
