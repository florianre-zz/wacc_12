package wacc.ast;

import java.util.List;

public class ASTParamListNode implements ASTNode {
  public List<ASTParamNode> parameters;

  public ASTParamListNode(List<ASTParamNode> parameters) {
    this.parameters = parameters;
  }

  @Override
  public <Result> Result accept(ASTNodeVisitor<Result> visitor) {
    return null;
  }
}
