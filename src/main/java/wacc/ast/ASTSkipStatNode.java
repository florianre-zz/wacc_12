package wacc.ast;

public class ASTSkipStatNode extends ASTStatNode {
  @Override
  public String toString() {
    return "skip";
  }

  @Override
  public <Result> Result accept(ASTNodeVisitor<Result> visitor) {
    return null;
  }
}
