package wacc.ast;

import java.util.List;

public class ASTIfStatNode extends ASTStatNode {
  final ASTExprNode condition;
  final List<ASTStatNode> thenBody;
  final List<ASTStatNode> elseBody;

  public ASTIfStatNode(ASTExprNode condition, List<ASTStatNode> thenBody,
                       List<ASTStatNode> elseBody) {
    this.condition = condition;
    this.thenBody = thenBody;
    this.elseBody = elseBody;
  }

  @Override
  public String toString() {
    return "if (" + condition.toString() + ") then: " + thenBody.toString()
        + " else: " + elseBody.toString();
  }

  @Override
  public <Result> Result accept(ASTNodeVisitor<Result> visitor) {
    return visitor.visitIfStat(condition, thenBody, elseBody);
  }
}
