package wacc.ast;

public class ASTPrintStatNode extends ASTStatNode {
  final ASTExprNode expr;
  final boolean newline; // print / println

  public ASTPrintStatNode(ASTExprNode expr, boolean newline) {
    this.expr = expr;
    this.newline = newline;
  }

  @Override
  public String toString() {
    return (newline ? "println" : "print") + " " + expr.toString();
  }

  @Override
  public <Result> Result accept(ASTNodeVisitor<Result> visitor) {
    return visitor.visitPrintStat(expr, newline);
  }
}
