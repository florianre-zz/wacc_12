import bindings.Binding;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Rule;
import wacc.SymbolTable;
import wacc.WACCSymbolTableBuilder;
import wacc.error.WACCErrorHandler;
public class WACCSymbolTableBuilderTest {

  @Rule
  public JUnitRuleMockery context = new JUnitRuleMockery() {{
    setImposteriser(ClassImposteriser.INSTANCE);
  }};

  @SuppressWarnings("unchecked")
  private String filePath;
  String program;
  private SymbolTable<String, Binding> top;
  WACCErrorHandler errorHandler = context.mock(WACCErrorHandler.class);
  WACCSymbolTableBuilder symbolTableBuilder;

  @Before
  public void setUpWACCSymbolTableBuilder() {
    top = new SymbolTable<>();
    symbolTableBuilder = new WACCSymbolTableBuilder(top, errorHandler);
  }

//  private ParseTree parseProgram() throws IOException {
//    String content = new Scanner(new File(filePath)).useDelimiter("\\Z").next();
//    ANTLRInputStream input = new ANTLRInputStream(content);
//
//    // create a lexer that feeds off of input CharStream
//    WACCLexer lexer = new WACCLexer(input);
//
//    // create a buffer of tokens pulled from the lexer
//    CommonTokenStream tokens = new CommonTokenStream(lexer);
//
//    // create a parser that feeds off the tokens buffer
//    WACCParser parser = new WACCParser(tokens);
//
//    return parser.prog(); // begin parsing at prog rule
//  }

//  @Test
//  public void topSymbolTableContainsProg()
//      throws NoSuchFieldException, IllegalAccessException {
//    final WACCParser.ProgContext ctx
//        = context.mock(WACCParser.ProgContext.class);
//
//    context.checking(new Expectations() {{
//      ignoring(ctx).func();
//      ignoring(ctx).getChildCount();
//    }});
//    symbolTableBuilder.visitProg(ctx);
//    assertTrue(top.containsKey("0prog"));
//  }
//
//  @Test
//  public void topSymbolTableDoesNotConatainFunctionScopes() {
//    filePath = "src/test/resources/examples/valid/function/nested_functions" +
//               "/mutualRecursion.wacc";
//    try {
//      ParseTree prog = parseProgram();
//      symbolTableBuilder.visit(prog);
//      assertFalse(top.containsKey("r1"));
//      assertFalse(top.containsKey("r2"));
//    } catch (IOException e) {
//      e.printStackTrace();
//    }
//
//  }
//
//  @Test
//  public void listOfParamsInFunctionScopeMatchFormals() {
//    filePath = "src/test/resources/examples/valid/function/simple_functions/" +
//               "functionManyArguments.wacc";
//
//    try {
//      ParseTree prog = parseProgram();
//      symbolTableBuilder.visit(prog);
//      Function function
//          = (Function) ((NewScope) top.get("0prog")).getSymbolTable()
//                                                  .get("doSomething");
//      int paramSize = function.getParams().size();
//      assertTrue(paramSize == 6);
//      assertNotNull(function);
//    } catch (IOException e) {
//      e.printStackTrace();
//    }
//  }


}