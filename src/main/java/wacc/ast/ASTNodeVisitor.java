package wacc.ast;

import bindings.Type;

import java.util.List;

public interface ASTNodeVisitor<Result> {

  Result visitProgram(List<ASTStatNode> main, List<ASTFuncNode> functions);

  Result visitAssignStat(ASTExprNode assignLHS, ASTExprNode assignRHS);

  Result visitBeginStat(List<ASTStatNode> statements);

  Result visitInitStat(ASTIdentNode ident, ASTNode assignRHS, Type type);

  Result visitPrintStat(ASTExprNode expr, boolean newline);

  Result visitExitStat(ASTExprNode expr);

  Result visitFreeStat(ASTExprNode expr);

  Result visitIfStat(ASTExprNode condition,
                   List<ASTStatNode> thenBody,
                   List<ASTStatNode> elseBody);

  Result visitReadStat(ASTNode assignLHS);

  Result visitReturnStat(ASTExprNode expr);

  Result visitWhileStat(ASTExprNode condition, List<ASTStatNode> body);

  Result visitArrayElem(Type type, ASTIdentNode ident,
                        List<ASTExprNode> operands);

  Result visitBinaryOp(Type type,
                     List<BinaryOp> operators,
                     ASTExprNode operand,
                     List<ASTExprNode> operands);

  Result visitCall(Type type, ASTIdentNode ident, List<ASTExprNode> arguments);

  Result visitUnaryOp(Type type, UnaryOp operator, ASTNode operand);

  Result visitFuncNode(Type type,
                     ASTIdentNode ident,
                     List<ASTParamNode> parameters, List<ASTStatNode> body);


  Result visitParamNode(Type type, ASTIdentNode ident);

  Result visitNewPairNode(ASTExprNode first, ASTExprNode second);

  Result visitIdentNode(String name);

  Result visitArrayLiteral(Type type, List<ASTExprNode> elems);

  Result visitBoolLiteral(Type type, boolean value);

  Result visitCharacterLiteral(Type type, char value);

  Result visitIntLiteral(Type type, Long value);

  Result visitNullPairLiteral();

  Result visitStringLiteral(Type type, String value);
}
