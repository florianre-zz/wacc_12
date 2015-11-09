package wacc;

import antlr.WACCParser;
import antlr.WACCParserBaseVisitor;
import bindings.NewScope;
import bindings.SymbolTable;

public class WACCBuildSTVisitor extends WACCParserBaseVisitor<Void> {

  private SymbolTable top;
  private SymbolTable workingSymbTable;

  public WACCBuildSTVisitor(SymbolTable top) {
    this.top = this.workingSymbTable = top;
  }

  @Override
  public Void visitProg(WACCParser.ProgContext ctx) {
    SymbolTable programSymbTab = new SymbolTable(workingSymbTable);
    workingSymbTable.put("prog", new NewScope("prog", ctx, programSymbTab));
    setWorkingSymbTable(programSymbTab);
    super.visitChildren(ctx);
    goUpWorkingSymbTable();
    return null;
  }

  private void setWorkingSymbTable(SymbolTable workingSymbTable) {
    this.workingSymbTable = workingSymbTable;
  }

  private void goUpWorkingSymbTable() {
    SymbolTable enclosingST = workingSymbTable.getEnclosingST();
    if (enclosingST != null) {
      setWorkingSymbTable(enclosingST);
    }
  }
}
