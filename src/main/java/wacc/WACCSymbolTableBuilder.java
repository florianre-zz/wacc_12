package wacc;

import antlr.WACCParser;
import bindings.*;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.misc.NotNull;
import wacc.error.DeclarationError;
import wacc.error.SemanticError;
import wacc.error.SyntaxError;
import wacc.error.WACCErrorHandler;

import java.util.ArrayList;
import java.util.List;

public class WACCSymbolTableBuilder extends WACCVisitor<Void> {

  private WACCTypeCreator typeCreator;
  private boolean hasReturnStat, hasExitStat;

  public WACCSymbolTableBuilder(SymbolTable<String, Binding> top,
                                WACCErrorHandler errorHandler) {
    super(top, errorHandler);
    this.typeCreator = new WACCTypeCreator(top);
  }

  /***************************** Helper Method *******************************/
  
  /**
   * Given a context which requires a new scope, its symbol table is filled
   * with all relevant elements of its children
   * Once all elements are added, the working symbol table is reset to its
   * value before method call
   */
  private Void fillNewSymbolTable(ParserRuleContext ctx,
                                  SymbolTable<String, Binding> symbolTab) {
    setWorkingSymbolTable(symbolTab);
    super.visitChildren(ctx);
    goUpWorkingSymbolTable();
    return null;
  }

  /**
	 * Creates a new symbol table and binding for a context which requires a
   * new scope
   */
  private Void setANewScope(ParserRuleContext ctx, String scopeName) {
    // Deal with if scope
    if (ctx instanceof WACCParser.IfStatContext) {
      return setIfStatScope((WACCParser.IfStatContext) ctx);
    }

    SymbolTable<String, Binding> newScopeSymbolTable
        = new SymbolTable<>(scopeName, workingSymbolTable);
    NewScope newScope;
    ParserRuleContext contextToVisit;

    // Dealing with prog
    if (ctx instanceof WACCParser.ProgContext) {
      newScope = setProgScope((WACCParser.ProgContext) ctx,
                              scopeName,
                              newScopeSymbolTable);
      contextToVisit = ctx;

    // Deal with function
    } else if (ctx instanceof WACCParser.FuncContext) {
      newScope
          = getFuncScope((WACCParser.FuncContext) ctx, newScopeSymbolTable);
      contextToVisit = getStatListContext(ctx);

    // Deal with other scopes
    } else {
      newScope = new NewScope(scopeName, newScopeSymbolTable);
      contextToVisit = getStatListContext(ctx);
    }

    workingSymbolTable.put(scopeName, newScope);

    return fillNewSymbolTable(contextToVisit, newScopeSymbolTable);
  }

  /**
   * Store names of functions in prog (if any) in thw working symbol table
   */
  private NewScope setProgScope(WACCParser.ProgContext ctx,
                                String scopeName,
                                SymbolTable<String, Binding> progSymbolTable) {
    List<? extends WACCParser.FuncContext> progFuncContexts = ctx.func();
    String funcName;
      /*
       * Allows mutual recursion but does not allow overloading
       */
    Function dummy = new Function();
    for (WACCParser.FuncContext progFuncContext:progFuncContexts) {
      funcName = progFuncContext.funcName.getText();
      Binding checker = progSymbolTable.put(funcName, dummy);
      if (checker != null){
        String errorMsg
            = "Function name " + funcName + " has been used already";
        errorHandler.complain(new DeclarationError(ctx, errorMsg));
      }
    }
    return new NewScope(scopeName, progSymbolTable);
  }

