package wacc.ast;

import antlr.WACCParser;
import org.antlr.v4.runtime.Token;
import wacc.Utils;

public enum BinaryOp {
  AND,
  OR,
  GT,
  GE,
  LT,
  LE,
  EQ,
  NE,
  PLUS,
  MINUS,
  MUL,
  DIV,
  MOD;

  public static BinaryOp fromWACCToken(Token token) {

    String text = token.getText();
    if (text.equals(Utils.getToken(WACCParser.AND))) {
      return AND;
    } else if (text.equals(Utils.getToken(WACCParser.OR))) {
      return OR;
    } else if (text.equals(Utils.getToken(WACCParser.GT))) {
      return GT;
    } else if (text.equals(Utils.getToken(WACCParser.LT))) {
      return LT;
    } else if (text.equals(Utils.getToken(WACCParser.LE))) {
      return LE;
    } else if (text.equals(Utils.getToken(WACCParser.EQ))) {
      return NE;
    } else if (text.equals(Utils.getToken(WACCParser.PLUS))) {
      return PLUS;
    } else if (text.equals(Utils.getToken(WACCParser.MINUS))) {
      return MINUS;
    } else if (text.equals(Utils.getToken(WACCParser.MUL))) {
      return MUL;
    } else if (text.equals(Utils.getToken(WACCParser.DIV))) {
      return DIV;
    } else if (text.equals(Utils.getToken(WACCParser.MOD))) {
      return MOD;
    }
    return null;
  }

  @Override
  public String toString() {
    switch (this) {
      case AND:
        return "&&";
      case OR:
        return "||";
      case GT:
        return ">";
      case GE:
        return ">=";
      case LT:
        return "<";
      case LE:
        return "<=";
      case EQ:
        return "==";
      case NE:
        return "!=";
      case PLUS:
        return "+";
      case MINUS:
        return "-";
      case MUL:
        return "*";
      case DIV:
        return "/";
      case MOD:
        return "%";
      default:
        return "Unsupported Binary Op";
    }
  }
}

