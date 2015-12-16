package wacc;

import bindings.Binding;
import bindings.NewScope;

import java.util.*;

public class SymbolTable<S, T> extends LinkedHashMap<S, T> {

  private String name;
  private SymbolTable<S, T> enclosingST;

  public SymbolTable() {
    this("TOP", null);
  }

  public SymbolTable(String name, SymbolTable<S, T> enclosingST) {
    this.name = name;
    this.enclosingST = enclosingST;
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

  public String getName() {
    return name;
  }

  @Override
  public String toString() {
    List<SymbolTable<S, T>> list = new LinkedList<>();
    StringBuilder sb = new StringBuilder("SymbolTable {\n");
    Iterator<S> keys = this.keySet().iterator();
    while (keys.hasNext()) {
      sb.append(getKeyElemPairString(list, keys));
    }
    sb.append("}\n");
    for (SymbolTable<S, T> symTable : list) {
      sb.append("\n").append(symTable.name).append(": ");
      sb.append(symTable);
    }
    return sb.toString();
  }

  private String getKeyElemPairString(List<SymbolTable<S, T>> list,
                                        Iterator<S> keys) {
    S element = keys.next();
    StringBuilder sb = new StringBuilder();
    sb.append("\t");
    sb.append(element);
    sb.append(": ");
    if (this.get(element) instanceof NewScope) {
      NewScope newScope = (NewScope) this.get(element);
      LinkedHashMap<String, Binding> symbolTable = newScope.getSymbolTable();
      list.add((SymbolTable<S, T>) symbolTable);
      sb.append("SymbolTable");
    } else {
      sb.append(this.get(element));
    }

    return sb.append("\n").toString();
  }


  public List<T> filterByClass(Class<?> c) {
    Iterator<T> elements = this.values().iterator();
    List<T> result = new ArrayList<>();
    while (elements.hasNext()) {
      T nextElement = elements.next();
      if (c.isInstance(nextElement)) {
        result.add(nextElement);
      }
    }
    return result;
  }

}
