package wacc.ast;

public class ASTNewPairNode extends ASTExprNode {

  final ASTExprNode first;
  final ASTExprNode second;

  public ASTNewPairNode(ASTExprNode first, ASTExprNode second) {

    this.first = first;
    this.second = second;
  }

  @Override
  public String toString() {
    return "newpair(" + first.toString() + ", " + second.toString() + ")";
  }

  @Override
  public <Result> Result accept(ASTNodeVisitor<Result> visitor) {
    return visitor.visitNewPairNode(first, second);
  }
}
