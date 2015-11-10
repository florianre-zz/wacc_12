package wacc;

import antlr.WACCParser;
import antlr.WACCParserBaseVisitor;
import bindings.*;

import java.util.ArrayList;
import java.util.List;

public class WACCBuildSTVisitor extends WACCParserBaseVisitor<Void> {

  private SymbolTable<String, Binding> top;
  private SymbolTable<String, Binding> workingSymbTable;

  public WACCBuildSTVisitor(SymbolTable<String, Binding> top) {
    this.top = this.workingSymbTable = top;
  }

  // Helper Methods

  private Type getType(WACCParser.TypeContext ctx) {
    return (Type) top.lookupAll(ctx.getText());
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

  // Visit Functions

  @Override
  public Void visitProg(WACCParser.ProgContext ctx) {
    SymbolTable<String, Binding> programSymbTab = new SymbolTable<>(workingSymbTable);
    workingSymbTable.put("prog", new NewScope("prog", programSymbTab));
    setWorkingSymbTable(programSymbTab);
    super.visitChildren(ctx);
    goUpWorkingSymbTable();
    return null;
  }

  @Override
  public Void visitFunc(WACCParser.FuncContext ctx) {
    SymbolTable<String, Binding> funcSymbTab
        = new SymbolTable<>(workingSymbTable);
    List<Variable> funcParams = getFunctionParameters(ctx);

    Function function = new Function(ctx.funcName.getText(), funcParams,
                                     funcSymbTab, getType(ctx.type()));
    workingSymbTable.put(ctx.funcName.getText(), function);

    setWorkingSymbTable(funcSymbTab);
    super.visitChildren(ctx);
    goUpWorkingSymbTable();
    return null;
  }

  private List<Variable> getFunctionParameters(WACCParser.FuncContext ctx) {
    List<? extends WACCParser.ParamContext> paramContexts
        = ctx.paramList().param();
    List<Variable> funcParams = new ArrayList<>();

    for (WACCParser.ParamContext paramContext : paramContexts) {
      String name = paramContext.getText();
      Type type = getType(paramContext.type());
      Variable param = new Variable(name, type);
      funcParams.add(param);
    }
    return funcParams;
  }


}
