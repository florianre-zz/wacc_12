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
    ifCount = whileCount =  beginCount = 0;
  }

  private void setWorkingSymTable(SymbolTable<String, Binding> workingSymTable) {
    this.workingSymTable = workingSymTable;
  }

  // Helper Methods
  
  // Returns a Type object for the given context 
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
  private Void fillNewSymbolTable(ParserRuleContext ctx,
                                  SymbolTable<String, Binding> symTab) {
    setWorkingSymTable(symTab);
    super.visitChildren(ctx);
    goUpWorkingSymTable();
    return null;
  }

  /* Creates a new symbol table and binding for a context which requires a 
   * new scope  
   */
  private Void setANewScope(ParserRuleContext ctx, String scopeName) {
    SymbolTable<String, Binding> symTab = new SymbolTable<>(workingSymTable);
    workingSymTable.put(scopeName, new NewScope(scopeName, symTab));
    return fillNewSymbolTable(ctx, symTab);
  }

  /* Creates a new function binding which is stored in the working 
   * symbol table
   * Creates associated symbol table for function and calls for it to be filled 
   */
  // TODO: change "int"
  private Void createFunc(WACCParser.FuncContext ctx, List<Variable> params) {
    SymbolTable<String, Binding> symTable = new SymbolTable<>(workingSymTable);
    String funcName = ctx.funcName.getText();
    Function function
        = new Function((Type) top.lookupAll("int"), funcName, params, symTable);
    workingSymTable.put(funcName, function);
    return fillNewSymbolTable(ctx, symTable);
  }

  // Visit Functions

  // Calls for a new scope to be created for the program
  @Override
  public Void visitProg(WACCParser.ProgContext ctx) {
    return setANewScope(ctx, "prog");
  }

  //TODO: Change main to NewScope()
  // Creates a new scope for the body of the program 
  @Override
  public Void visitMain(WACCParser.MainContext ctx) {
    return null; //createFunc(ctx, new ArrayList<Variable>());
  }

  /* Finds the information about a function definition and calls for a new 
   * scope to be created 
   */
  // TODO: consider collapsing private helper method
  // TODO: visit paramList first
  // TODO: then visit statList
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

    return createFunc(ctx, funcParams);

  }
  
  /* Calls for a new scope to be created for every new BEGIN - END
   * Each begin scope is given a unique name using a counter
   */
  @Override
  public Void visitBeginStat(WACCParser.BeginStatContext ctx) {
    String scopeName = "begin" + ++beginCount;
    return setANewScope(ctx, scopeName);
  }

  /* Calls for a new scope to be created for every new if statement
   * Each if scope is given a unique name using a counter
   */
  // TODO: create separate scopes for the then and else statements
  @Override
  public Void visitIfStat(WACCParser.IfStatContext ctx) {
    String scopeName = "if" + ++ifCount;
    return setANewScope(ctx, scopeName);
  }

  /* Calls for a new scope to be created for every new while loop
   * Each while is given a unique name using a counter
   */
  @Override
  public Void visitWhileStat(WACCParser.WhileStatContext ctx) {
    String scopeName = "while" + ++whileCount;
    return setANewScope(ctx, scopeName);
  }

  /* Adds a newly initialised variable to the working symbol table
   * The variable is checked so that it is not declared twice in the same scope
   * and has at least been declared in an ancestor scope
   * If the RHS includes functions or variables, these also have to be
   * checked for existence
   */
  // TODO: check RHS identifiers exist
  @Override
  public Void visitInitStat(WACCParser.InitStatContext ctx) {
    String varName = ctx.varName.getText();
    if (ctx.type().arrayType() != null) {
      if (ctx.type().arrayType().nonArrayType().baseType() != null) {
        workingSymTable.put(varName,
                            top.get(
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
        workingSymTable.put(varName,
                            new Variable(varName, new Type(ctx.type().nonArrayType().baseType().getText())));
      }
    }

    //return super.visitInitStat(ctx);
    return null;
  }

  // TODO: write visitIdent()
  // TODO: write visitParamList()
  // TODO: write visitStatList() IF NECESSARY (i.e. for order)
  // TODO: check with Elliot on atom identifiers
}
