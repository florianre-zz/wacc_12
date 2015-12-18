package wacc.ast;

import java.util.List;

public class ASTArgListNode implements ASTNode {
  final List<ASTExprNode> arguments;

  public ASTArgListNode(List<ASTExprNode> arguments) {
    this.arguments = arguments;
  }

  @Override
  public <Result> Result accept(ASTNodeVisitor<Result> visitor) {
    return null;
  }
}
