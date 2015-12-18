package wacc.ast;

public class ASTIntLiteralNode extends ASTLiteralNode {
  final Long value;

  public ASTIntLiteralNode(Long value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return value.toString();
  }

  @Override
  public <Result> Result accept(ASTNodeVisitor<Result> visitor) {
    return visitor.visitIntLiteral(type, value);
  }
}
