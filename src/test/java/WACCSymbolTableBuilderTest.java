import org.jmock.integration.junit4.JUnitRuleMockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Rule;

public class WACCSymbolTableBuilderTest {

  @Rule
  public JUnitRuleMockery context = new JUnitRuleMockery() {{
    setImposteriser(ClassImposteriser.INSTANCE);
  }};

//  @SuppressWarnings("unchecked")
//  SymbolTable<String, Binding> top = context.mock(SymbolTable.class);
//  WACCSymbolTableBuilder buildSTVisitor = new WACCSymbolTableBuilder(top);

//  @Test
//  public void visitProgAddsNewProgramSymbolTableToTop()
//      throws NoSuchFieldException, IllegalAccessException {
//    final WACCParser.ProgContext ctx
//        = context.mock(WACCParser.ProgContext.class);
//
//    context.checking(new Expectations() {{
//      oneOf(top)
//          .put(with(aNonNull(String.class)), with(aNonNull(NewScope.class)));
//      ignoring(ctx).getChildCount();
//    }});
//
//    buildSTVisitor.visitProg(ctx);
//  }

  //TODO: Top SymbolTable contains prog

  //TODO: Top SymbolTable doesn't contain any functions

  //TODO: Variables declared in the body of the program stored in main

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