package bindings;

public class Variable extends Binding {

  private Type type;

  // TODO: finalise?
  private long offset;

  public Variable(String name, Type type) {
    super(name);
    this.type = type;
  }

  public Type getType() {
    return type;
  }

  @Override
  public String toString() {
    return type.toString();
  }

  public long getOffset() {
    return offset;
  }

  public void setOffset(long offset) {
    this.offset = offset;
  }
}
