package bindings;

import wacc.SymbolTable;

import java.util.LinkedHashMap;
import java.util.List;

public class Function extends NewScope {

  private Type type;
  private List<Variable> params;

  public Function(Type type, String name, List<Variable> params,
                  LinkedHashMap<String, Binding> symbolTable) {
    super(name, symbolTable);
    this.params = params;
    this.type = type;
  }

  public Function() {
    super("dummy", new SymbolTable<String, Binding>());
  }

  public Type getType() {
    return type;
  }

  public List<Variable> getParams() {
    return params;
  }

  @Override
  public String toString() {

    return type + " " + super.toString() + "(...)";
  }
}
