package wacc.ast;

public interface ASTNode {
  <Result> Result accept(ASTNodeVisitor<Result> visitor);
}
