package wacc;

import antlr.WACCParser;
import antlr.WACCParserBaseVisitor;
import bindings.*;
import org.antlr.v4.runtime.ParserRuleContext;

import java.util.ArrayList;
import java.util.List;

public class WACCSymbolTableBuilder extends WACCParserBaseVisitor<Void> {

  private SymbolTable<String, Binding> top;
  private SymbolTable<String, Binding> workingSymTable;
  private int ifCount, whileCount, beginCount;

  public WACCSymbolTableBuilder(SymbolTable<String, Binding> top) {

    this.top = this.workingSymTable = top;
    ifCount = whileCount = beginCount = 0;
  }

  private void setWorkingSymTable(SymbolTable<String, Binding> workingSymTable) {

    this.workingSymTable = workingSymTable;
  }

  // Helper Methods

  // Returns a Type object for the given context n
  // TODO: if type doesn't exist, create it and put it in top
  private Type getType(WACCParser.TypeContext ctx) {

    return (Type) top.lookupAll(ctx.getText());
  }

  /* Sets the working symbol table to the parent of the working 
   * symbol table 
   */
  private void goUpWorkingSymTable() {

    SymbolTable<String, Binding> enclosingST = workingSymTable.getEnclosingST();
    if (enclosingST != null) {
      setWorkingSymTable(enclosingST);
    }
  }

  /* Given a context which requires a new scope, its symbol table is filled 
   * with all relevant elements of its children
   * Once all elements are added, the working symbol table is reset to its 
   * value before method call 
   */
  private Void fillNewSymbolTable(WACCParser.StatListContext ctx,
                                  SymbolTable<String, Binding> symTab) {
    setWorkingSymTable(symTab);
    super.visitChildren(ctx);
    goUpWorkingSymTable();
    return null;
  }

  /* Create a function scope
   * Puts all the function's params in its symbol table and stores them in as a
   * List in the Function that is returned
   */
  private NewScope getFuncScope(WACCParser.FuncContext funcContext,
                                SymbolTable<String, Binding> newScopeSymTab) {
    // Get list of ParamContexts from FuncContext
    List<? extends WACCParser.ParamContext> paramContexts =
        funcContext.paramList().param();

    // Get list of function parameters that will be stored as Variables
    List<Variable> funcParams = new ArrayList<>();
    for (WACCParser.ParamContext paramContext : paramContexts) {
      // Get name and type of function
      String name = paramContext.getText();
      Type type = getType(paramContext.type());

      /* Create param as a variable
       * Store it in the function's symbolTable and add the param to the
       * list of params of the function (used to create the scope)
       */
      Variable param = new Variable(name, type);
      newScopeSymTab.put(name, param);
      funcParams.add(param);
    }

    return new Function(getType(funcContext.type()),
                        funcContext.funcName.getText(), funcParams,
                        newScopeSymTab);
  }

  /* Creates a new symbol table and binding for a context which requires a
   * new scope
   */
  private Void setANewScope(ParserRuleContext ctx, String scopeName) {

    if (ctx instanceof WACCParser.IfStatContext) {
      return setIfStatScopes((WACCParser.IfStatContext) ctx);
    }
    SymbolTable<String, Binding> newScopeSymTab = new SymbolTable<>(workingSymTable);
    NewScope newScope;

    // Dealing with function
    if (ctx instanceof WACCParser.FuncContext) {
      newScope = getFuncScope((WACCParser.FuncContext) ctx, newScopeSymTab);


      // Dealing with other scopes
    } else {
      newScope = new NewScope(scopeName, newScopeSymTab);
    }

    workingSymTable.put(scopeName, newScope);

    WACCParser.StatListContext statListContext = getStatlistContext(ctx);

    return fillNewSymbolTable(statListContext, newScopeSymTab);
  }

  // Deals with special case of if statement where 2 scopes are required
  private Void setIfStatScopes(WACCParser.IfStatContext ctx) {
    setIfBranchScope("0then", ctx.thenStat);
    setIfBranchScope("0else", ctx.elseStat);
    return null;
  }

  // Create a newScope for if branches
  private void setIfBranchScope(String name,
                                WACCParser.StatListContext statList) {
    SymbolTable<String, Binding> symbolTable
        = new SymbolTable<>(workingSymTable);
    NewScope newScope = new NewScope(name + ifCount, symbolTable);
    workingSymTable.put(name + ifCount, newScope);
    fillNewSymbolTable(statList, symbolTable);
  }

