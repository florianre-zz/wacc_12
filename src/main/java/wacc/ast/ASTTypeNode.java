package wacc.ast;

import bindings.Type;

public class ASTTypeNode implements ASTNode {

  final Type type;

  public ASTTypeNode(Type type) {
    this.type = type;
  }

  @Override
  public <Result> Result accept(ASTNodeVisitor<Result> visitor) {
    return null;
  }
}
