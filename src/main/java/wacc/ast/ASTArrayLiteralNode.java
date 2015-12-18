package wacc.ast;

import java.util.List;

public class ASTArrayLiteralNode extends ASTLiteralNode {
  final List<ASTExprNode> elems;

  public ASTArrayLiteralNode(List<ASTExprNode> elems) {
    this.elems = elems;
  }

  @Override
  public String toString() {
    return elems.toString();
  }

  @Override
  public <Result> Result accept(ASTNodeVisitor<Result> visitor) {
    return visitor.visitArrayLiteral(type, elems);
  }
}
