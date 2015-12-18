package wacc.ast;

public class ASTNullPairLiteralNode extends ASTLiteralNode {
  @Override
  public String toString() {
    return "null";
  }

  @Override
  public <Result> Result accept(ASTNodeVisitor<Result> visitor) {
    return visitor.visitNullPairLiteral();
  }
}
