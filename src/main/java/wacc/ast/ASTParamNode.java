package wacc.ast;

import bindings.Type;

public class ASTParamNode extends ASTExprNode {
  public final ASTIdentNode ident;

  public ASTParamNode(Type type, ASTIdentNode ident) {
    this.ident = ident;
    super.type = type;
  }

  @Override
  public String toString() {
    return ident.toString();
  }

  @Override
  public <Result> Result accept(ASTNodeVisitor<Result> visitor) {
    return visitor.visitParamNode(type, ident);
  }
}
