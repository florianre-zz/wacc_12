package wacc.ast;

import antlr.WACCParser;
import bindings.*;
import org.antlr.v4.runtime.Token;
import wacc.SymbolTable;
import wacc.WACCVisitor;

import java.util.ArrayList;
import java.util.List;

public class ASTBuilder extends WACCVisitor<ASTNode> {

  public ASTBuilder(SymbolTable<String, Binding> top) {
    super(top);
  }

  @Override
  public ASTNode visitProg(WACCParser.ProgContext ctx) {
    ASTStatListNode main = visitMain(ctx.main());
    List<ASTFuncNode> functions = new ArrayList<>();
    for (WACCParser.FuncContext func : ctx.func()) {
      functions.add(visitFunc(func));
    }
    return new ASTProgramNode(main.statements, functions);
  }

  @Override
  public ASTStatListNode visitMain(WACCParser.MainContext ctx) {
    return visitStatList(ctx.statList());
  }

  @Override
  public ASTFuncNode visitFunc(WACCParser.FuncContext ctx) {
    ASTTypeNode type = visitType(ctx.type());
    ASTIdentNode ident = visitIdent(ctx.ident());
    ASTParamListNode params = visitParamList(ctx.paramList());
    ASTStatListNode body = visitStatList(ctx.statList());
    return new ASTFuncNode(type.type, ident, params.parameters,
        body.statements);
  }

  @Override
  public ASTParamListNode visitParamList(WACCParser.ParamListContext ctx) {
    List<ASTParamNode> parameters = new ArrayList<>();
    if (ctx != null) {
      for (WACCParser.ParamContext param : ctx.param()) {
        parameters.add(visitParam(param));
      }
    }
    return new ASTParamListNode(parameters);
  }

  @Override
  public ASTStatListNode visitStatList(WACCParser.StatListContext ctx) {
    List<ASTStatNode> statements = new ArrayList<>();

    for (WACCParser.StatContext stat : ctx.stat()) {
      ASTNode node = visitStat(stat);
      statements.add((ASTStatNode) node);
    }
    return new ASTStatListNode(statements);
  }

  @Override
  public ASTArgListNode visitArgList(WACCParser.ArgListContext ctx) {
    List<ASTExprNode> arguments = new ArrayList<>();
    if (ctx != null) {
      for (WACCParser.ExprContext arg : ctx.expr()) {
        arguments.add(visitExpr(arg));
      }
    }
    return new ASTArgListNode(arguments);
  }

  @Override
  public ASTParamNode visitParam(WACCParser.ParamContext ctx) {
    ASTTypeNode type = visitType(ctx.type());
    ASTIdentNode ident = visitIdent(ctx.ident());
    return new ASTParamNode(type.type, ident);
  }

  @Override
  public ASTStatNode visitStat(WACCParser.StatContext ctx) {
    return (ASTStatNode) visit(ctx);
  }

  @Override
  public ASTSkipStatNode visitSkipStat(WACCParser.SkipStatContext ctx) {
    return new ASTSkipStatNode();
  }

  @Override
  public ASTInitStatNode visitInitStat(WACCParser.InitStatContext ctx) {
    ASTTypeNode type = visitType(ctx.type());
    ASTIdentNode ident = visitIdent(ctx.ident());
    ASTNode assignRHS = visitAssignRHS(ctx.assignRHS());
    return new ASTInitStatNode(type.type, ident, assignRHS);
  }

  @Override
  public ASTAssignStatNode visitAssignStat(WACCParser.AssignStatContext ctx) {
    ASTExprNode assignLHS = visitAssignLHS(ctx.assignLHS());
    ASTExprNode assignRHS = visitAssignRHS(ctx.assignRHS());
    return new ASTAssignStatNode(assignLHS, assignRHS);
  }

  @Override
  public ASTReadStatNode visitReadStat(WACCParser.ReadStatContext ctx) {
    ASTNode assignLHS = visitAssignLHS(ctx.assignLHS());
    return new ASTReadStatNode(assignLHS);
  }

  @Override
  public ASTFreeStatNode visitFreeStat(WACCParser.FreeStatContext ctx) {
    ASTExprNode expr = visitExpr(ctx.expr());
    return new ASTFreeStatNode(expr);
  }

  @Override
  public ASTExitStatNode visitExitStat(WACCParser.ExitStatContext ctx) {
    ASTExprNode expr = visitExpr(ctx.expr());
    return new ASTExitStatNode(expr);
  }

