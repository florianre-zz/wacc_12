package bindings;

import java.util.Dictionary;

public class NewScope extends Binding {

  private Dictionary<String, Binding> symbolTable;

  public NewScope(String name, Dictionary<String, Binding> symbolTable) {
    super(name);
    this.symbolTable = symbolTable;
  }

  public Dictionary<String, Binding> getSymbolTable() {
    return symbolTable;
  }
}
