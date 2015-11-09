import bindings.Binding;
import wacc.SymbolTable;

import org.jmock.integration.junit4.JUnitRuleMockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Rule;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

public class SymbolTableTest {

  @Rule
  public JUnitRuleMockery context = new JUnitRuleMockery() {{
    setImposteriser(ClassImposteriser.INSTANCE);
  }};

  SymbolTable<String, Binding> symbolTable = new SymbolTable<>();

  @Test
  public void testLookupAll() throws Exception {

  }

  @Test
  public void testGetEnclosingSTNull() throws Exception {
    SymbolTable<String, Binding> st = new SymbolTable<>();
    assertNull(st.getEnclosingST());
  }

  @Test
  public void testGetEnclosingSTParent() throws Exception {
    SymbolTable<String, Binding> st = new SymbolTable<>(symbolTable);
    assertThat(st.getEnclosingST(), is(symbolTable));
  }
}