  /* Given a context with only one statList this statList is returned
   * The if statement case is dealt with in setIfStatScopes()
   */
  private WACCParser.StatListContext getStatlistContext(ParserRuleContext ctx) {

    if (ctx instanceof WACCParser.FuncContext) {
      WACCParser.FuncContext funcContext = (WACCParser.FuncContext) ctx;
      return funcContext.statList();
    } else if (ctx instanceof WACCParser.MainContext) {
      WACCParser.MainContext mainContext = (WACCParser.MainContext) ctx;
      return mainContext.statList();
    } else if (ctx instanceof WACCParser.BeginStatContext) {
      WACCParser.BeginStatContext beginContext = (WACCParser.BeginStatContext) ctx;
      return beginContext.statList();
    } else { // ctx instanceof WACCParser.WhileStatContext
      WACCParser.WhileStatContext whileContext = (WACCParser.WhileStatContext) ctx;
      return whileContext.statList();
    }
  }

  // Visit Functions

  // Calls for a new scope to be created for the program
  @Override
  public Void visitProg(WACCParser.ProgContext ctx) {
    return setANewScope(ctx, "prog");
  }

  // Creates a new scope for the body of the program
  @Override
  public Void visitMain(WACCParser.MainContext ctx) {
    return setANewScope(ctx, "0main");
  }

  /* Finds the information about a function definition and calls for a new
   * scope to be created
   */
  @Override
  public Void visitFunc(WACCParser.FuncContext ctx) {
    return setANewScope(ctx, ctx.funcName.getText());
  }

  /* Calls for a new scope to be created for every new BEGIN - END
   * Each begin scope is given a unique name using a counter
   */
  @Override
  public Void visitBeginStat(WACCParser.BeginStatContext ctx) {
    String scopeName = "0begin" + ++beginCount;
    return setANewScope(ctx, scopeName);
  }

  /* Calls for a new scope to be created for every new if statement
   * Each if scope is given a unique name using a counter
   */
  @Override
  public Void visitIfStat(WACCParser.IfStatContext ctx) {
    ++ifCount;
    return setANewScope(ctx, null);
  }

  /* Calls for a new scope to be created for every new while loop
   * Each while is given a unique name using a counter
   */
  @Override
  public Void visitWhileStat(WACCParser.WhileStatContext ctx) {
    String scopeName = "0while" + ++whileCount;
    visitExpr(ctx.expr());
    return setANewScope(ctx, scopeName);
  }

  /* Adds a newly initialised variable to the working symbol table
   * The variable is checked so that it is not declared twice in the same scope
   * and has at least been declared in an ancestor scope
   * If the RHS includes functions or variables, these also have to be
   * checked for existence
   */
  // TODO: check if while or else or then to deal with declaration
  // TODO: check RHS identifiers exist
  // TODO: visit RHS before LHS (avoid int a = a)
  // TODO: boolean startsWith(String prefix)
  @Override
  public Void visitInitStat(WACCParser.InitStatContext ctx) {
    String varName = ctx.varName.getText();
    if (ctx.type().arrayType() != null) {
      if (ctx.type().arrayType().nonArrayType().baseType() != null) {
        workingSymTable.put(varName, top.get(
                                ctx.type().arrayType().nonArrayType().baseType()
                                   .getText()));
      } else {

      }
    } else {
      if (ctx.type().nonArrayType().pairType() != null) {
        //        workingSymTable.put(varName,
        //                            new Pair(varName,
        //                                     new Type(),
        //                                     ctx.type().nonArrayType().pairType()
        //                                         .secondType));
      } else {
        workingSymTable.put(varName, new Variable(varName, new Type(
            ctx.type().nonArrayType().baseType().getText())));
      }
    }

    //return super.visitInitStat(ctx);
    return null;
  }

  // TODO: write visitIdent()
  // TODO: write visitStatList() IF NECESSARY (i.e. for order)
  // TODO: g4: atom identifiers
  // TODO: g4: comparisonOper?
  // TODO: IF & WHILE: stop redeclaration of variables in ancestor scopes
  // TODO: Allow for mutual recursion
  // TODO: Allow funcName and variableName to be the same
  // TODO: Ask Elliot who checks number of params
}