  @Override
  public ASTReturnStatNode visitReturnStat(WACCParser.ReturnStatContext ctx) {
    ASTExprNode expr = visitExpr(ctx.expr());
    return new ASTReturnStatNode(expr);
  }

  @Override
  public ASTPrintStatNode visitPrintStat(WACCParser.PrintStatContext ctx) {
    ASTExprNode expr = visitExpr(ctx.expr());
    boolean newline = ctx.PRINTLN() != null;
    return new ASTPrintStatNode(expr, newline);
  }

  @Override
  public ASTIfStatNode visitIfStat(WACCParser.IfStatContext ctx) {
    ASTExprNode condition = visitExpr(ctx.expr());
    ASTStatListNode thenBody = visitStatList(ctx.thenStat);
    ASTStatListNode elseBody = visitStatList(ctx.elseStat);
    return new ASTIfStatNode(condition, thenBody.statements,
        elseBody.statements);
  }

  @Override
  public ASTWhileStatNode visitWhileStat(WACCParser.WhileStatContext ctx) {
    ASTExprNode condition = visitExpr(ctx.expr());
    ASTStatListNode body = visitStatList(ctx.statList());
    return new ASTWhileStatNode(condition, body.statements);
  }

  @Override
  public ASTBeginStatNode visitBeginStat(WACCParser.BeginStatContext ctx) {
    ASTStatListNode body = visitStatList(ctx.statList());
    return new ASTBeginStatNode(body.statements);
  }

  @Override
  public ASTExprNode visitAssignLHS(WACCParser.AssignLHSContext ctx) {
    ASTExprNode lhs = (ASTExprNode) visitChildren(ctx);
    lhs.type = ctx.returnType;
    return lhs;
  }

  @Override
  public ASTIdentNode visitIdent(WACCParser.IdentContext ctx) {
    return new ASTIdentNode(ctx.getText());
  }

  @Override
  public ASTExprNode visitUnaryOper(WACCParser.UnaryOperContext ctx) {
    ASTExprNode operand;
    if (ctx.ident() != null) {
      operand = visitIdent(ctx.ident());
    } else {
      operand = visitExpr(ctx.expr());
    }

    UnaryOp operator;

    if (ctx.NOT() != null) {
      operator = UnaryOp.NOT;
    } else if (ctx.MINUS() != null) {
      operator = UnaryOp.MINUS;
    } else if (ctx.LEN() != null) {
      operator = UnaryOp.LEN;
    } else if (ctx.ORD() != null) {
      operator = UnaryOp.ORD;
    } else if (ctx.CHR() != null) {
      operator = UnaryOp.CHR;
    } else {
      return operand;
    }

    return new ASTUnaryOpNode(operator, operand);
  }

  @Override
  public ASTExprNode visitBinaryOper(WACCParser.BinaryOperContext ctx) {
    return visitLogicalOper(ctx.logicalOper());
  }

  @Override
  public ASTExprNode visitLogicalOper(WACCParser.LogicalOperContext ctx) {
    ASTExprNode operand = visitComparisonOper(ctx.first);

    // If there are no operations to perform, return the first operand
    if (ctx.ops == null || ctx.ops.size() == 0) {
      return operand;
    }

    List<BinaryOp> operators = new ArrayList<>();
    for (Token token : ctx.ops) {
      operators.add(BinaryOp.fromWACCToken(token));
    }

    List<ASTExprNode> operands = new ArrayList<>();
    for (WACCParser.ComparisonOperContext expr : ctx.otherExprs) {
      operands.add(visitComparisonOper(expr));
    }

    return new ASTBinaryOpNode(operand, operators, operands);
  }

  @Override
  public ASTExprNode visitComparisonOper(WACCParser.ComparisonOperContext ctx) {
    return (ASTExprNode) visitChildren(ctx);
  }

  @Override
  public ASTExprNode visitOrderingOper(WACCParser.OrderingOperContext ctx) {
    ASTExprNode operand = visitAddOper(ctx.first);

    // If there is no second operand, return the node of the first
    if (ctx.second == null) {
      return operand;
    }

    List<ASTExprNode> operands = new ArrayList<>();
    operands.add(visitAddOper(ctx.second));

    List<BinaryOp> operators = new ArrayList<>();
    BinaryOp operator;

    if (ctx.GT() != null) {
      operator = BinaryOp.GT;
    } else if (ctx.GE() != null) {
      operator = BinaryOp.GE;
    } else if (ctx.LT() != null) {
      operator = BinaryOp.LT;
    } else if (ctx.LE() != null) {
      operator = BinaryOp.LE;
    } else {
      // If there is no operator, return the first operand
      return operand;
    }

    operators.add(operator);
    return new ASTBinaryOpNode(operand, operators, operands);
  }

