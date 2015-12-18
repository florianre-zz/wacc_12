package wacc.ast;

public class ASTExitStatNode extends ASTStatNode {
  final ASTExprNode expr;

  public ASTExitStatNode(ASTExprNode expr) {
    this.expr = expr;
  }

  @Override
  public String toString() {
    return "exit " + expr.toString();
  }

  @Override
  public <Result> Result accept(ASTNodeVisitor<Result> visitor) {
    return visitor.visitExitStat(expr);
  }
}
