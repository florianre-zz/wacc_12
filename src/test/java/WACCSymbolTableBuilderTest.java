import antlr.WACCLexer;
import antlr.WACCParser;
import bindings.Binding;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import wacc.SymbolTable;
import wacc.WACCSymbolTableBuilder;
import wacc.error.WACCErrorHandler;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
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

  private ParseTree parseProgram() throws IOException {
    String content = new Scanner(new File(filePath)).useDelimiter("\\Z").next();
    ANTLRInputStream input = new ANTLRInputStream(content);

    // create a lexer that feeds off of input CharStream
    WACCLexer lexer = new WACCLexer(input);

    // create a buffer of tokens pulled from the lexer
    CommonTokenStream tokens = new CommonTokenStream(lexer);

    // create a parser that feeds off the tokens buffer
    WACCParser parser = new WACCParser(tokens);

    return parser.prog(); // begin parsing at prog rule
  }

  @Test
  public void topSymbolTableContainsProg()
      throws NoSuchFieldException, IllegalAccessException {
    final WACCParser.ProgContext ctx
        = context.mock(WACCParser.ProgContext.class);

    context.checking(new Expectations() {{
      ignoring(ctx).func();
      ignoring(ctx).getChildCount();
    }});
    symbolTableBuilder.visitProg(ctx);
    assertTrue(top.containsKey("0prog"));
  }

  @Test
  public void topSymbolTableDoesNotConatainFunctionScopes() {
    filePath = "src/test/resources/examples/valid/function/nested_functions" +
               "/mutualRecursion.wacc";
    try {
      ParseTree prog = parseProgram();
      symbolTableBuilder.visit(prog);
      assertFalse(top.containsKey("r1"));
      assertFalse(top.containsKey("r2"));
    } catch (IOException e) {
      e.printStackTrace();
    }

  }
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

  //TODO: Variables declared in the body of the filePath stored in main

  //TODO: Allow functions of names which may be the same as newScope names (without the prefix integer)

  //TODO: Allow mutual recursion

  //TODO: Ensure Function scope contains parameters

  //TODO: Ensure Function SymbolTable contains parameters

  //TODO: Deny overloading

  //TODO: Deny parameters with the same name

  //TODO: Allow function name and parameter name to be the same

  //TODO: Deny function parameter name to be re-declared within function

  //TODO: Ensure Begin Stat creates a new scope within relevant scopes

  //TODO: Ensure If Stat creates new scopes within relevant scopes

  //TODO: Ensure While Stat creates a new scope within relevant scopes

  //TODO: Deny use of variable used in conditional expression where variable
  // is only declared within the conditional's scopes

  //TODO: Ensure variable definition stores Variable in relevant scopes

  //TODO: Allow variable name to be used in different regular scopes

  //TODO: Allow variable name to be used in different Functions

  //TODO: Deny redeclaration of variable

  //TODO: Deny redeclaration of variable in one way scopes

  //TODO: Deny redeclaration of variable in nested one way scopes

  //TODO: Deny int a = a (recursive def of var - RHS not already declared)

  //TODO: Allow int a = a when RHS already declared

  //TODO: Allow use of declared variable within current scope

  //TODO: Allow use of variable declared in scope above

  //TODO: Allow use of variable declared in an outer scope

  //TODO: Deny use of undeclared variable

  //TODO: Deny use of undeclared variable which shares name with function

  //TODO: Allow the call of declared functions

  //TODO: Deny the call of undeclared functions

  //TODO: Deny the call of undeclared function when a variable of that name
  // has been declared

  //TODO: Deny function call with undeclared parameters

  //TODO: Allow function call with non-variable parameters

}