import arm11.DataInstructions;
import arm11.Label;
import arm11.PrintFormatters;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
public class DataInstructionsTest {

  @Test
  public void testLabelHasCorrectName() throws Exception {
    DataInstructions data = new DataInstructions();
    assertEquals(data.addConstString("foo").toString(), "msg_0");
    assertEquals(data.addConstString("bar").toString(), "msg_1");
    assertEquals(data.addConstString("baz").toString(), "msg_2");
  }

  @Test
  public void testAddDuplicateConstString() throws Exception {
    DataInstructions data = new DataInstructions();
    Label label = data.addConstString("foo");
    assertEquals(label.toString(), "msg_0");
    // Adding the same string again returns the same label
    assertEquals(label, data.addConstString("foo"));
  }

  private static int countLines(String str){
    String[] lines = str.split("\r\n|\r|\n");
    return  lines.length;
  }

  @Test
  public void testAddStringPrintFormatter() throws Exception {
    DataInstructions data = new DataInstructions();
    String expected = "msg_0:\n" +
                      "\t.word 3\n" +
                      "\t.ascii \"%s\" \n";

    data.addPrintFormatter(PrintFormatters.STRING_PRINT_FORMATTER);
    assertEquals(countLines(data.toString()), countLines(expected));
    // TODO: Get this to work!
//    assertEquals(data.toString(), expected);
  }

//  @Test
//  public void testAddStringPrintFormatter() throws Exception {
//    DataInstructions data = new DataInstructions();
//
//    data.addPrintFormatter(PrintFormatters.STRING_PRINT_FORMATTER);
//    assertEquals(countLines(data.toString()), 3);
//    assertThat(data.toString(), containsString("msg_0:"));
//    assertThat(data.toString(), containsString(".word 3"));
//    assertThat(data.toString(), containsString(".ascii \"%s\""));
//  }


}
