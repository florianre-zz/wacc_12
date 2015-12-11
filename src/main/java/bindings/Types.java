package bindings;

public enum Types {

  INT_T("int", 4),
  BOOL_T("bool", 1),
  CHAR_T("char", 1),
  STRING_T("string", 4),
  PAIR_T("pair", 4),
  GENERIC_ARRAY_T("T[]", 4),;

  private final String name;
  private final int size;

  Types(String name, int size) {
    this.name = name;
    this.size = size;
  }

  public int getSize() {
    return size;
  }

  @Override
  public String toString() {
    return name;
  }

  public boolean isEqualTo(String str) {
    return str.equals(name);
  }

}