package wacc;

import antlr.WACCParser;
import antlr.WACCParserBaseVisitor;
import bindings.Program;
import bindings.SymbolTable;

public class WACCBuildSTVisitor extends WACCParserBaseVisitor<Void> {

  private SymbolTable top;
  private SymbolTable workingSymbTable;

  public WACCBuildSTVisitor(SymbolTable top) {
    this.top = top;
  }

  @Override
  public Void visitProg(WACCParser.ProgContext ctx) {
    SymbolTable programSymbTab = new SymbolTable(top);
    workingSymbTable = programSymbTab;
    top.add("prog", new Program("prog", ctx, programSymbTab));
    return super.visitProg(ctx);
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
