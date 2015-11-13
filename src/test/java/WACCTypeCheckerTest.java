import antlr.WACCParser;
import bindings.Binding;
import bindings.Type;
import bindings.Types;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.*;
import wacc.SymbolTable;
import wacc.WACCTypeChecker;
import wacc.error.*;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;


public class WACCTypeCheckerTest {

  public static final String INVALID_TYPE = "0type";

  private WACCTypeChecker typeChecker;

  @Rule
  public JUnitRuleMockery context = new JUnitRuleMockery() {{
    setImposteriser(ClassImposteriser.INSTANCE);
  }};

  WACCParser.TypeContext typeCtx;
  ErrorHandler errorHandler;

  @Before
  public void setUp() throws Exception {

    typeCtx = context.mock(WACCParser.TypeContext.class);
    errorHandler = new ErrorHandler();

    SymbolTable<String, Binding> top = new SymbolTable<>();
    top.put("int", new Type(Types.INT_T, Integer.MIN_VALUE, Integer.MAX_VALUE));
    top.put("bool", new Type(Types.BOOL_T, 0, 1));
    top.put("char", new Type(Types.CHAR_T, 0, 255));
    top.put("string", new Type(Types.STRING_T));

    typeChecker = new WACCTypeChecker(top, errorHandler);
  }

  @After
  public void tearDown() throws Exception {

  }


}