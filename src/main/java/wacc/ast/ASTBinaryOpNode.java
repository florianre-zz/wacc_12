package wacc.ast;

import java.util.List;

public class ASTBinaryOpNode extends ASTExprNode {

  final List<BinaryOp> operators;
  final ASTExprNode operand;
  final List<ASTExprNode> operands;

  public ASTBinaryOpNode(ASTExprNode operand, List<BinaryOp> operators,
                         List<ASTExprNode> operands) {
    this.operators = operators;
    this.operand = operand;
    this.operands = operands;

    if (operands.size() != operators.size()) {
      throw new IllegalArgumentException("Number of operands must be "
          + "equal to the number of operators");
    }
  }

  @Override
  public String toString() {
    String string = "(" + operand.toString();
    for (int i = 0; i < operands.size(); i++) {
      string += " " + operators.get(i) + " " + operands.get(i);
    }
    string += ")";
    return string;
  }

  @Override
  public <Result> Result accept(ASTNodeVisitor<Result> visitor) {
    return visitor.visitBinaryOp(type, operators, operand, operands);
  }
}
