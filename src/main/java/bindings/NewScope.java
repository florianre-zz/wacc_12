package bindings;

import org.antlr.v4.runtime.ParserRuleContext;

import java.util.Dictionary;
import java.util.Hashtable;

public class NewScope extends Binding {

  private Dictionary<String, Binding> symbolTable;

  public NewScope(String name, ParserRuleContext ctx, Dictionary<String, Binding> symbolTable) {
    super(name, ctx);
    this.symbolTable = symbolTable;
  }

  public Dictionary<String, Binding> getSymbolTable() {
    return symbolTable;
  }
}
