package wacc;

import antlr.WACCParser;
import antlr.WACCParserBaseVisitor;
import bindings.*;

public class WACCBuildSTVisitor extends WACCParserBaseVisitor<Void> {

  private SymbolTable<String, Binding> top;
  private SymbolTable<String, Binding> workingSymbTable;

  public WACCBuildSTVisitor(SymbolTable<String, Binding> top) {
    this.top = this.workingSymbTable = top;
  }

  @Override
  public Void visitProg(WACCParser.ProgContext ctx) {
    SymbolTable<String, Binding> programSymbTab = new SymbolTable<>(workingSymbTable);
    workingSymbTable.put("prog", new NewScope("prog", ctx, programSymbTab));
    setWorkingSymbTable(programSymbTab);
    super.visitChildren(ctx);
    goUpWorkingSymbTable();
    return null;
  }

  private void setWorkingSymbTable(SymbolTable<String, Binding> workingSymbTable) {
    this.workingSymbTable = workingSymbTable;
  }

  private void goUpWorkingSymbTable() {
    SymbolTable<String, Binding> enclosingST = workingSymbTable.getEnclosingST();
    if (enclosingST != null) {
      setWorkingSymbTable(enclosingST);
    }
  }
}
