package wacc.ast;

public class ASTIdentNode extends ASTExprNode {

  final String name;

  public ASTIdentNode(String name) {
    this.name = name;
  }

  public String getName() { return name; }

  @Override
  public String toString() {
    return name;
  }

  @Override
  public <Result> Result accept(ASTNodeVisitor<Result> visitor) {
    return visitor.visitIdentNode(name);
  }
}
