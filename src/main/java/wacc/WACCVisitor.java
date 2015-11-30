package wacc;

import antlr.WACCParser;
import antlr.WACCParserBaseVisitor;
import bindings.*;
import wacc.error.*;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;

public abstract class WACCVisitor<T> extends WACCParserBaseVisitor<T> {

  protected final SymbolTable<String, Binding> top;
  protected SymbolTable<String, Binding> workingSymbolTable;
  protected final WACCErrorHandler errorHandler;
  protected int ifCount, whileCount, beginCount;
  protected Deque<HashSet<String>> variableStack;

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
    variableStack = new ArrayDeque<>();
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

  protected void pushEmptyVariableSet() {
    HashSet<String> scope = new HashSet<>();
    variableStack.push(scope);
  }

  protected void popCurrentScopeVariableSet() {
    variableStack.pop();
  }

  protected void addVariableToCurrentScope(String name) {
    HashSet<String> current = variableStack.peek();
    current.add(name);
  }

  protected Variable getMostRecentBindingForVariable(String varName) {
    // Keep looking up the variable down the stack, if not found return null
    HashSet<String> declaredVars = variableStack.peek();
    Binding b;
    if (declaredVars.contains(varName)) {
      b = workingSymbolTable.get(varName);
    } else {
      b = workingSymbolTable.getEnclosingST().lookupAll(varName);
    }
    return (Variable) b;
  }

  protected long getAccumulativeOffsetForVariable(String varName) {
    long offset = 0;
    HashSet<String> declaredVars = variableStack.peek();
    Binding b = null;
    if (declaredVars.contains(varName)) {
      b = workingSymbolTable.get(varName);
    } else {
      SymbolTable<String, Binding> currentScope = workingSymbolTable;
      while (currentScope != null) {
        Binding value = currentScope.get(varName);
        if (value != null) {
          b = value;
          break;
        }
        String scopeName = currentScope.getName();
        SymbolTable<String, Binding> parent = currentScope.getEnclosingST();
        // looking into next highest scope
        offset += ((NewScope) parent.get(scopeName)).getStackSpaceSize();
        currentScope = parent;
      }
    }
    if (b != null) {
      offset += ((Variable) b).getOffset();
    }
    return offset;
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
