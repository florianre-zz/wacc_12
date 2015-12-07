package arm11;

import org.junit.Test;

import java.util.HashSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class DataInstructionsTest {

  @Test
  public void testLabelHasCorrectName() throws Exception {
    DataInstructions data = new DataInstructions();
    assertEquals(data.addConstString("foo").toString(), "msg_0");
    assertEquals(data.addConstString("bar").toString(), "msg_1");
    assertEquals(data.addConstString("baz").toString(), "msg_2");
  }

//  @Test
//  public void testAddDuplicateConstString() throws Exception {
//    DataInstructions data = new DataInstructions();
//    Label label = data.addConstString("foo");
//    assertEquals(label.toString(), "msg_0");
//    // Adding the same string again returns the same label
//    assertEquals(label, data.addConstString("foo"));
//  }

  @Test
  public void testSameHashCodes() {
    DataInstructions data = new DataInstructions();
    assertEquals(PrintFunctions.printBool(data).hashCode(),
        PrintFunctions.printBool(data).hashCode());
    assertEquals(PrintFunctions.printString(data).hashCode(),
        PrintFunctions.printString(data).hashCode());
    assertNotEquals(PrintFunctions.printBool(data).hashCode(),
        PrintFunctions.printString(data).hashCode());
  }

  @Test
  public void testAddingTheSameHelperFunctionToSet() {
    DataInstructions data = new DataInstructions();
    HashSet<InstructionList> helperFunctions = new HashSet<>();
    assertEquals(0, helperFunctions.size());
    InstructionList printBool = PrintFunctions.printBool(data);
    helperFunctions.add(printBool);
    assertEquals(1, helperFunctions.size());
    InstructionList printBoolAgain = PrintFunctions.printBool(data);
    assertEquals(printBool.hashCode(), printBoolAgain.hashCode());
    helperFunctions.add(printBoolAgain);
    assertEquals(1, helperFunctions.size());
    helperFunctions.add(PrintFunctions.printString(data));
    assertEquals(2, helperFunctions.size());
    helperFunctions.add(PrintFunctions.printString(data));
    assertEquals(2, helperFunctions.size());
  }

  private static int countLines(String str){
    String[] lines = str.split("\r\n|\r|\n");
    return  lines.length;
  }

//  @Test
//  public void testAddStringPrintFormatter() throws Exception {
//    DataInstructions data = new DataInstructions();
//    String expected = ".data\n" +
//                      "msg_0:\n" +
//                      "\t.word 3\n" +
//                      "\t.ascii \"%s\" \n";
//
//    data.addPrintFormatter(IOFormatters.STRING_FORMATTER);
//    assertEquals(countLines(data.toString()), countLines(expected));
//    // TODO: Get this to work!
////    assertEquals(data.toString(), expected);
//  }

//  @Test
//  public void testAddStringPrintFormatter() throws Exception {
//    DataInstructions data = new DataInstructions();
//
//    data.addPrintFormatter(IOFormatters.STRING_FORMATTER);
//    assertEquals(countLines(data.toString()), 3);
//    assertThat(data.toString(), containsString("msg_0:"));
//    assertThat(data.toString(), containsString(".word 3"));
//    assertThat(data.toString(), containsString(".ascii \"%s\""));
//  }


}