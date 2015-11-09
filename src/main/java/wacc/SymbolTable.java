package wacc;

import java.util.Hashtable;

public class SymbolTable<S, T> extends Hashtable<S, T> {

  private SymbolTable<S, T> enclosingST;

  public SymbolTable() {
    this(null);
  }

  public SymbolTable(SymbolTable<S, T> enclosingST) {
    this.enclosingST = enclosingST;
  }

  public T lookupAll(S key){
    SymbolTable<S, T> currentScope = this;
    while (currentScope != null){
      T value = currentScope.get(key);
      if (value != null) {
        return value;
      }
      // looking into next highest scope
      currentScope = currentScope.getEnclosingST();
    }
    // id is found in no other scope
    return null;
  }

  public SymbolTable<S, T> getEnclosingST() {
    return enclosingST;
  }
}
