package wacc;

import antlr.WACCParser;
import arm11.*;
import bindings.*;
import org.antlr.v4.runtime.ParserRuleContext;
import wacc.error.DeclarationError;
import wacc.error.WACCErrorHandler;
import wacc.error.TypeAssignmentError;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static arm11.ARM11Registers.R0;
import static arm11.ARM11Registers.SP;
import static arm11.InstructionType.*;

public class Utils {

  private static final long MAX_IMM_SIZE = 1024L; // 2^10

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

  public static List<Variable> getParamList(WACCParser.FuncContext ctx,
                                            WACCTypeCreator typeCreator) {
    List<Variable> funcParams = new ArrayList<>();

    if (ctx.paramList() != null) {
      List<? extends WACCParser.ParamContext> paramContexts =
              ctx.paramList().param();

      for (WACCParser.ParamContext paramContext : paramContexts) {
        String name = paramContext.name.getText();
        Type type = typeCreator.visitParam(paramContext);

        Variable param = new Variable(name, type);
        funcParams.add(param);
      }
    }
    return funcParams;
  }

  public static String getParamString(List<Type> types) {
    return getArgString(types);
  }

  public static String getArgString(List<Type> types) {
    StringBuilder sb = new StringBuilder();

    for (Type t : types) {
      sb.append(".").append(getNameForLabel(t));
    }
    return sb.append(".").toString();
  }

  private static String getNameForLabel(Type t) {
//    System.err.println("Label: " + t.toLabel());
    return t.toLabel();
  }

  public static String getFuncParamTypeSuffix(List<Variable> params) {
    List<Type> types = new ArrayList<>();
    for (Variable v : params) {
      types.add(v.getType());
    }
    return getArgString(types);
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

  public static InstructionList getAllocationInstructions(long stackVarSize,
                                                         InstructionType type) {
    InstructionList list = new InstructionList();
    if (stackVarSize > 0) {
      Immediate imm;
      while (stackVarSize > MAX_IMM_SIZE) {
        imm = new Immediate(MAX_IMM_SIZE);
        stackVarSize -= MAX_IMM_SIZE;
        list.add(InstructionFactory.mutateStackPointer(type, imm));
      }
      imm = new Immediate(stackVarSize);
      list.add(InstructionFactory.mutateStackPointer(type, imm));
    }
    return list;
  }

  public static void addOffsetToParams(List<Binding> variables, long offset) {
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

  public static InstructionList allocateSpaceOnStack(
                            SymbolTable<String, Binding> workingSymbolTable) {
    InstructionList list = new InstructionList();
    List<Binding> variables = workingSymbolTable.filterByClass(Variable.class);
    long stackSpaceVarSize = 0;

    for (Binding b : variables) {
      Variable v = (Variable) b;
      if (!v.isParam()) {
        stackSpaceVarSize += v.getType().getSize();
      }
    }
    list.add(Utils.getAllocationInstructions(stackSpaceVarSize, SUB));
    String scopeName = workingSymbolTable.getName();
    Binding scopeB = workingSymbolTable.getEnclosingST().get(scopeName);
    NewScope scope = (NewScope) scopeB;
    scope.setStackSpaceSize(stackSpaceVarSize);

    long offset = Utils.addOffsetsToVariables(variables);
    Utils.addOffsetToParams(variables, offset);

    return list;
  }

  public static InstructionList deallocateSpaceOnStack(
      SymbolTable<String, Binding> workingSymbolTable) {
    InstructionList list = new InstructionList();
    String scopeName = workingSymbolTable.getName();
    Binding scopeB = workingSymbolTable.getEnclosingST().get(scopeName);
    NewScope scope = (NewScope) scopeB;

    long stackSpaceSize = scope.getStackSpaceSize();

    list.add(Utils.getAllocationInstructions(stackSpaceSize, ADD));

    return list;
  }

  private static long getAccumulativeStackSizeFromReturn(
                            SymbolTable<String, Binding> workingSymbolTable) {

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

  public static void addFunctionToHelpers(InstructionList function,
                                          HashSet<InstructionList>
                                            helperFunctions) {
    if (function != null) {
      helperFunctions.add(function);
    }
  }


  public static void printNewLine(InstructionList list, DataInstructions data,
                                  HashSet<InstructionList> helperFunctions) {
    Label printLabel = new Label("p_print_ln");
    list.add(InstructionFactory.createBranchLink(printLabel));
    Utils.addFunctionToHelpers(PrintFunctions.printLn(data), helperFunctions);
  }

  public static Long totalListSize(
                                List<? extends WACCParser.ExprContext> exprs) {
    Long totalSize = 0L;
    for (WACCParser.ExprContext exprCtx : exprs) {
      totalSize += exprCtx.returnType.getSize();
    }
    return totalSize;
  }

  public static void addRuntimeErrorFunctionsToHelpers(InstructionList err,
                                                       DataInstructions data,
                                                       HashSet<InstructionList>
                                                         helperFunctions) {
    Utils.addFunctionToHelpers(err, helperFunctions);
    addThrowRuntimeErrorFunctionsToHelpers(data, helperFunctions);
  }

  public static void addThrowRuntimeErrorFunctionsToHelpers(
            DataInstructions data, HashSet<InstructionList> helperFunctions) {
    Utils.addFunctionToHelpers(RuntimeErrorFunctions.throwRuntimeError(data),
        helperFunctions);
    Utils.addFunctionToHelpers(PrintFunctions.printString(data),
                               helperFunctions);
  }

  public static InstructionList storeLengthOfArray(long numberOfElems,
                                             Register addressOfArray,
                                             Register lengthOfArray) {

    InstructionList list = new InstructionList();
    list.add(InstructionFactory.createLoad(lengthOfArray,
                                           new Immediate(numberOfElems)))
        .add(InstructionFactory.createStore(lengthOfArray,
                                            addressOfArray,
                                            new Immediate(0L)));
    return list;
  }

  public static InstructionList allocateArrayAddress(long bytesToAllocate,
                                               Label malloc,
                                               Register addressOfArray,
                                               AccumulatorMachine accMachine) {
    InstructionList list = new InstructionList();
    list.add(InstructionFactory.createLoad(R0, new Immediate(bytesToAllocate)))
        .add(InstructionFactory.createBranchLink(malloc))
        .add(accMachine.getInstructionList(MOV, addressOfArray, R0));
    return list;
  }

  public static long getTypeSize(WACCParser.ArrayLitrContext ctx) {
    Type returnType = ctx.expr().get(0).returnType;
    return returnType.getSize();
  }

}
