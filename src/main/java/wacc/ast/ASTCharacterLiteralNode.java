package wacc.ast;

public class ASTCharacterLiteralNode extends ASTLiteralNode {
  final char value;

  public ASTCharacterLiteralNode(char value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return "'" + value + "'";
  }

  @Override
  public <Result> Result accept(ASTNodeVisitor<Result> visitor) {
    return visitor.visitCharacterLiteral(type, value);
  }
}
