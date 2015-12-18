package wacc.ast;

import java.util.List;

public class ASTStatListNode implements ASTNode {

  public List<ASTStatNode> statements;

  public ASTStatListNode(List<ASTStatNode> statements) {
    this.statements = statements;
  }

  @Override
  public <Result> Result accept(ASTNodeVisitor<Result> visitor) {
    return null;
  }
}
