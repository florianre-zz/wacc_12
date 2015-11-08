package bindings;

import java.util.Dictionary;
import java.util.Hashtable;

public class SymbolTable {

    private SymbolTable enclosingST;
    private Dictionary<String, Bindings> dict;

    public SymbolTable(SymbolTable enclosingST) {
        this.enclosingST = enclosingST;
        this.dict = new Hashtable<>();
    }

    public Bindings add(String id, Bindings binding){
        return dict.put(id, binding);
    }

    public Bindings lookup(String id){
        return dict.get(id);
    }

    public Bindings lookupAll(String id){
        SymbolTable currentScope = this;
        while (currentScope != null){
            Bindings binding = currentScope.lookup(id);
            if (binding != null) {
                return binding;
            }
            currentScope = currentScope.enclosingST; // looking into next highest scope
        }
        // id is found in no other scope
        return null;
    }
}
