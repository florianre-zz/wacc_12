package arm11;

public class Word extends Operand {

  private int word;

  public Word(int word) {
    this.word = word;
  }

  @Override
  boolean isWord() {
    return true;
  }
}
