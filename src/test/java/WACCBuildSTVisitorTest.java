import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Rule;
import org.junit.Test;

import antlr.WACCParser;
import bindings.NewScope;
import wacc.SymbolTable;
import wacc.WACCBuildSTVisitor;

public class WACCBuildSTVisitorTest {

  @Rule
  public JUnitRuleMockery context = new JUnitRuleMockery() {{
    setImposteriser(ClassImposteriser.INSTANCE);
  }};

  final SymbolTable top = context.mock(SymbolTable.class);
  WACCBuildSTVisitor buildSTVisitor = new WACCBuildSTVisitor(top);

  @Test
  public void visitProgAddsNewProgramSymbolTableToTop(){
    final WACCParser.ProgContext ctx
        = context.mock(WACCParser.ProgContext.class);

    context.checking(new Expectations() {{
      oneOf(top).put(with(aNonNull(String.class)),
          with(aNonNull(NewScope.class)));
      ignoring(ctx).getChildCount();
    }});

    buildSTVisitor.visitProg(ctx);
  }
  
}