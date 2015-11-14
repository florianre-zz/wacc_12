package wacc;

import java.util.Enumeration;
import java.util.Hashtable;

public class SymbolTable<S, T> extends Hashtable<S, T> {

  private SymbolTable<S, T> enclosingST;
  private String name;

  public SymbolTable() {
    this("TOP", null);
  }

  public SymbolTable(String name, SymbolTable<S, T> enclosingST) {
    this.enclosingST = enclosingST;
    this.name = name;
  }

  public T lookupAll(S key){
    SymbolTable<S, T> currentScope = this;
    while (currentScope != null) {
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

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("SymbolTable {\n");
    Enumeration<S> keys = this.keys();
    while (keys.hasMoreElements()) {
      S element = keys.nextElement();
      sb.append("\t");
      sb.append(element.toString());
      sb.append(": ");
      sb.append(this.get(element));
      sb.append("\n");
    }
    sb.append("}");
    return sb.toString();
  }

  public String getName() {
    return name;
  }
}
