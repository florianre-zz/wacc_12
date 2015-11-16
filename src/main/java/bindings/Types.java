package bindings;

public enum Types {

  INT_T("int"),
  BOOL_T("bool"),
  CHAR_T("char"),
  STRING_T("string"),
  PAIR_T("pair");

  private final String name;

  Types(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return name;
  }

  public boolean isEqualTo(String str) {
    return str.equals(name);
  }

}
