import antlr.WACCLexer;
import antlr.WACCParser;
import bindings.*;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.Test;
import wacc.SymbolTable;
import wacc.WACCSymbolTableFiller;
import wacc.error.WACCErrorHandler;

import java.util.Dictionary;
public class SymbolTableValuesTest {

  private static SymbolTable<String, Binding> createTopSymbolTable() {
    SymbolTable<String, Binding> top = new SymbolTable<>();
    top.put(Types.INT_T.toString(), new Type(Types.INT_T, Integer.MIN_VALUE, Integer
        .MAX_VALUE));
    top.put(Types.BOOL_T.toString(), new Type(Types.BOOL_T, 0, 1));
    top.put(Types.CHAR_T.toString(), new Type(Types.CHAR_T, 0, 255));
    top.put(Types.STRING_T.toString(), new Type(Types.STRING_T));
    top.put(Types.PAIR_T.toString(), new PairType());
    return top;
  }

  Dictionary<String, Binding> symbolsInProgram(String inputString) {

    ANTLRInputStream input = new ANTLRInputStream(inputString);

    // create a lexer that feeds off of input CharStream
    WACCLexer lexer = new WACCLexer(input);

    // create a buffer of tokens pulled from the lexer
    CommonTokenStream tokens = new CommonTokenStream(lexer);

    // create a parser that feeds off the tokens buffer
    WACCParser parser = new WACCParser(tokens);

    ParseTree tree = parser.prog(); // begin parsing at prog rule

    SymbolTable<String, Binding> top = createTopSymbolTable();
    WACCErrorHandler errorHandler = new WACCErrorHandler(parser.getInputStream());
    WACCSymbolTableFiller buildSTVisitor
        = new WACCSymbolTableFiller(top, errorHandler);
    buildSTVisitor.visit(tree);

    Dictionary<String, Binding> zeroMainTable =
        ((NewScope) top.get("prog")).getSymbolTable();

    return zeroMainTable;
  }

//  @Test
//  public void testNoAssignment() throws Exception {
//    String program = "begin" +
//                     "  skip" +
//                     "end";
//
//    Dictionary<String, Binding> table = symbolsInProgram(program);
//    assertThat(table, contains("i: int"));
//
//    //    assertEquals(representationOfSymbolTableWithInput(program), expected);
//  }

  @Test
  public void testSingleIntAssignment() throws Exception {
    String program = "begin" +
                     "  int i = 0" +
                     "end";

    Dictionary<String, Binding> table = symbolsInProgram(program);
  }

}
