package wacc;

import antlr.WACCLexer;
import antlr.WACCParser;
import bindings.Binding;
import bindings.Type;
import bindings.Types;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import wacc.error.WACCErrorHandler;

// TODO: Tidy up this main()

public class WACCCompile {
  public static void main(String[] args) throws Exception {
    System.out.println("Welcome to WACC Compile...");
    // create a CharStream that reads from standard input
    ANTLRInputStream input = new ANTLRInputStream(System.in);

    // create a lexer that feeds off of input CharStream
    WACCLexer lexer = new WACCLexer(input);

    // create a buffer of tokens pulled from the lexer
    CommonTokenStream tokens = new CommonTokenStream(lexer);

    // create a parser that feeds off the tokens buffer
    WACCParser parser = new WACCParser(tokens);

    ParseTree tree = parser.prog(); // begin parsing at prog rule

    //System.out.println(tree.toStringTree(parser)); // print LISP-style tree

    System.out.println("====");
    System.out.println("Visiting...");
    SymbolTable<String, Binding> top = createTopSymbolTable();
    WACCErrorHandler errorHandler = new WACCErrorHandler(parser.getInputStream());
    WACCSymbolTableBuilder buildSTVisitor
        = new WACCSymbolTableBuilder(top, errorHandler);
    buildSTVisitor.visit(tree);

    System.out.println("Symbol Tables: ");
    System.out.println(top);

    //TODO: Null pointer
    WACCTypeChecker typeChecker = new WACCTypeChecker(top, errorHandler);
    typeChecker.visit(tree);
    System.err.println(errorHandler);

    System.out.println("====");
  }

  private static SymbolTable<String, Binding> createTopSymbolTable() {
    SymbolTable<String, Binding> top = new SymbolTable<>();
    // TODO: Is Integer.MAX_VALUE correct for the wacc language
    top.put(Types.INT_T.toString(), new Type(Types.INT_T, Integer.MIN_VALUE, Integer
        .MAX_VALUE));
    top.put(Types.BOOL_T.toString(), new Type(Types.BOOL_T, 0, 1));
    top.put(Types.CHAR_T.toString(), new Type(Types.CHAR_T, 0, 255));
    top.put(Types.STRING_T.toString(), new Type(Types.STRING_T));
    top.put(Types.PAIR_T.toString(), new Type(Types.PAIR_T));
    return top;
  }

}