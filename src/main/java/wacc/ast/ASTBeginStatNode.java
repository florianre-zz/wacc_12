package wacc.ast;

import java.util.List;

public class ASTBeginStatNode extends ASTStatNode {
  final List<ASTStatNode> statements;

  public ASTBeginStatNode(List<ASTStatNode> statements) {
    this.statements = statements;
  }

  @Override
  public String toString() {
    return "begin: " + statements.toString();
  }

  @Override
  public <Result> Result accept(ASTNodeVisitor<Result> visitor) {
    return visitor.visitBeginStat(statements);
  }
}
