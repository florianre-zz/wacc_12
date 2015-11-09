import wacc.SymbolTable;

import org.jmock.integration.junit4.JUnitRuleMockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Rule;
import org.junit.Test;
import wacc.SymbolTable;

public class SymbolTableTest {

  @Rule
  public JUnitRuleMockery context = new JUnitRuleMockery() {{
    setImposteriser(ClassImposteriser.INSTANCE);
  }};

  SymbolTable symbolTable = new SymbolTable();

  @Test
  public void testLookupAll() throws Exception {

  }

  @Test
  public void testGetEnclosingST() throws Exception {

  }
}
