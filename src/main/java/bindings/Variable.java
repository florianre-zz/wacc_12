package bindings;

public class Variable extends Binding {

  private Type type;

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
}
