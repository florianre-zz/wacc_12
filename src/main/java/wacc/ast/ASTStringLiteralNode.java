package wacc.ast;

public class ASTStringLiteralNode extends ASTLiteralNode {
  final String value;

  public ASTStringLiteralNode(String value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return value;
  }

  @Override
  public <Result> Result accept(ASTNodeVisitor<Result> visitor) {
    return visitor.visitStringLiteral(type, value);
  }
}
