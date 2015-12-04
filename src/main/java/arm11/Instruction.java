package arm11;

public abstract class Instruction implements IInstruction {

  @Override
  public String toString() {
    return printInstruction();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    Instruction that = (Instruction) o;
    return this.toString().equals(that.toString());
  }

  @Override
  public int hashCode() {
    return this.toString().hashCode();
  }

}
