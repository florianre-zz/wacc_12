package wacc.ast;

import bindings.Type;

import java.util.List;

public class ASTNodeBaseVisitor<Result> implements ASTNodeVisitor<Result> {

  @Override
  public Result visitProgram(List<ASTStatNode> main, List<ASTFuncNode> functions) {
    return null;
  }

  @Override
  public Result visitAssignStat(ASTExprNode assignLHS, ASTExprNode assignRHS) {
    return null;
  }

  @Override
  public Result visitBeginStat(List<ASTStatNode> statements) {
    return null;
  }

  @Override
  public Result visitInitStat(ASTIdentNode ident, ASTNode assignRHS, Type type) {
    return null;
  }

  @Override
  public Result visitPrintStat(ASTExprNode expr, boolean newline) {
    return null;
  }

  @Override
  public Result visitExitStat(ASTExprNode expr) {
    return null;
  }

  @Override
  public Result visitFreeStat(ASTExprNode expr) {
    return null;
  }

  @Override
  public Result visitIfStat(ASTExprNode condition, List<ASTStatNode> thenBody, List<ASTStatNode> elseBody) {
    return null;
  }

  @Override
  public Result visitReadStat(ASTNode assignLHS) {
    return null;
  }

  @Override
  public Result visitReturnStat(ASTExprNode expr) {
    return null;
  }

  @Override
  public Result visitWhileStat(ASTExprNode condition, List<ASTStatNode> body) {
    return null;
  }

  @Override
  public Result visitArrayElem(Type type, ASTIdentNode ident, List<ASTExprNode> operands) {
    return null;
  }

  @Override
  public Result visitBinaryOp(Type type, List<BinaryOp> operators, ASTExprNode operand, List<ASTExprNode> operands) {
    return null;
  }

  @Override
  public Result visitCall(Type type, ASTIdentNode ident, List<ASTExprNode> arguments) {
    return null;
  }

  @Override
  public Result visitUnaryOp(Type type, UnaryOp operator, ASTNode operand) {
    return null;
  }

  @Override
  public Result visitFuncNode(Type type, ASTIdentNode ident, List<ASTParamNode> parameters, List<ASTStatNode> body) {
    return null;
  }

  @Override
  public Result visitParamNode(Type type, ASTIdentNode ident) {
    return null;
  }

  @Override
  public Result visitNewPairNode(ASTExprNode first, ASTExprNode second) {
    return null;
  }

  @Override
  public Result visitIdentNode(String name) {
    return null;
  }

  @Override
  public Result visitArrayLiteral(Type type, List<ASTExprNode> elems) {
    return null;
  }

  @Override
  public Result visitBoolLiteral(Type type, boolean value) {
    return null;
  }

  @Override
  public Result visitCharacterLiteral(Type type, char value) {
    return null;
  }

  @Override
  public Result visitIntLiteral(Type type, Long value) {
    return null;
  }

  @Override
  public Result visitNullPairLiteral() {
    return null;
  }

  @Override
  public Result visitStringLiteral(Type type, String value) {
    return null;
  }
}
