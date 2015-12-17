package bindings;

import wacc.SymbolTable;

public class NewScope extends Binding {

  private SymbolTable<String, Binding> symbolTable;
  // TODO: make final?
  private long stackSpaceSize;

  public NewScope(String name, SymbolTable<String, Binding> symbolTable) {
    super(name);
    this.symbolTable = symbolTable;
  }

  public SymbolTable<String, Binding> getSymbolTable() {
    return symbolTable;
  }

  public long getStackSpaceSize(){
    return stackSpaceSize;
  }

  public void setStackSpaceSize(long stackSpaceSize){
    this.stackSpaceSize = stackSpaceSize;
  }
}
