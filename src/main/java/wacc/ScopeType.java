package wacc;

enum ScopeType {

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
