package wacc;

import antlr.WACCParser;
import antlr.WACCParserBaseVisitor;
import bindings.*;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.misc.NotNull;

import java.util.ArrayList;
import java.util.List;

public class WACCBuildSTVisitor extends WACCParserBaseVisitor<Void> {

  private SymbolTable<String, Binding> top;
  private SymbolTable<String, Binding> workingSymbTable;
  private int ifCount, whileCount, beginCount;

  public WACCBuildSTVisitor(SymbolTable<String, Binding> top) {
    this.top = this.workingSymbTable = top;
    ifCount = whileCount =  beginCount = 0;
  }

  // Helper Methods

  private Type getType(WACCParser.TypeContext ctx) {
    return (Type) top.lookupAll(ctx.getText());
  }

  private void setWorkingSymbTable(SymbolTable<String, Binding> workingSymbTable) {
    this.workingSymbTable = workingSymbTable;
  }

  private void goUpWorkingSymbTable() {
    SymbolTable<String, Binding> enclosingST
        = workingSymbTable.getEnclosingST();
    if (enclosingST != null) {
      setWorkingSymbTable(enclosingST);
    }
  }

  private Void fillNewSymbolTable(ParserRuleContext ctx,
                                  SymbolTable<String, Binding> symbTab) {

    setWorkingSymbTable(symbTab);
    super.visitChildren(ctx);
    goUpWorkingSymbTable();
    return null;
  }

  private Void setANewScope(ParserRuleContext ctx, String scopeName) {
    SymbolTable<String, Binding> symbTab
        = new SymbolTable<>(workingSymbTable);

    workingSymbTable.put(scopeName, new NewScope(scopeName, symbTab));

    return fillNewSymbolTable(ctx, symbTab);
  }

  // Visit Functions

  @Override
  public Void visitProg(WACCParser.ProgContext ctx) {
    return setANewScope(ctx, "prog");
  }

  @Override
  public Void visitFunc(WACCParser.FuncContext ctx) {

    SymbolTable<String, Binding> funcSymbTab
        = new SymbolTable<>(workingSymbTable);

    List<? extends WACCParser.ParamContext> paramContexts =
        ctx.paramList().param();

    List<Variable> funcParams = new ArrayList<>();

    for (WACCParser.ParamContext paramContext : paramContexts) {
      String name = paramContext.getText();
      Type type = getType(paramContext.type());
      Variable param = new Variable(name, type);
      funcParams.add(param);
    }

    Function function =
        new Function((Type) top.lookupAll("int"), ctx.funcName.getText(),
                     funcParams,
                     funcSymbTab);
    workingSymbTable.put(ctx.funcName.getText(), function);

    return fillNewSymbolTable(ctx, funcSymbTab);
  }

  @Override
  public Void visitMain(@NotNull WACCParser.MainContext ctx) {
    SymbolTable<String, Binding> funcSymbTab
        = new SymbolTable<>(workingSymbTable);
    List<Variable> funcParams = new ArrayList<>();

//    Function function =
//        new Function(getType("int"), "0main", funcParams,
//                     funcSymbTab);
//    workingSymbTable.put(ctx.funcName.getText(), function);

    return fillNewSymbolTable(ctx, funcSymbTab);
  }

  @Override
  public Void visitIfStat(WACCParser.IfStatContext ctx) {
    String scopeName = "if" + ++ifCount;
    return setANewScope(ctx, scopeName);
  }

  @Override
  public Void visitWhileStat(WACCParser.WhileStatContext ctx) {
    String scopeName = "while" + ++whileCount;
    return setANewScope(ctx, scopeName);
  }

  @Override
  public Void visitBeginStat(WACCParser.BeginStatContext ctx) {
    String scopeName = "begin" + ++beginCount;
    return setANewScope(ctx, scopeName);
  }


}
