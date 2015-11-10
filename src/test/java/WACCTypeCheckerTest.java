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
import static org.junit.Assert.*;


public class WACCTypeCheckerTest {


  public static final String INT_TYPE = "int";
  private static final String BOOL_TYPE = "bool";
  public static final String INVALID_TYPE = "0type";
  public static final String INT_T = "INT_T";

  private WACCTypeChecker typeChecker;

  @Rule
  public JUnitRuleMockery context = new JUnitRuleMockery() {{
    setImposteriser(ClassImposteriser.INSTANCE);
  }};

  WACCParser.TypeContext typeCtx;

  @Before
  public void setUp() throws Exception {

    typeCtx = context.mock(WACCParser.TypeContext.class);

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

    context.checking(new Expectations() {{
      oneOf(typeCtx).getText(); will(returnValue(INT_TYPE));
      oneOf(paramCtx).type().getText(); will(returnValue(typeCtx));
    }});

    Type type = typeChecker.visitParam(paramCtx);
    assertNotNull(type);
    assertThat(type.getName(), is(INT_T));
  }

  @Test
  public void testVisitParamFail() {
    final WACCParser.ParamContext paramCtx
        = context.mock(WACCParser.ParamContext.class);

    context.checking(new Expectations() {{
      oneOf(typeCtx).getText(); will(returnValue(INVALID_TYPE));
      oneOf(paramCtx).type().getText(); will(returnValue(typeCtx));
    }});

    Type type = typeChecker.visitParam(paramCtx);
    assertNull(type);
  }

  @Test
  public void testVisitFuncPassSameType() {
    final WACCParser.FuncContext funcCtx
        = context.mock(WACCParser.FuncContext.class);
    context.checking(new Expectations() {{
      oneOf(typeCtx).getText(); will(returnValue(INT_TYPE));
      oneOf(funcCtx).type().getText(); will(returnValue(typeCtx));
      oneOf(funcCtx).paramList();
      oneOf(funcCtx).statList();
    }});

    Type type = typeChecker.visitFunc(funcCtx);
    assertNotNull(type);
    assertThat(type.getName(), is(INT_T));
  }

  @Test
  public void testVisitFuncFailDifferentType() {
    final WACCParser.FuncContext funcCtx
        = context.mock(WACCParser.FuncContext.class);
    context.checking(new Expectations() {{
      oneOf(typeCtx).getText(); will(returnValue(BOOL_TYPE));
      oneOf(funcCtx).type().getText(); will(returnValue(typeCtx));
      oneOf(funcCtx).paramList();
      oneOf(funcCtx).statList();
    }});

    Type type = typeChecker.visitFunc(funcCtx);
    assertNotNull(type);

    //TODO: Error Handling Tests
  }

  @Test
  public void testVisitFuncFailNull() {
    final WACCParser.FuncContext funcCtx
        = context.mock(WACCParser.FuncContext.class);
    context.checking(new Expectations() {{
      oneOf(typeCtx).getText(); will(returnValue(INVALID_TYPE));
      oneOf(funcCtx).type().getText(); will(returnValue(typeCtx));
      oneOf(funcCtx).paramList();
      oneOf(funcCtx).statList();
    }});

    Type type = typeChecker.visitFunc(funcCtx);
    assertNull(type);

    //TODO: Error Handling Tests
  }

}