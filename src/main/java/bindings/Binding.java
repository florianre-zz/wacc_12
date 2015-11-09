package bindings;

public class Binding {

  private String name;

  public Binding(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  @Override
  public String toString() {
    return getName();
  }

}
