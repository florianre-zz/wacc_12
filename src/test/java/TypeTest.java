import bindings.*;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class TypeTest {

  @Test
  public void testIntType() {
    Type intType = new Type(Types.INT_T.toString());
    assertThat(intType.toString(), is("int"));
  }

  @Test
  public void testBoolType() {
    Type intType = new Type(Types.BOOL_T.toString());
    assertThat(intType.toString(), is("bool"));
  }

  @Test
  public void testCharType() {
    Type intType = new Type(Types.CHAR_T.toString());
    assertThat(intType.toString(), is("char"));
  }

  @Test
  public void testStringType() {
    Type intType = new Type(Types.STRING_T.toString());
    assertThat(intType.toString(), is("string"));
  }

  @Test
  public void testBaseArrayType() {
    Type baseType = new Type(Types.INT_T.toString());
    ArrayType arrayType = new ArrayType(baseType);
    assertThat(arrayType.toString(), is("int[]"));
  }

  @Test
  public void testMultiArrayType() {
    Type baseType = new Type(Types.INT_T.toString());
    ArrayType innerArray = new ArrayType(baseType);
    ArrayType outerArray = new ArrayType(innerArray);
    assertThat(outerArray.toString(), is("int[][]"));
  }

  @Test
  public void testMultiArrayType2() {
    Type baseType = new Type(Types.INT_T.toString());
    ArrayType array = new ArrayType(baseType, 2);
    assertThat(array.toString(), is("int[][]"));
  }

  @Test
  public void testMultiArrayType3() {
    Type baseType = new Type(Types.INT_T.toString());
    ArrayType innerArray = new ArrayType(baseType);
    ArrayType outerArray = new ArrayType(innerArray);
    outerArray = new ArrayType(outerArray);
    assertThat(outerArray.toString(), is("int[][][]"));
  }

  @Test
  public void testPairType() {
    Type fstType = new Type(Types.INT_T.toString());
    Type sndType = new Type(Types.STRING_T.toString());
    PairType pairType = new PairType(fstType, sndType);
    assertThat(pairType.toString(), is("pair(int, string)"));
  }

  @Test
  public void testPairLitrPairType() {
    Type pairLitr = new PairType();
    Type baseType = new Type(Types.INT_T.toString());
    PairType pairType = new PairType(baseType, pairLitr);
    assertThat(pairType.toString(), is("pair(int, pair)"));
  }

  @Test
  public void testPairPairType() {
    Type fstType = new Type(Types.INT_T.toString());
    Type sndType = new Type(Types.STRING_T.toString());

    PairType fstInnerPair
        = new PairType(fstType, sndType);
    PairType sndInnerPair
        = new PairType(sndType, fstType);

    PairType outerPair
        = new PairType(fstInnerPair, sndInnerPair);

    String type = "pair(pair, pair)";
    assertThat(outerPair.toString(), is(type));
  }

  @Test
  public void testPairOfArrays() {
    Type fstBaseType = new Type(Types.INT_T.toString());
    Type sndBaseType = new Type(Types.CHAR_T.toString());
    ArrayType fstArrayType = new ArrayType(fstBaseType);
    ArrayType sndArrayType = new ArrayType(sndBaseType);
    PairType pairType = new PairType(fstArrayType, sndArrayType);
    String type = "pair(int[], char[])";
    assertThat(pairType.toString(), is(type));
  }

  @Test
  public void testArrayOfPairs() {
    Type fstBaseType = new Type(Types.INT_T.toString());
    Type sndBaseType = new Type(Types.CHAR_T.toString());
    PairType pairType = new PairType(fstBaseType, sndBaseType);
    ArrayType arrayType = new ArrayType(pairType);
    String type = "pair(int, char)[]";
    assertThat(arrayType.toString(), is(type));
  }

  @Test
  public void testGenericPairsAreEqual() {
    assertEquals(new PairType(), new PairType());
  }

  @Test
  public void testGenericPairsAreEqualToAnyPair() {
    Type baseType = new Type(Types.INT_T);
    assertEquals(new PairType(), new PairType(baseType, baseType));
  }

  @Test
  public void testGenericPairsAreEqualToAnyPairWithGenericSubtypes() {
    PairType pairType = new PairType();
    assertEquals(new PairType(), new PairType(pairType, pairType));
    assertEquals(new PairType(pairType, pairType), new PairType());
  }

  @Test
  public void testStringIsCharArray() {
    Type charArray = new ArrayType(new Type(Types.CHAR_T));
    Type string = new Type(Types.STRING_T);

    assert string.equals(charArray);
  }

  @Test
  public void testCharArrayIsString() {
    Type charArray = new ArrayType(new Type(Types.CHAR_T));
    Type string = new Type(Types.STRING_T);

    assert charArray.equals(string);
  }

  @Test
  public void testArrayOfGenericPairsEqualToArrayOfNonGenericPairs() {
    PairType genericPair = new PairType();
    Type intType = new Type(Types.INT_T);
    ArrayType genericPairArrayType = new ArrayType(genericPair);
    PairType intPair = new PairType(intType, intType);
    ArrayType pairIntArrayType = new ArrayType(intPair);
    assertEquals(pairIntArrayType, genericPairArrayType);
    assertEquals(genericPairArrayType, pairIntArrayType);
  }

}
