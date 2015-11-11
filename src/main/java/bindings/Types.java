package bindings;

public enum Types {

  INT_T("INT_T"),
  BOOL_T("BOOL_T"),
  CHAR_T("CHAR_T"),
  STRING_T("STRING_T");

  private final String name;

  private Types(String name) {
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
