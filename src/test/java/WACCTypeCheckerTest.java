import antlr.WACCParser;
import bindings.Binding;
import bindings.Type;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.*;
import wacc.SymbolTable;
import wacc.WACCTypeChecker;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;


public class WACCTypeCheckerTest {


  public static final String VALID_TYPE = "int";
  public static final String INVALID_TYPE = "0type";
  private WACCTypeChecker typeChecker;

  @Rule
  public JUnitRuleMockery context = new JUnitRuleMockery() {{
    setImposteriser(ClassImposteriser.INSTANCE);
  }};

  @Before
  public void setUp() throws Exception {
    SymbolTable<String, Binding> top = new SymbolTable<>();
    top.put("int", new Type("INT_T", Integer.MIN_VALUE, Integer.MAX_VALUE));
    top.put("bool", new Type("BOOL_T", 0, 1));
    top.put("char", new Type("CHAR_T", 0, 255));
    top.put("string", new Type("STRING_T"));
    typeChecker = new WACCTypeChecker(top);
  }

  @After
  public void tearDown() throws Exception {

  }

  @Test
  public void testVisitParamPass() {
    final WACCParser.ParamContext paramCtx
        = context.mock(WACCParser.ParamContext.class);
    final WACCParser.TypeContext typeCtx
        = context.mock(WACCParser.TypeContext.class);

    context.checking(new Expectations() {{
      oneOf(typeCtx).getText(); will(returnValue(VALID_TYPE));
      oneOf(paramCtx).type().getText(); will(returnValue(typeCtx));
    }});

    Type type = typeChecker.visitParam(paramCtx);
    assertNotNull(type);
    assertThat(type.getName(), is("INT_T"));
  }

  @Test
  public void testVisitParamFail() {
    final WACCParser.ParamContext paramCtx
        = context.mock(WACCParser.ParamContext.class);
    final WACCParser.TypeContext typeCtx
        = context.mock(WACCParser.TypeContext.class);

    context.checking(new Expectations() {{
      oneOf(typeCtx).getText(); will(returnValue(INVALID_TYPE));
      oneOf(paramCtx).type().getText(); will(returnValue(typeCtx));
    }});

    Type type = typeChecker.visitParam(paramCtx);
    assertNull(type);
  }

}