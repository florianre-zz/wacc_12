package wacc;

import antlr.WACCLexer;
import antlr.WACCParser;
import bindings.Binding;
import bindings.PairType;
import bindings.Type;
import bindings.Types;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;
import wacc.error.WACCErrorHandler;

// TODO: Tidy up this main()

public class WACCCompile {
  public static void main(String[] args) throws Exception {
    // create a CharStream that reads from standard input
    ANTLRInputStream input = new ANTLRInputStream(System.in);

    // create a lexer that feeds off of input CharStream
    WACCLexer lexer = new WACCLexer(input);

    // create a buffer of tokens pulled from the lexer
    CommonTokenStream tokens = new CommonTokenStream(lexer);

    // create a parser that feeds off the tokens buffer
    WACCParser parser = new WACCParser(tokens);

    ParseTree tree = parser.prog(); // begin parsing at prog rule

    int numberOfSyntaxErrors = parser.getNumberOfSyntaxErrors();
    if (numberOfSyntaxErrors > 0) {
      System.err.println(numberOfSyntaxErrors + " Syntax Errors");
      System.exit(100);
    }

    SymbolTable<String, Binding> top = createTopSymbolTable();
    TokenStream inputStream = parser.getInputStream();
    WACCErrorHandler errorHandler = new WACCErrorHandler(inputStream);

    checkSemantics(tree, top, errorHandler);

    System.exit(0);
  }

  private static void checkSemantics(ParseTree tree,
                                     SymbolTable<String, Binding> top,
                                     WACCErrorHandler errorHandler) {
    WACCSymbolTableBuilder buildSTVisitor
        = new WACCSymbolTableBuilder(top, errorHandler);
    buildSTVisitor.visit(tree);

    WACCTypeChecker typeChecker = new WACCTypeChecker(top, errorHandler);
    typeChecker.visit(tree);

    if (errorHandler.getSyntacticErrorCount() > 0) {
      System.err.println(errorHandler);
      System.exit(100);
    }

    if (errorHandler.getSemanticErrorCount() > 0) {
      System.err.println(errorHandler);
      System.exit(200);
    }
  }

  private static SymbolTable<String, Binding> createTopSymbolTable() {
    SymbolTable<String, Binding> top = new SymbolTable<>();
    // TODO: move somewhere more appropriate
    int min = (int) -Math.pow(2, 31);
    int max = (int) (Math.pow(2, 31) - 1);
    top.put(Types.INT_T.toString(), new Type(Types.INT_T, min,
        max));
    top.put(Types.BOOL_T.toString(), new Type(Types.BOOL_T, 0, 1));
    top.put(Types.CHAR_T.toString(), new Type(Types.CHAR_T, 0, 255));
    top.put(Types.STRING_T.toString(), new Type(Types.STRING_T));
    top.put(Types.PAIR_T.toString(), new PairType());
    return top;
  }

}