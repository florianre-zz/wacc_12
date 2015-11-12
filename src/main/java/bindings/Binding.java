package bindings;

public class Binding {

  protected String name;

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

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Binding binding = (Binding) o;

    if (this.toString() != null) {
      return this.toString().equals(binding.toString());
    } else {
      return binding.toString() == null;
    }
  }

  @Override
  public int hashCode() {
    return this.toString() != null ? this.toString().hashCode() : 0;
  }
}
