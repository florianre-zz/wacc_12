package wacc.ast;

import java.util.List;

public class ASTArrayElemNode extends ASTExprNode {
  final ASTIdentNode ident;
  final List<ASTExprNode> operands;

  public ASTArrayElemNode(ASTIdentNode ident, List<ASTExprNode> operands) {

    this.ident = ident;
    this.operands = operands;
  }

  @Override
  public String toString() {
    String string = ident.toString();
    for (ASTExprNode operand : operands) {
      string += "[" + operand.toString() + "]";
    }
    return string;
  }

  @Override
  public <Result> Result accept(ASTNodeVisitor<Result> visitor) {
    return visitor.visitArrayElem(type, ident, operands);
  }
}
