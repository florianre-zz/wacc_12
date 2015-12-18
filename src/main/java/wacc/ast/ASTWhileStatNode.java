package wacc.ast;

import java.util.List;

public class ASTWhileStatNode extends ASTStatNode {
  final ASTExprNode condition;
  final List<ASTStatNode> body;

  public ASTWhileStatNode(ASTExprNode condition,
                          List<ASTStatNode> body) {
    this.condition = condition;
    this.body = body;
  }

  @Override
  public String toString() {
    return "while (" + condition.toString() + ") " + body.toString();
  }

  @Override
  public <Result> Result accept(ASTNodeVisitor<Result> visitor) {
    return visitor.visitWhileStat(condition, body);
  }
}