  @Override
  public ASTExprNode visitEqualityOper(WACCParser.EqualityOperContext ctx) {
    ASTExprNode operand = visitAddOper(ctx.first);

    // If there is no second operand, return the node of the first
    if (ctx.second == null) {
      return operand;
    }

    List<ASTExprNode> operands = new ArrayList<>();
    operands.add(visitAddOper(ctx.second));

    List<BinaryOp> operators = new ArrayList<>();
    BinaryOp operator;

    if (ctx.EQ() != null) {
      operator = BinaryOp.EQ;
    } else if (ctx.NE() != null) {
      operator = BinaryOp.NE;
    } else {
      // If there is no operator, return the first operand
      return operand;
    }

    operators.add(operator);
    return new ASTBinaryOpNode(operand, operators, operands);
  }

  @Override
  public ASTExprNode visitAddOper(WACCParser.AddOperContext ctx) {
    ASTExprNode operand = visitMultOper(ctx.first);

    // If there are no operations to perform, return the first operand
    if (ctx.ops == null || ctx.ops.size() == 0) {
      return operand;
    }

    List<BinaryOp> operators = new ArrayList<>();
    for (Token token : ctx.ops) {
      operators.add(BinaryOp.fromWACCToken(token));
    }

    List<ASTExprNode> operands = new ArrayList<>();
    for (WACCParser.MultOperContext expr : ctx.otherExprs) {
      operands.add(visitMultOper(expr));
    }

    return new ASTBinaryOpNode(operand, operators, operands);
  }

  @Override
  public ASTExprNode visitMultOper(WACCParser.MultOperContext ctx) {
    ASTExprNode operand = visitAtom(ctx.first);

    // If there are no operations to perform, return the first operand
    if (ctx.ops == null || ctx.ops.size() == 0) {
      return operand;
    }

    List<BinaryOp> operators = new ArrayList<>();
    for (Token token : ctx.ops) {
      operators.add(BinaryOp.fromWACCToken(token));
    }

    List<ASTExprNode> operands = new ArrayList<>();
    for (WACCParser.AtomContext atom : ctx.otherExprs) {
      operands.add(visitAtom(atom));
    }

    return new ASTBinaryOpNode(operand, operators, operands);
  }

  @Override
  public ASTExprNode visitAtom(WACCParser.AtomContext ctx) {
    return (ASTExprNode) visitChildren(ctx);
  }

  @Override
  public ASTIntLiteralNode visitInteger(WACCParser.IntegerContext ctx) {
    Long value = Long.parseLong(ctx.INTEGER().getText());
    if (ctx.sign() != null && ctx.sign().MINUS() != null) {
      value *= -1;
    }
    return new ASTIntLiteralNode(value);
  }

  @Override
  public ASTExprNode visitBool(WACCParser.BoolContext ctx) {
    ASTBoolLiteralNode bool = visitBoolLitr(ctx.boolLitr());
    if (ctx.NOT() != null) {
      return new ASTUnaryOpNode(UnaryOp.NOT, bool);
    }
    return bool;
  }

  @Override
  public ASTBoolLiteralNode visitBoolLitr(WACCParser.BoolLitrContext ctx) {
    return new ASTBoolLiteralNode((ctx.TRUE() != null));
  }

  @Override
  public ASTExprNode visitCharacter(WACCParser.CharacterContext ctx) {
    char c = ctx.CHARACTER().getText().charAt(1); // [''','c', ''']
    ASTCharacterLiteralNode character = new ASTCharacterLiteralNode(c);
    if (ctx.ORD() != null) {
      return new ASTUnaryOpNode(UnaryOp.ORD, character);
    }
    return character;
  }

  @Override
  public ASTExprNode visitString(WACCParser.StringContext ctx) {
    ASTStringLiteralNode string
        = new ASTStringLiteralNode(ctx.STRING().getText());
    if (ctx.LEN() != null) {
      return new ASTUnaryOpNode(UnaryOp.LEN, string);
    }
    return string;
  }

  @Override
  public ASTNullPairLiteralNode visitPairLitr(WACCParser.PairLitrContext ctx) {
    return new ASTNullPairLiteralNode();
  }

