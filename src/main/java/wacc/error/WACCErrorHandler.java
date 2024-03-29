package wacc.error;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.Interval;

import java.util.ArrayList;
import java.util.List;

public class WACCErrorHandler implements ErrorHandler<ParserRuleContext> {
  ArrayList<IError<ParserRuleContext>> semanticErrors;
  ArrayList<IError<ParserRuleContext>> syntacticErrors;
  ArrayList<String> lexingErrors;
  TokenStream tokenStream;


  public WACCErrorHandler(TokenStream tokenStream) {
    this.semanticErrors = new ArrayList<>();
    this.syntacticErrors = new ArrayList<>();
    this.lexingErrors = new ArrayList<>();
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

  @Override
  public String toString() {

    StringBuilder sb = new StringBuilder();

    if (syntacticErrors.size() > 0) {
      sb = printErrors(syntacticErrors);
    } else {
      if (semanticErrors.size() > 0) {
        sb = printErrors(semanticErrors);
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

  private String getLineAndChar(IError<ParserRuleContext> e) {
    ParserRuleContext ctx = e.getCtx();
    Interval sourceInterval = ctx.getSourceInterval();
    Token firstToken = tokenStream.get(sourceInterval.a);
    int lineNumber = firstToken.getLine();
    int charNumber = firstToken.getCharPositionInLine() + 1;

    return "  at " + String.format("%4d", lineNumber) + ":"
        + String.format("%02d", charNumber) + " -- ";
  }

  @Override
  public int getLexingErrorCount() {
    return lexingErrors.size();
  }

  private String concatWithNewLines(String location, String[] lines) {
    StringBuilder sb = new StringBuilder();
    for (int i = 1; i < lines.length; i++) {
      String spaces = new String(new char[location.length() + 2]);
      spaces = spaces.replace('\0', ' ');
      sb.append(spaces).append(lines[i]).append("\n");
    }

    return sb.toString();
  }

  public void complainAboutLexing(List<String> errors) {
    this.lexingErrors.addAll(errors);
  }

  private StringBuilder printErrors(ArrayList<IError<ParserRuleContext>>
                                            errors) {
    final StringBuilder sb = new StringBuilder();
    int size = errors.size();
    sb.append(size).append(" Error");
    sb.append(size == 1 ? "" : "s").append(":\n");

    for (IError<ParserRuleContext> e : errors) {
      String location = getLineAndChar(e);
      sb.append(location);
      String lines[] = e.toString().split("\\r?\\n");
      sb.append(lines[0]).append("\n");
      sb.append(concatWithNewLines(location, lines));
    }

    return sb;
  }

  public boolean printLexingErrors() {
    for (String error : lexingErrors) {
      System.err.println(error);
    }

    return lexingErrors.size() > 0;
  }

  public boolean printSyntaxErrors() {
    System.err.println(printErrors(syntacticErrors));
    return syntacticErrors.size() > 0;
  }

  public void printSemanticErrors() {
    if (hasSemanticErrors()) {
      System.err.println(printErrors(semanticErrors));
    }
  }

  public boolean hasSyntaxErrors() {
    return syntacticErrors.size() > 0;
  }

  public boolean hasSemanticErrors() {
    return semanticErrors.size() > 0;
  }
}
