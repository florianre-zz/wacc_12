package wacc;

import antlr.WACCParserBaseVisitor;
import bindings.Binding;
import wacc.error.ErrorHandler;

public class WACCVisitor<T> extends WACCParserBaseVisitor<T> {

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
  protected SymbolTable<String, Binding> workingSymTable;
  protected final ErrorHandler errorHandler;
  protected int ifCount, whileCount, beginCount;

  public WACCVisitor(SymbolTable<String, Binding> top,
                     ErrorHandler errorHandler) {
    this.top = this.workingSymTable = top;
    this.errorHandler = errorHandler;
    this.beginCount = this.whileCount = this.ifCount = 0;
  }

  public WACCVisitor(SymbolTable<String, Binding> top) {
    this(top,null);
  }

}
