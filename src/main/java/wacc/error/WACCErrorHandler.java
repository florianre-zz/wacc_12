package wacc.error;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.Interval;

import java.util.ArrayList;

public class WACCErrorHandler implements ErrorHandler<ParserRuleContext> {
  ArrayList<IError<ParserRuleContext>> semanticErrors;
  ArrayList<IError<ParserRuleContext>> syntacticErrors;
  TokenStream tokenStream;


  public WACCErrorHandler(TokenStream tokenStream) {
    this.semanticErrors = new ArrayList<>();
    this.syntacticErrors = new ArrayList<>();
    this.tokenStream = tokenStream;
  }

  @Override
  public void complain(IError<ParserRuleContext> e) {
    if (e instanceof SyntaxError){
      syntacticErrors.add(e);
    } else {
      semanticErrors.add(e);
    }
  }


  //synthax = 0 -> semantics
  @Override
  public String toString() {

    final StringBuilder sb = new StringBuilder();
    int size = semanticErrors.size();

    if (syntacticErrors.size() > 0) {
      sb.append(size).append(" Error");
      sb.append(size == 1 ? "" : "s").append(":\n");

      for (IError<ParserRuleContext> e : syntacticErrors) {
        String preamble = getErrorString(e);
        sb.append(preamble);
        String lines[] = e.toString().split("\\r?\\n");
        sb.append(lines[0]).append("\n");
        concatWithNewLines(sb, preamble, lines);
      }
    } else {
      if (semanticErrors.size() > 0) {
        sb.append(size).append(" Error");
        sb.append(size == 1 ? "" : "s").append(":\n");

        for (IError<ParserRuleContext> e : semanticErrors) {
          String preamble = getErrorString(e);
          sb.append(preamble);
          String lines[] = e.toString().split("\\r?\\n");
          sb.append(lines[0]).append("\n");
          concatWithNewLines(sb, preamble, lines);
        }
      }
    }

    return sb.toString();
  }

  @Override
  public int getSemanticErrorCount() {
    return semanticErrors.size();
  }

  @Override
  public int getSyntacticErrorCount() {
    return syntacticErrors.size();
  }

  private String getErrorString(IError<ParserRuleContext> e) {
    ParserRuleContext ctx = e.getCtx();
    Interval sourceInterval = ctx.getSourceInterval();
    Token firstToken = tokenStream.get(sourceInterval.a);
    int lineNumber = firstToken.getLine();
    int charNumber = firstToken.getCharPositionInLine() + 1;

    return "  at " + String.format("%4d", lineNumber) + ":"
        + String.format("%02d", charNumber) + " -- ";
  }

  private void concatWithNewLines(StringBuilder sb, String preamble, String[] lines) {
    for (int i = 1; i < lines.length; i++) {
      String spaces = new String(new char[preamble.length() + 2]);
      spaces = spaces.replace('\0', ' ');
      sb.append(spaces).append(lines[i]).append("\n");
    }
  }

}
