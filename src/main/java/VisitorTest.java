// import ANTLR's runtime libraries
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

// import antlr package (your code)
import antlr.*;
public class VisitorTest {
    public static void main(String[] args) throws Exception {

        // create a CharStream that reads from standard input
        ANTLRInputStream input = new ANTLRInputStream(
            "begin " +
            "int inc(int x) is\n" +
            "  return x + 1\n" +
            "end skip end");

        // create a lexer that feeds off of input CharStream
        WACCLexer lexer = new WACCLexer(input);

        // create a buffer of tokens pulled from the lexer
        CommonTokenStream tokens = new CommonTokenStream(lexer);

        // create a parser that feeds off the tokens buffer
        WACCParser parser = new WACCParser(tokens);

        ParseTree tree = parser.prog(); // begin parsing at prog rule

        //System.out.println(tree.toStringTree(parser)); // print LISP-style tree
        System.out.println("====");
        WACCVisitor visitor = new WACCVisitor();
        visitor.visit(tree);
        System.out.println("====");
    }
}