  /**
	 * Create a function scope
   * Put all the function's params in its symbol table and stores them in as a
   * List in the Function that is returned
   */
  private NewScope getFuncScope(WACCParser.FuncContext funcContext,
                                SymbolTable<String, Binding>
                                    newScopeSymbolTable) {
    // Get list of function parameters that will be stored as Variables
    List<Variable> funcParams = new ArrayList<>();

    if (funcContext.paramList() != null) {

      // Get list of ParamContexts from FuncContext
      List<? extends WACCParser.ParamContext> paramContexts =
          funcContext.paramList().param();

      for (WACCParser.ParamContext paramContext : paramContexts) {
        // Get name and type of function
        String name = paramContext.name.getText();
        Type type = typeCreator.visitParam(paramContext);

      /*
       * Create param as a variable
       * Store it in the function's symbolTable and add the param to the
       * list of params of the function (used to create the scope)
       */
        Variable param = new Variable(name, type);
        Binding binding = newScopeSymbolTable.put(name, param);
        if (binding != null) {
          String errorMsg = "parameter name " + name + " already exists";
          errorHandler.complain(new DeclarationError(funcContext, errorMsg));
        }
        funcParams.add(param);
      }
    }

    return new Function(typeCreator.visitType(funcContext.type()),
        funcContext.funcName.getText(), funcParams,
        newScopeSymbolTable);
  }

  /**
   * Deal with special case of if statement where 2 scopes are required
   */
  private Void setIfStatScope(WACCParser.IfStatContext ctx) {
    boolean hasReturnBeforehand = hasReturnStat;
    boolean hasExitBeforehand = hasExitStat;
    setIfBranchScope(Scope.THEN, ctx.thenStat);
    boolean hasReturnStatInThen = hasReturnStat;
    boolean hasExitStatInThen = hasExitStat;
    setIfBranchScope(Scope.ELSE, ctx.elseStat);
    hasReturnStat &= hasReturnStatInThen;
    hasReturnStat |= hasReturnBeforehand;
    hasExitStat &= hasExitStatInThen;
    hasExitStat |= hasExitBeforehand;
    return null;
  }

  /**
   * Create a newScope for if branches
   */
  private void setIfBranchScope(Scope scope,
                                WACCParser.StatListContext statList) {
    String name = scope.toString() + ifCount;
    SymbolTable<String, Binding> symbolTable
        = new SymbolTable<>(name, workingSymbolTable);
    NewScope newScope = new NewScope(name, symbolTable);
    workingSymbolTable.put(name, newScope);
    fillNewSymbolTable(statList, symbolTable);
  }

  /**
	 * Given a context with only one statList this statList is returned
   * The if statement case is dealt with in setIfStatScopes()
   */
  private WACCParser.StatListContext getStatListContext(ParserRuleContext ctx) {
    if (ctx instanceof WACCParser.FuncContext) {
      WACCParser.FuncContext funcContext = (WACCParser.FuncContext) ctx;
      return funcContext.statList();
    } else if (ctx instanceof WACCParser.MainContext) {
      WACCParser.MainContext mainContext = (WACCParser.MainContext) ctx;
      return mainContext.statList();
    } else if (ctx instanceof WACCParser.BeginStatContext) {
      WACCParser.BeginStatContext beginContext
          = (WACCParser.BeginStatContext) ctx;
      return beginContext.statList();
    } else { // ctx instanceof WACCParser.WhileStatContext
      WACCParser.WhileStatContext whileContext
          = (WACCParser.WhileStatContext) ctx;
      return whileContext.statList();
    }
  }

  /**
   * Given a symbol table for a particular scope, this returns whether
   * that scope is a one way scope i.e. if it is a scope within an if
   * statement or a while statement
   * One way scope names begin with the digit '1'
   * Regular scopes begin with the digit '0'
   */
  private boolean isScopeOneWay(SymbolTable<String, Binding> temp) {
    return temp.getName().startsWith(ScopeType.ONE_WAY_SCOPE.toString());
  }

  /************************** Visit Functions ****************************/

  /**
   * Calls for a new scope to be created for the program
   */
  @Override
  public Void visitProg(WACCParser.ProgContext ctx) {
    setANewScope(ctx, Scope.PROG.toString());
    return null;
  }

  /**
   * Creates a new scope for the body of the program
   */
  @Override
  public Void visitMain(WACCParser.MainContext ctx) {
    hasReturnStat = false;
    setANewScope(ctx, Scope.MAIN.toString());
    if (hasReturnStat) {
      String errorMsg = "Return statement not required in body of program";
      errorHandler.complain(new SemanticError(ctx, errorMsg));
    }
    return null;
  }

