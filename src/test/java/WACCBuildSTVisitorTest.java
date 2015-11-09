import antlr.WACCParser;
import bindings.Binding;
import bindings.NewScope;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Rule;
import org.junit.Test;
import wacc.SymbolTable;
import wacc.WACCBuildSTVisitor;

public class WACCBuildSTVisitorTest {

  @Rule
  public JUnitRuleMockery context = new JUnitRuleMockery() {{
    setImposteriser(ClassImposteriser.INSTANCE);
  }};

  @SuppressWarnings("unchecked")
  SymbolTable<String, Binding> top = context.mock(SymbolTable.class);
  WACCBuildSTVisitor buildSTVisitor = new WACCBuildSTVisitor(top);

  @Test
  public void visitProgAddsNewProgramSymbolTableToTop()
      throws NoSuchFieldException, IllegalAccessException {
    final WACCParser.ProgContext ctx
        = context.mock(WACCParser.ProgContext.class);

    context.checking(new Expectations() {{
      oneOf(top)
          .put(with(aNonNull(String.class)), with(aNonNull(NewScope.class)));
      ignoring(ctx).getChildCount();
    }});

    buildSTVisitor.visitProg(ctx);
  }

  @Test
  public void

}