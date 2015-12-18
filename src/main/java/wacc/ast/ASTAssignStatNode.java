package wacc.ast;

public class ASTAssignStatNode extends ASTStatNode {
  final ASTExprNode assignLHS;
  final ASTExprNode assignRHS;

  public ASTAssignStatNode(ASTExprNode assignLHS, ASTExprNode assignRHS) {
    this.assignLHS = assignLHS;
    this.assignRHS = assignRHS;
  }

  @Override
  public String toString() {
    return assignLHS.type.toString() + " " + assignLHS.toString()
        + " = " + assignRHS.toString();
  }

  @Override
  public <Result> Result accept(ASTNodeVisitor<Result> visitor) {
    return visitor.visitAssignStat(assignLHS, assignRHS);
  }
}
