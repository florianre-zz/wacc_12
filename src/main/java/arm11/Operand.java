package arm11;

public abstract class Operand {
  boolean isRegister() {
    return false;
  }

  boolean isImmediate() {
    return false;
  }

  boolean isAddress() {
    return false;
  }

  boolean isLabel() {
    return false;
  }
}
