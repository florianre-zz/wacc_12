package bindings;

import java.util.LinkedHashMap;

public class NewScope extends Binding {

  private LinkedHashMap<String, Binding> symbolTable;

  public NewScope(String name, LinkedHashMap<String, Binding> symbolTable) {
    super(name);
    this.symbolTable = symbolTable;
  }

  public LinkedHashMap<String, Binding> getSymbolTable() {
    return symbolTable;
  }
}
