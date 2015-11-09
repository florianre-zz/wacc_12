import bindings.Binding;
import bindings.SymbolTable;

import org.jmock.integration.junit4.JUnitRuleMockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Rule;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class SymbolTableTest {

  @Rule
  public JUnitRuleMockery context = new JUnitRuleMockery() {{
    setImposteriser(ClassImposteriser.INSTANCE);
  }};

  SymbolTable symbolTable = new SymbolTable();
  Binding binding = new Binding("binding", null);

  @Test
  public void testAddWhenKeyDoesNotExistInDict() throws Exception {
    symbolTable.put("name", binding);
    assertThat(null, is(symbolTable.get("name")));
  }

  @Test
  public void testAddWhenKeyDoesExistInDict() throws Exception {
    symbolTable.put("name", binding);
    Binding duplicateBinding = new Binding("duplicateBinding", null);
    symbolTable.put("name", duplicateBinding);
    assertThat(binding, is(symbolTable.get("name")));
  }

  @Test
  public void testLookup() throws Exception {

  }

  @Test
  public void testLookupAll() throws Exception {

  }

  @Test
  public void testGetEnclosingST() throws Exception {

  }
}
