package wacc;

import antlr.*;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

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

    //System.out.println(tree.toStringTree(parser)); // print LISP-style tree
    System.out.println("====");
    System.out.println("Visiting...");
    WACCVisitor visitor = new WACCVisitor();
    visitor.visit(tree);
    System.out.println("====");
  }
}