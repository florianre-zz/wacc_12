package wacc;

import antlr.WACCParser;
import arm11.*;
import bindings.*;
import org.antlr.v4.runtime.ParserRuleContext;
import wacc.error.DeclarationError;
import wacc.error.WACCErrorHandler;
import wacc.error.TypeAssignmentError;

import java.util.List;

import static arm11.ARM11Registers.SP;

public class Utils {

  public static boolean isReadable(Type lhsType) {
    return Type.isInt(lhsType) || Type.isChar(lhsType);
  }

  public static boolean isFreeable(Type exprType) {
    return ArrayType.isArray(exprType) || PairType.isPair(exprType);
  }

  public static void incorrectType(ParserRuleContext ctx,
                                   Type exprType, String expectedType,
                                   WACCErrorHandler errorHandler) {
    String actual = exprType != null ? exprType.toString() : "'null'";
    errorHandler.complain(
        new TypeAssignmentError(ctx, expectedType, actual)
    );
  }

  public static boolean checkTypesEqual(ParserRuleContext ctx,
                                        Type lhsType, Type rhsType,
                                        WACCErrorHandler errorHandler) {
    if (lhsType != null) {
      if (!lhsType.equals(rhsType)) {
        incorrectType(ctx, rhsType, lhsType.toString(), errorHandler);
        return false;
      }
      return true;
    }
    return false;
  }

  public static Type lookupTypeInWorkingSymbolTable(
      String key, SymbolTable<String, Binding> workingSymbolTable) {
    Binding b = workingSymbolTable.lookupAll(key);
    if (b instanceof Variable) {
      return ((Variable) b).getType();
    }
    if (b instanceof Function) {
      return ((Function) b).getType();
    }
    return null;
  }

  public static void inconsistentParamCountError(WACCParser.CallContext ctx,
                                                 int expectedSize,
                                                 int actualSize,
                                                 WACCErrorHandler errorHandler)
  {
    StringBuilder sb = new StringBuilder();
    sb.append("The number of arguments doesn't match function declaration: ");

    sb.append(ctx.getText()).append("\n");
    sb.append("There are currently ").append(actualSize);
    sb.append(" params, there should be ");
    sb.append(expectedSize);

    String errorMsg = sb.toString();
    errorHandler.complain(new DeclarationError(ctx, errorMsg));
  }

  public static String getToken(int index){
    String tokenName = WACCParser.tokenNames[index];
    assert(tokenName.charAt(0) != '\'');
    return tokenName.substring(1, tokenName.length() - 1);
  }

  public static InstructionList getAllocationInstructions
                                      (long stackSpaceVarSize) {
    InstructionList list = new InstructionList();
    Immediate imm;
    while (stackSpaceVarSize > 1024L) {
      imm = new Immediate(1024L);
      list.add(InstructionFactory.createSub(SP, SP, imm));
      stackSpaceVarSize -= 1024L;
    }
    imm = new Immediate(stackSpaceVarSize);
    return list.add(InstructionFactory.createSub(SP, SP, imm));
  }

  public static void addOffsetToParam(List<Binding> variables, long offset) {
    offset += 4;
    for (Binding b : variables) {
      Variable v = (Variable) b;
      if (v.isParam()) {
        v.setOffset(offset);
        offset += v.getType().getSize();
      }
    }
  }

  public static long addOffsetsToVariables(List<Binding> variables) {
    // First pass to add offsets to variables
    long offset = 0;
    for (int i = variables.size() - 1; i >= 0; i--) {
      Variable v = (Variable) variables.get(i);
      if (!v.isParam()) {
        v.setOffset(offset);
        offset += v.getType().getSize();
      }
    }
    return offset;
  }

  public static InstructionList allocateSpaceOnStack
                            (SymbolTable<String, Binding> workingSymbolTable) {
    InstructionList list = new InstructionList();
    List<Binding> variables = workingSymbolTable.filterByClass(Variable.class);
    long stackSpaceVarSize = 0;

    for (Binding b : variables) {
      Variable v = (Variable) b;
      if (!v.isParam()) {
        stackSpaceVarSize += v.getType().getSize();
      }
    }
    if (stackSpaceVarSize > 0) {
      list.add(Utils.getAllocationInstructions(stackSpaceVarSize));
    }
    String scopeName = workingSymbolTable.getName();
    Binding scopeB = workingSymbolTable.getEnclosingST().get(scopeName);
    NewScope scope = (NewScope) scopeB;
    scope.setStackSpaceSize(stackSpaceVarSize);

    long offset = Utils.addOffsetsToVariables(variables);
    Utils.addOffsetToParam(variables, offset);

    return list;
  }

  private static long getAccumulativeStackSizeFromReturn
                            (SymbolTable<String, Binding> workingSymbolTable) {
    long accumulativeStackSize = 0;
    SymbolTable<String, Binding> currentSymbolTable = workingSymbolTable;
    while (currentSymbolTable != null) {
      String scopeName = currentSymbolTable.getName();
      SymbolTable<String, Binding> parent = currentSymbolTable.getEnclosingST();
      NewScope currentScope = (NewScope) parent.get(scopeName);
      accumulativeStackSize += (currentScope).getStackSpaceSize();
      if (currentScope instanceof Function) {
        break;
      }
      currentSymbolTable = parent;
    }

    return accumulativeStackSize;
  }

  public static InstructionList deallocateSpaceOnStackFromReturn(
    SymbolTable<String, Binding> workingSymbolTable) {
    InstructionList list = new InstructionList();
    long stackSpaceSize
      = Utils.getAccumulativeStackSizeFromReturn(workingSymbolTable);
    if (stackSpaceSize > 0) {
      Operand imm = new Immediate(stackSpaceSize);
      Register sp = SP;
      list.add(InstructionFactory.createAdd(sp, sp, imm));
    }

    return list;
  }

}
