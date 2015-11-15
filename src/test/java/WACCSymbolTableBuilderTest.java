import bindings.Binding;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Rule;
import wacc.SymbolTable;
import wacc.WACCSymbolTableBuilder;

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

  //TODO:
}