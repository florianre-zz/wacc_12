package wacc;

import antlr.WACCParser;
import antlr.WACCParserBaseVisitor;
import bindings.Binding;
import bindings.Function;
import bindings.NewScope;
import wacc.error.*;

public abstract class WACCVisitor<T> extends WACCParserBaseVisitor<T> {

  protected Function getCalledFunction(WACCParser.CallContext ctx) {
    NewScope progScope = (NewScope) top.get(Scope.PROG.toString());
    String funcName = ctx.funcName.getText();
    Binding function = progScope.getSymbolTable().get(funcName);
    if (function instanceof Function) {
      return (Function) function;
    }
    return null;
  }

  protected enum ScopeType {

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

    MAIN(ScopeType.REGULAR_SCOPE + "main"),
    PROG(ScopeType.REGULAR_SCOPE + "prog"),
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

  protected final SymbolTable<String, Binding> top;
  protected SymbolTable<String, Binding> workingSymbolTable;
  protected final WACCErrorHandler errorHandler;
  protected int ifCount, whileCount, beginCount;

  public WACCVisitor(SymbolTable<String, Binding> top,
                     WACCErrorHandler errorHandler) {
    this.top = this.workingSymbolTable = top;
    this.errorHandler = errorHandler;
    this.beginCount = this.whileCount = this.ifCount = 0;
  }

  public WACCVisitor(SymbolTable<String, Binding> top) {
    this(top,null);
  }

}
