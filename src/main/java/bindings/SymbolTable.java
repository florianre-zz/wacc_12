package bindings;

import java.util.Hashtable;

public class SymbolTable extends Hashtable<String, Binding> {

  private SymbolTable enclosingST;

  public SymbolTable() {
    this(null);
  }

  public SymbolTable(SymbolTable enclosingST) {
    this.enclosingST = enclosingST;
  }

  public Binding lookupAll(String id){
    SymbolTable currentScope = this;
    while (currentScope != null){
      Binding binding = currentScope.get(id);
      if (binding != null) {
        return binding;
      }
      currentScope = currentScope.enclosingST; // looking into next highest scope
    }
    // id is found in no other scope
    return null;
  }

  public SymbolTable getEnclosingST() {
    return enclosingST;
  }
}
