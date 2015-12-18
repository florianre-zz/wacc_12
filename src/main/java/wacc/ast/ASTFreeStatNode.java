package wacc.ast;

public class ASTFreeStatNode extends ASTStatNode {
  public final ASTExprNode expr;

  public ASTFreeStatNode(ASTExprNode expr) {
    this.expr = expr;
  }

  @Override
  public String toString() {
    return "free " + expr.toString();
  }

  @Override
  public <Result> Result accept(ASTNodeVisitor<Result> visitor) {
    return visitor.visitFreeStat(expr);
  }
}
