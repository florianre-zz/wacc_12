package wacc;

enum Scope {
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