  /**
	 * Finds the information about a function definition and calls for a new
   * scope to be created
   */
  @Override
  public Void visitFunc(WACCParser.FuncContext ctx) {
    hasReturnStat = false;
    hasExitStat = false;
    String funcName = ctx.funcName.getText();
    setANewScope(ctx, funcName);
    if (!hasReturnStat && !hasExitStat) {
      String errorMsg = "Return statement required in body of " + funcName;
      errorHandler.complain(new SyntaxError(ctx, errorMsg));    }
    return null;
  }

  /**
	 * Calls for a new scope to be created for every new BEGIN - END
   * Each begin scope is given a unique name using a counter
   */
  @Override
  public Void visitBeginStat(WACCParser.BeginStatContext ctx) {
    String scopeName = Scope.BEGIN.toString() + ++beginCount;
    return setANewScope(ctx, scopeName);
  }

  /**
	 * Calls for a new scope to be created for every new if statement
   * Each if scope is given a unique name using a counter
   */
  @Override
  public Void visitIfStat(WACCParser.IfStatContext ctx) {
    ++ifCount;
    visitExpr(ctx.expr());
    return setANewScope(ctx, null);
  }

  /**
	 * Calls for a new scope to be created for every new while loop
   * Each while is given a unique name using a counter
   */
  @Override
  public Void visitWhileStat(WACCParser.WhileStatContext ctx) {
    String scopeName = Scope.WHILE.toString() + ++whileCount;
    visitExpr(ctx.expr());
    return setANewScope(ctx, scopeName);
  }

  /**
	 * Adds a newly initialised variable to the working symbol table
   * The variable is checked so that it is not declared twice in the same scope
   * and has at least been declared in an ancestor scope
   * If the RHS includes functions or variables, these also have to be
   * checked for existence
   */
  @Override
  public Void visitInitStat(WACCParser.InitStatContext ctx) {
    String varName = ctx.ident().IDENT().getText();
    visitAssignRHS(ctx.assignRHS());
    Variable variable
        = new Variable(varName, typeCreator.visitType(ctx.type()));
    Binding binding = workingSymbolTable.put(varName, variable);

    // check if exists in current scope
    // no need to check if function since this can never be called within the
    // program scope
    if (binding != null) {
      String errorMsg = varName + " is already declared in current scope";
      errorHandler.complain(new DeclarationError(ctx, errorMsg));
    } else {
      SymbolTable<String, Binding> temp = workingSymbolTable;
      while (isScopeOneWay(temp)) {
        temp = temp.getEnclosingST();
        binding = temp.get(varName);
        if (binding != null) {
          String errorMsg
              = "Cannot redefine variable " + varName + " in this scope";
          errorHandler.complain(new DeclarationError(ctx, errorMsg));
          break;
        }
      }
    }
    return null;
  }

  /**
	 * This assumes that the current ident is not the LHS of an initStat or the
   * name of a function in a callStat
   * Throws error for undeclared variable (includes when IDENT is only a
   * function name)
   */
  @Override
  public Void visitIdent(WACCParser.IdentContext ctx) {
    String varName = ctx.IDENT().getText();
    Binding binding = workingSymbolTable.lookupAll(varName);
    if (binding == null || binding instanceof Function) {
      String errorMsg = "Variable " + varName + " has not been declared";
      errorHandler.complain(new DeclarationError(ctx, errorMsg));
    }
    return null;
  }

  @Override
  public Void visitCall(WACCParser.CallContext ctx) {
    if (getCalledFunction(ctx) == null) {
      String errorMsg = "Function " + ctx.funcName.getText() + " not defined";
      errorHandler.complain(new DeclarationError(ctx, errorMsg));
    }
    if (ctx.argList() == null) {
      return null;
    }
    return visitArgList(ctx.argList());
  }

  @Override
  public Void visitReturnStat(@NotNull WACCParser.ReturnStatContext ctx) {
    hasReturnStat = true;
    return super.visitReturnStat(ctx);
  }

  @Override
  public Void visitExitStat(@NotNull WACCParser.ExitStatContext ctx) {
    hasExitStat = true;
    return super.visitExitStat(ctx);
  }
}
