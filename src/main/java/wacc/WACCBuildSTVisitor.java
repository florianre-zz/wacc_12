package wacc;

import antlr.WACCParser;
import antlr.WACCParserBaseVisitor;
import bindings.*;
import org.antlr.v4.runtime.ParserRuleContext;

import java.util.ArrayList;
import java.util.List;

public class WACCBuildSTVisitor extends WACCParserBaseVisitor<Void> {

  private SymbolTable<String, Binding> top;
  private SymbolTable<String, Binding> workingSymTable;
  private int ifCount, whileCount, beginCount;

  public WACCBuildSTVisitor(SymbolTable<String, Binding> top) {
    this.top = this.workingSymTable = top;
    ifCount = whileCount =  beginCount = 0;
  }

  private void setWorkingSymTable(SymbolTable<String, Binding> workingSymTable) {
    this.workingSymTable = workingSymTable;
  }

  // Helper Methods

  private Type getType(WACCParser.TypeContext ctx) {
    return (Type) top.get(ctx.getText());
  }

  private void goUpWorkingSymTable() {
    SymbolTable<String, Binding> enclosingST = workingSymTable.getEnclosingST();
    if (enclosingST != null) {
      setWorkingSymTable(enclosingST);
    }
  }

  private Void fillNewSymbolTable(ParserRuleContext ctx,
                                  SymbolTable<String, Binding> symTab) {
    setWorkingSymTable(symTab);
    super.visitChildren(ctx);
    goUpWorkingSymTable();
    return null;
  }

  private Void setANewScope(ParserRuleContext ctx, String scopeName) {
    SymbolTable<String, Binding> symTab = new SymbolTable<>(workingSymTable);
    workingSymTable.put(scopeName, new NewScope(scopeName, symTab));
    return fillNewSymbolTable(ctx, symTab);
  }

  private Void createFunc(ParserRuleContext ctx, String s,
                          List<Variable> params) {
    SymbolTable<String, Binding> symTable = new SymbolTable<>(workingSymTable);
    Function function
        = new Function((Type) top.lookupAll("int"), s, params, symTable);
    workingSymTable.put(s, function);
    return fillNewSymbolTable(ctx, symTable);
  }

  private Array getArrayType(WACCParser.ArrayTypeContext ctx) {
    Array arrayType;
    if () {
      int arrayDimensionality = ctx.OPEN_BRACKET().size();
      Type arrayElemsType;
      if (ctx.nonArrayType().baseType() != null) {
        arrayElemsType = (Type) top.get(ctx.nonArrayType().baseType().getText());
      } else {
        arrayElemsType = getPairType(ctx.nonArrayType().pairType());
      }
      arrayType = new Array(arrayElemsType.getName(), arrayElemsType,
                                  arrayDimensionality);
    } else {

    }
    return arrayType;
  }

  private Pair getPairType(WACCParser.PairTypeContext ctx) {

    return null;
  }

  // Visit Functions

  @Override
  public Void visitMain(WACCParser.MainContext ctx) {
    return createFunc(ctx, "0main", new ArrayList<Variable>());
  }

  @Override
  public Void visitFunc(WACCParser.FuncContext ctx) {
    List<? extends WACCParser.ParamContext> paramContexts
        = ctx.paramList().param();

    List<Variable> funcParams = new ArrayList<>();
    for (WACCParser.ParamContext paramContext : paramContexts) {
      String name = paramContext.getText();
      Type type = getType(paramContext.type());
      Variable param = new Variable(name, type);
      funcParams.add(param);
    }

    return createFunc(ctx, ctx.funcName.getText(), funcParams);
  }

  @Override
  public Void visitProg(WACCParser.ProgContext ctx) {
    return setANewScope(ctx, "prog");
  }

  @Override
  public Void visitBeginStat(WACCParser.BeginStatContext ctx) {
    String scopeName = "begin" + ++beginCount;
    return setANewScope(ctx, scopeName);
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
  public Void visitInitStat(WACCParser.InitStatContext ctx) {
    workingSymTable.put(ctx.varName.getText(),
                        new Variable(ctx.varName.getText(),
                                     new Type(ctx.type().getText())));
    //return super.visitInitStat(ctx);
    return null;
  }

}