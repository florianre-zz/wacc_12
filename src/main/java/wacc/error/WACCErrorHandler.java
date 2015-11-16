package wacc.error;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.misc.Interval;

import java.util.ArrayList;

public class WACCErrorHandler implements ErrorHandler<ParserRuleContext> {
  ArrayList<IError<ParserRuleContext>> abstractErrors;
  TokenStream tokenStream;

  public WACCErrorHandler(TokenStream tokenStream) {
    this.abstractErrors = new ArrayList<>();
    this.tokenStream = tokenStream;
  }

  @Override
  public void complain(IError<ParserRuleContext> e) {
    abstractErrors.add(e);
  }

  @Override
  public String toString() {

    final StringBuilder sb = new StringBuilder();
    int size = abstractErrors.size();
    if (size > 0) {

      sb.append(size).append(" Error");
      sb.append(size == 1 ? "" : "s").append(":\n");

      for (IError<ParserRuleContext> e : abstractErrors) {
        ParserRuleContext ctx = e.getCtx();
        Interval sourceInterval = ctx.getSourceInterval();
        Token firstToken = tokenStream.get(sourceInterval.a);
        int lineNumber = firstToken.getLine();
        int charNumber = firstToken.getCharPositionInLine() + 1;

        sb.append("  at ");

        sb.append(lineNumber).append(":").append(String.format("%02d", charNumber));
        sb.append(" -- ").append(e).append("\n");
      }
    }
    return sb.toString();
  }
}