  @Override
  public ASTExprNode visitArray(WACCParser.ArrayContext ctx) {
    ASTArrayElemNode array = visitArrayElem(ctx.arrayElem());
    if (ctx.LEN() != null) {
      return new ASTUnaryOpNode(UnaryOp.LEN, array);
    }
    return array;
  }

  @Override
  public ASTArrayElemNode visitArrayElem(WACCParser.ArrayElemContext ctx) {
    ASTIdentNode ident = visitIdent(ctx.ident());

    List<ASTExprNode> operands = new ArrayList<>();
    for (WACCParser.ExprContext expr : ctx.expr()) {
      operands.add(visitExpr(expr));
    }

    ASTArrayElemNode array = new ASTArrayElemNode(ident, operands);
    array.type = ctx.returnType;

    return array;
  }

  @Override
  public ASTArrayLiteralNode visitArrayLitr(WACCParser.ArrayLitrContext ctx) {

    List<ASTExprNode> elems = new ArrayList<>();
    for (WACCParser.ExprContext expr : ctx.expr()) {
      elems.add(visitExpr(expr));
    }

    return new ASTArrayLiteralNode(elems);
  }

  @Override
  public ASTExprNode visitExpr(WACCParser.ExprContext ctx) {
    ASTExprNode expr = visitBinaryOper(ctx.binaryOper());
    expr.type = ctx.returnType;
    return expr;
  }

  @Override
  public ASTNewPairNode visitNewPair(WACCParser.NewPairContext ctx) {
    ASTExprNode first = visitExpr(ctx.first);
    ASTExprNode second = visitExpr(ctx.second);
    return new ASTNewPairNode(first, second);
  }

  @Override
  public ASTExprNode visitPairElem(WACCParser.PairElemContext ctx) {
    ASTIdentNode ident = visitIdent(ctx.ident());
    if (ctx.FST() != null) {
      return new ASTUnaryOpNode(UnaryOp.FST, ident);
    } else {
      return new ASTUnaryOpNode(UnaryOp.SND, ident);
    }
  }

  @Override
  public ASTCallNode visitCall(WACCParser.CallContext ctx) {
    ASTIdentNode ident = visitIdent(ctx.ident());
    ASTArgListNode arglist = visitArgList(ctx.argList());
    return new ASTCallNode(ident, arglist.arguments);
  }

  @Override
  public ASTExprNode visitAssignRHS(WACCParser.AssignRHSContext ctx) {
    return (ASTExprNode) visitChildren(ctx);
  }

  @Override
  public ASTTypeNode visitBaseType(WACCParser.BaseTypeContext ctx) {
    if (ctx.INT_T() != null) {
      return new ASTTypeNode(new Type(Types.INT_T));
    } else if (ctx.BOOL_T() != null) {
      return new ASTTypeNode(new Type(Types.BOOL_T));
    } else if (ctx.CHAR_T() != null) {
      return new ASTTypeNode(new Type(Types.CHAR_T));
    } else {
      return new ASTTypeNode(new Type(Types.STRING_T));
    }
  }

  @Override
  public ASTTypeNode visitType(WACCParser.TypeContext ctx) {
    return (ASTTypeNode) visitChildren(ctx);
  }

  @Override
  public ASTTypeNode visitNonArrayType(WACCParser.NonArrayTypeContext ctx) {
    return (ASTTypeNode) visitChildren(ctx);
  }

  @Override
  public ASTTypeNode visitArrayType(WACCParser.ArrayTypeContext ctx) {
    ASTTypeNode base = visitNonArrayType(ctx.nonArrayType());
    int dimensionality = ctx.OPEN_BRACKET().size();
    return new ASTTypeNode(new ArrayType(base.type, dimensionality));
  }

  @Override
  public ASTTypeNode visitPairType(WACCParser.PairTypeContext ctx) {
    ASTTypeNode first = visitPairElemType(ctx.firstType);
    ASTTypeNode second = visitPairElemType(ctx.secondType);
    return new ASTTypeNode(new PairType(first.type, second.type));
  }

  @Override
  public ASTTypeNode visitPairElemType(WACCParser.PairElemTypeContext ctx) {
    if (ctx.baseType() != null) {
      return visitBaseType(ctx.baseType());
    } else if (ctx.arrayType() != null) {
      return visitArrayType(ctx.arrayType());
    } else {
      return new ASTTypeNode(new PairType());
    }
  }
}
