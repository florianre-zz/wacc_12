package bindings;

import java.util.Dictionary;
import java.util.Hashtable;
import org.antlr.v4.runtime.ParserRuleContext;

public class SymbolTable {

    private SymbolTable enclosingST;
    private Dictionary<String, ParserRuleContext> dict;

    public SymbolTable(SymbolTable enclosingST) {
        this.enclosingST = enclosingST;
        this.dict = new Hashtable<>();
    }

    public ParserRuleContext add(String id, ParserRuleContext value){
        return dict.put(id, value);
    }

    public ParserRuleContext lookup(String id){
        return dict.get(id);
    }

    public ParserRuleContext lookupAll(String id){
        SymbolTable currentScope = this;
        while (currentScope != null){
            ParserRuleContext value = currentScope.lookup(id);
            if (value != null) {
                return value;
            }
            currentScope = currentScope.enclosingST; // looking into next highest scope
        }
        // id is found in no other scope
        return null;
    }
}
