package wacc.ast;

import java.util.List;

public class ASTCallNode extends ASTExprNode {
  final ASTIdentNode ident;
  final List<ASTExprNode> arguments;

  public ASTCallNode(ASTIdentNode ident, List<ASTExprNode> arguments) {
    this.ident = ident;
    this.arguments = arguments;
  }

  @Override
  public String toString() {
    if (arguments.size() == 0) {
      return ident.toString() + "()";
    } else {
      String string = ident.toString() + "(";
      for (int i = 0; i < arguments.size() - 1; i++) {
        string += arguments.get(i).toString() + ", ";
      }
      string += arguments.get(arguments.size() - 1).toString() + ")";
      return string;
    }
  }

  @Override
  public <Result> Result accept(ASTNodeVisitor<Result> visitor) {
    return visitor.visitCall(type, ident, arguments);
  }
}
