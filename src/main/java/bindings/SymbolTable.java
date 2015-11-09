package bindings;

import java.util.Dictionary;
import java.util.Hashtable;

public class SymbolTable {

  private SymbolTable enclosingST;
  private Dictionary<String, Binding> dict;

  public SymbolTable() {
    this(null);
  }

  public SymbolTable(SymbolTable enclosingST) {
    this.enclosingST = enclosingST;
    this.dict = new Hashtable<>();
  }

  // Returns null if (key, value) pair doesn't exist in the dictionary
  // otherwise returns the value that exists at key
  public Binding put(String id, Binding binding){
    return dict.put(id, binding);
  }

  public Binding get(String id){
      return dict.get(id);
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
