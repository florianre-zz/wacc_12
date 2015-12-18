package wacc.ast;

import bindings.Type;

public class ASTInitStatNode extends ASTStatNode {

  final ASTIdentNode ident;
  final ASTNode assignRHS;
  final Type type;

  public ASTInitStatNode(Type type, ASTIdentNode ident, ASTNode assignRHS) {
    this.ident = ident;
    this.assignRHS = assignRHS;
    this.type = type;
  }

  @Override
  public String toString() {
    return ident.toString() + " = " + assignRHS.toString();
  }

  @Override
  public <Result> Result accept(ASTNodeVisitor<Result> visitor) {
    return visitor.visitInitStat(ident, assignRHS, type);
  }
}
