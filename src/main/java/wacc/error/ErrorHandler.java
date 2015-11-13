package wacc.error;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.misc.Interval;

import java.util.ArrayList;

public class ErrorHandler {
  ArrayList<Error> errors;
  TokenStream tokenStream;

  public ErrorHandler(TokenStream tokenStream) {
    this.errors = new ArrayList<>();
    this.tokenStream = tokenStream;
  }

  public void complain(Error e) {
    errors.add(e);
  }

  @Override
  public String toString() {

    final StringBuilder sb = new StringBuilder();
    int size = errors.size();
    if (size > 0) {

      sb.append(size).append(" Error");
      sb.append(size == 1 ? "" : "s").append(":\n");

      for (Error e : errors) {
        ParserRuleContext ctx = e.getCtx();
        Interval sourceInterval = ctx.getSourceInterval();
        Token firstToken = tokenStream.get(sourceInterval.a);
        int lineNumber = firstToken.getLine();
        int charNumber = firstToken.getCharPositionInLine();

        sb.append("  at ");
        sb.append(lineNumber).append(":").append(charNumber);
        sb.append(" -- ").append(e).append("\n");
      }
    }
    return sb.toString();
  }
}
