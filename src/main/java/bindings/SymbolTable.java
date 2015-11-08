package bindings;

import java.util.Dictionary;
import java.util.Hashtable;

public class SymbolTable {

    private SymbolTable enclosingST;
    private Dictionary<String, Binding> dict;

    public SymbolTable(SymbolTable enclosingST) {
        this.enclosingST = enclosingST;
        this.dict = new Hashtable<>();
    }

    public Binding add(String id, Binding binding){
        return dict.put(id, binding);
    }

    public Binding lookup(String id){
        return dict.get(id);
    }

    public Binding lookupAll(String id){
        SymbolTable currentScope = this;
        while (currentScope != null){
            Binding binding = currentScope.lookup(id);
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
