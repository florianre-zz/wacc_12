package wacc.ast;

import java.util.List;

public class ASTProgramNode implements ASTNode {
  List<ASTStatNode> main;
  List<ASTFuncNode> functions;

  public ASTProgramNode(List<ASTStatNode> main, List<ASTFuncNode> functions) {
    this.main = main;
    this.functions = functions;
  }

  @Override
  public String toString() {
    return "main: " + main + "\n functions: " + functions + "\n";
  }

  @Override
  public <Result> Result accept(ASTNodeVisitor<Result> visitor) {
    return visitor.visitProgram(main, functions);
  }


}