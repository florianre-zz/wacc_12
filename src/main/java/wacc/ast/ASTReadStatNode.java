package wacc.ast;

public class ASTReadStatNode extends ASTStatNode {
  final ASTNode assignLHS;

  public ASTReadStatNode(ASTNode assignLHS) {
    this.assignLHS = assignLHS;
  }

  @Override
  public String toString() {
    return "read " + assignLHS.toString();
  }

  @Override
  public <Result> Result accept(ASTNodeVisitor<Result> visitor) {
    return visitor.visitReadStat(assignLHS);
  }
}
