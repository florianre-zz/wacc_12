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

  public static final String TEST_KEY = "testKey";
  @Rule
  public JUnitRuleMockery context = new JUnitRuleMockery() {{
    setImposteriser(ClassImposteriser.INSTANCE);
  }};

  private SymbolTable<String, Binding> symbolTable = new SymbolTable<>();
  private SymbolTable<String, Binding> parentSymbolTable = new SymbolTable<>();
  private Binding binding = context.mock(Binding.class, "binding");

  @Test
  public void testLookupAllNull() throws Exception {
    assertNull(symbolTable.lookupAll(TEST_KEY));
  }

  @Test
  public void testLookupAllInChildNoParent() throws Exception {
    symbolTable.put(TEST_KEY, binding);
    assertThat(symbolTable.lookupAll(TEST_KEY), is(binding));
  }

  @Test
  public void testLookupAllInChildWithParent() throws Exception {
    symbolTable = new SymbolTable<>(parentSymbolTable);
    symbolTable.put(TEST_KEY, binding);
    assertThat(symbolTable.lookupAll(TEST_KEY), is(binding));
  }

  @Test
  public void testLookupAllInChildAndParent() throws Exception {
    symbolTable = new SymbolTable<>(parentSymbolTable);
    symbolTable.put(TEST_KEY, binding);
    Binding parentBinding = context.mock(Binding.class, "parentBinding");
    parentSymbolTable.put(TEST_KEY, parentBinding);
    assertThat(symbolTable.lookupAll(TEST_KEY), is(binding));
  }

  @Test
  public void testLookupAllInParentNotChild() throws Exception {
    symbolTable = new SymbolTable<>(parentSymbolTable);
    parentSymbolTable.put(TEST_KEY, binding);
    assertThat(symbolTable.lookupAll(TEST_KEY), is(binding));
  }

  @Test
  public void testGetEnclosingSTNull() throws Exception {
    assertNull(symbolTable.getEnclosingST());
  }

  @Test
  public void testGetEnclosingSTParent() throws Exception {
    symbolTable = new SymbolTable<>(parentSymbolTable);
    assertThat(symbolTable.getEnclosingST(), is(parentSymbolTable));
  }
}
