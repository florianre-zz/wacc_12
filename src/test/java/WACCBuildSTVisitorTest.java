// import static org.hamcrest.CoreMatchers.containsString;
// import static org.hamcrest.CoreMatchers.hasItems;
// import static org.junit.Assert.fail;

import antlr.WACCParser;
import bindings.NewScope;
import bindings.SymbolTable;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Rule;
import org.junit.Test;
import wacc.WACCBuildSTVisitor;

public class WACCBuildSTVisitorTest {

  @Rule
  public JUnitRuleMockery context = new JUnitRuleMockery() {{
    setImposteriser(ClassImposteriser.INSTANCE);
  }};
  final SymbolTable top = context.mock(SymbolTable.class);

  @Test
  public void visitProgAddsNewProgramSymbolTableToTop(){
    final WACCParser.ProgContext ctx
        = context.mock(WACCParser.ProgContext.class);
    context.checking(new Expectations() {{
      oneOf(top).add(with(aNonNull(String.class)),
                     with(aNonNull(NewScope.class)));
      ignoring(ctx).getChildCount();
    }});
    WACCBuildSTVisitor buildSTVisitor = new WACCBuildSTVisitor(top);
    buildSTVisitor.visitProg(ctx);
  }
  
}