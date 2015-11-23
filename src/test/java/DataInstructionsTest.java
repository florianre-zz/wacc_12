import arm11.DataInstructions;
import arm11.Label;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
public class DataInstructionsTest {

  @Test
  public void testLabelHasCorrectName() throws Exception {
    DataInstructions data = new DataInstructions();
    assertEquals(data.addConstString("foo").toString(), "msg_0");
    assertEquals(data.addConstString("bar").toString(), "msg_1");
    assertEquals(data.addConstString("baz").toString(), "msg_2");
    System.out.println(data);
  }

  @Test
  public void testAddDuplicateConstString() throws Exception {
    DataInstructions data = new DataInstructions();
    Label label = data.addConstString("foo");
    assertEquals(label.toString(), "msg_0");
    // Adding the same string again returns the same label
    assertEquals(label, data.addConstString("foo"));
  }

}
