package wacc;

import antlr.WACCParser;
import antlr.WACCParserBaseVisitor;
import bindings.*;
import wacc.error.*;

import java.util.ArrayDeque;
import java.util.Deque;

public abstract class WACCVisitor<T> extends WACCParserBaseVisitor<T> {

  protected final SymbolTable<String, Binding> top;
  protected SymbolTable<String, Binding> workingSymbolTable;
  protected final WACCErrorHandler errorHandler;
  protected int ifCount, whileCount, beginCount;
  protected Deque<SymbolTable<String, Type>> variableSymbolTableStack;

  public WACCVisitor(SymbolTable<String, Binding> top) {
    this(top, null);
  }

  public WACCVisitor(SymbolTable<String, Binding> top,
                     WACCErrorHandler errorHandler) {
    this.top = top;
    this.workingSymbolTable = top;
    this.errorHandler = errorHandler;
    this.beginCount = 0;
    this.whileCount = 0;
    this.ifCount = 0;
    variableSymbolTableStack = new ArrayDeque<>();
  }

  protected Function getCalledFunction(WACCParser.CallContext ctx) {
    NewScope progScope = (NewScope) top.get(Scope.PROG.toString());
    String funcName = ScopeType.FUNCTION_SCOPE + ctx.funcName.getText();
    Binding function = progScope.getSymbolTable().get(funcName);
    if (function instanceof Function) {
      return (Function) function;
    }
    return null;
  }

  protected void setWorkingSymbolTable(
      SymbolTable<String, Binding> workingSymbolTable) {
    this.workingSymbolTable = workingSymbolTable;
  }

  protected Type getType(Types type) {
    return (Type) top.get(type.toString());
  }

  /**
   * Sets the working symbol table to the parent of the working
   * symbol table
   */
  protected void goUpWorkingSymbolTable() {
    SymbolTable<String, Binding> enclosingST
        = workingSymbolTable.getEnclosingST();
    if (enclosingST != null) {
      setWorkingSymbolTable(enclosingST);
    }
  }

  protected void changeWorkingSymbolTableTo(String scopeName) {
    NewScope b = (NewScope) workingSymbolTable.lookupAll(scopeName);
    if (b != null) {
      workingSymbolTable = (SymbolTable<String, Binding>) b.getSymbolTable();
    }
  }

  protected void pushEmptyVariableSymbolTable() {
    SymbolTable<String, Type> scope = new SymbolTable<>();
    variableSymbolTableStack.push(scope);
  }

  protected void popCurrentScopeVariableSymbolTable() {
    variableSymbolTableStack.pop();
  }

  protected void addVariableToCurrentScope(String name, Type type) {
    SymbolTable<String, Type> current = variableSymbolTableStack.peek();
    current.put(name, type);
  }

  protected Type getMostRecentBindingForVariable(String varname) {
    // Keep looking up the variable down the stack, if not found return null
    for (SymbolTable<String, Type> symbolTable : variableSymbolTableStack) {
      if (symbolTable.containsKey(varname)) {
        return symbolTable.get(varname);
      }
    }
    return null;
  }

  public Type lookupTypeInWorkingSymbolTable(String key) {
    Binding b = workingSymbolTable.lookupAll(key);
    if (b instanceof Variable) {
      return ((Variable) b).getType();
    }
    if (b instanceof Function) {
      return ((Function) b).getType();
    }
    return null;
  }

  protected enum ScopeType {
    FUNCTION_SCOPE("f_"),
    REGULAR_SCOPE("0"),
    ONE_WAY_SCOPE("1");

    private final String name;

    ScopeType(String name) {
      this.name = name;
    }

    @Override
    public String toString() {
      return name;
    }

  }

  protected enum Scope {
    MAIN("main"),
    PROG("prog"),
    BEGIN(ScopeType.REGULAR_SCOPE + "begin"),
    WHILE(ScopeType.ONE_WAY_SCOPE + "while"),
    THEN(ScopeType.ONE_WAY_SCOPE + "then"),
    ELSE(ScopeType.ONE_WAY_SCOPE + "else");

    private final String name;

    Scope(String name) {
      this.name = name;
    }

    @Override
    public String toString() {
      return name;
    }
  }

}
