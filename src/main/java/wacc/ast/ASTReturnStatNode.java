package wacc.ast;

public class ASTReturnStatNode extends ASTStatNode {
  final ASTExprNode expr;

  public ASTReturnStatNode(ASTExprNode expr) {
    this.expr = expr;
  }

  @Override
  public String toString() {
    return "return " + expr.toString();
  }

  @Override
  public <Result> Result accept(ASTNodeVisitor<Result> visitor) {
    return visitor.visitReturnStat(expr);
  }
}
