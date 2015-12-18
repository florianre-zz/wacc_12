package wacc.ast;

public class ASTUnaryOpNode extends ASTExprNode {
  final UnaryOp operator;
  final ASTNode operand;

  public ASTUnaryOpNode(UnaryOp operator, ASTNode operand) {
    this.operator = operator;
    this.operand = operand;
  }

  @Override
  public String toString() {
    return "(" + operator.toString() + " " + operand.toString() + ")";
  }

  @Override
  public <Result> Result accept(ASTNodeVisitor<Result> visitor) {
    return visitor.visitUnaryOp(type, operator, operand);
  }
}
