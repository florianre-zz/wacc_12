package wacc.ast;

import bindings.Type;

import java.util.List;

public class ASTFuncNode extends ASTExprNode {
  final ASTIdentNode ident;
  final List<ASTParamNode> parameters;
  final List<ASTStatNode> body;

  public ASTFuncNode(Type type, ASTIdentNode ident,
                     List<ASTParamNode> parameters, List<ASTStatNode> body) {
    super.type = type;
    this.ident = ident;
    this.parameters = parameters;
    this.body = body;
  }

  @Override
  public String toString() {
    String params = "(";
    if (parameters.size() != 0) {
      for (int i = 0; i < parameters.size() - 1; i++) {
        params += parameters.get(i) + ", ";
      }
      params += parameters.get(parameters.size() - 1);
    }
    params += ")";

    return "\n" + type.toString() + " "
        + ident.toString() + params + "\n\t" + body;
  }

  @Override
  public <Result> Result accept(ASTNodeVisitor<Result> visitor) {
    return visitor.visitFuncNode(type, ident, parameters, body);
  }
}
