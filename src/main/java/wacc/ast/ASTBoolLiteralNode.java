package wacc.ast;

public class ASTBoolLiteralNode extends ASTLiteralNode {
  final boolean value;

  public ASTBoolLiteralNode(boolean value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return "" + value;
  }

  @Override
  public <Result> Result accept(ASTNodeVisitor<Result> visitor) {
    return visitor.visitBoolLiteral(type, value);
  }
}
