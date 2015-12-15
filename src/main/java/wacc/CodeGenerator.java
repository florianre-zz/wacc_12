package wacc;

import antlr.WACCParser;
import arm11.*;
import arm11.Shift.Shifts;
import bindings.Binding;
import bindings.PairType;
import bindings.Type;
import bindings.Variable;
import java.util.HashSet;
import static antlr.WACCParser.*;
import static arm11.ARM11Registers.*;
import static arm11.HeapFunctions.freePair;
import static arm11.InstructionFactory.*;
import static arm11.InstructionType.*;

public class CodeGenerator extends WACCVisitor<InstructionList> {

  private static final boolean DEBUGGING = false;

  private static final long ADDRESS_SIZE = 4L;
  private static final long PAIR_SIZE = 2 * ADDRESS_SIZE;

  private AccumulatorMachine accMachine;
  private DataInstructions data;
  private HashSet<InstructionList> helperFunctions;
  private boolean isAssigning;

  public CodeGenerator(SymbolTable<String, Binding> top) {
    super(top);
    this.data = new DataInstructions();
    this.helperFunctions = new HashSet<>();
    this.accMachine = new AccumulatorMachine();
  }

  @Override
  protected InstructionList defaultResult() {
    return new InstructionList();
  }

  @Override
  protected InstructionList aggregateResult(InstructionList aggregate,
                                            InstructionList nextResult) {
    aggregate.add(nextResult);
    return aggregate;
  }

  private InstructionList allocateSpaceForPairElem(Label malloc,
                                                   ExprContext exprCtx,
                                                   Long size, Register next) {
    InstructionList list = defaultResult();
    list.add(visitExpr(exprCtx))
        .add(createLoad(R0, new Immediate(size)))
        .add(createBranchLink(malloc));

    if (size == 1) {
      list.add(createStoreByte(next, R0, new Immediate(0L)));
    } else {
      list.add(createStore(next, R0, new Immediate(0L)));
    }

    return list;
  }

  private InstructionList allocateSpaceForNewPair(Label malloc,
                                                  Immediate sizeOfObject) {
    InstructionList list = defaultResult();
    list.add(createLoad(R0, sizeOfObject))
        .add(createBranchLink(malloc));
    return list;
  }

  private void storeArrayElem(InstructionList list,
                              Register addressOfArray,
                              long offset,
                              ExprContext elem,
                              Register result) {
    Immediate imm = new Immediate(offset);
    list.add(visitExpr(elem));
    if (elem.returnType.getSize() == ADDRESS_SIZE) {
      list.add(createStore(result, addressOfArray, imm));
    } else {
      list.add(createStoreByte(result, addressOfArray, imm));
    }
  }

  /**
   * given an offset, variable type and an AssignRHS context
   * returns instructions for storing the value of the RHS to that offset
   */
  private InstructionList storeToOffset(long varOffset,
                                        Type varType,
                                        AssignRHSContext assignRHS) {
    InstructionList list = defaultResult();

    Register reg = accMachine.peekFreeRegister();

    list.add(visitAssignRHS(assignRHS));

    InstructionList storeInstr;
    Immediate offset = new Immediate(varOffset);
    if (Type.isBool(varType) || Type.isChar(varType)) {
      storeInstr = accMachine.getInstructionList(STRB, reg, SP, offset);
    } else {
      storeInstr = accMachine.getInstructionList(STR, reg, SP, offset);
    }

    list.add(storeInstr);
    accMachine.pushFreeRegister(reg);
    return list;
  }

  /**
   * Gets relevant instructions for expression to print
   * Adds instruction to move the expression to relevant register
   * Adds call for correct print procedure
   */
  private InstructionList printExpression(ExprContext ctx,
                                          Label printLabel,
                                          Register result) {
    InstructionList list = defaultResult();
    list.add(visitExpr(ctx))
        .add(createMove(R0, result))
        .add(createBranchLink(printLabel));
    return list;
  }

  /**
   * Gets instructions to call for divide by zero procedure
   * Adds Labels and relevant move instructions for a div or a mod operation
   * given src registers
   */
  private InstructionList divMoves(Register dst1, Register dst2, String op) {
    InstructionList list =  defaultResult();
    Label checkDivideByZeroLabel = new Label("p_check_divide_by_zero");
    list.add(accMachine.getInstructionList(DIVMOD, dst1, dst2))
        .add(createBranchLink(checkDivideByZeroLabel));
    if (op.equals(Utils.getToken(DIV))){
      list.add(createDiv())
          .add(createMove(dst1, R0));
    } else {
      list.add(createMod())
          .add(createMove(dst1, R1));
    }
    Utils.addRuntimeErrorFunctionsToHelpers(
        RuntimeErrorFunctions.divideByZero(data), data, helperFunctions);

    return list;
  }

  /**
   * Gets the instructions of its children.
   * Creates the global label
   * Adds all the children's instructions together and the helper methods we
   * use in the assembly code
   */
  @Override
  public InstructionList visitProg(ProgContext ctx) {
    accMachine.resetFreeRegisters();
    String scopeName = Scope.PROG.toString();
    changeWorkingSymbolTableTo(scopeName);
    InstructionList program = defaultResult();

    InstructionList functions = defaultResult();
    for (FuncContext function : ctx.func()) {
      functions.add(visitFunc(function));
    }

    InstructionList main = visitMain(ctx.main());
    program.add(data.getInstructionList())
           .add(createText());
    Label mainLabel = new Label(WACCVisitor.Scope.MAIN.toString());
    program.add(createGlobal(mainLabel))
           .add(functions)
           .add(main);

    helperFunctions.forEach(program::add);

    goUpWorkingSymbolTable();
    return program;
  }

  /**
   * Sets up stack frame
   * Add instructions of its body
   * Sets exit code to 0
   */
  @Override
  public InstructionList visitMain(MainContext ctx) {
    InstructionList list = defaultResult();
    String scopeName = Scope.MAIN.toString();

    changeWorkingSymbolTableTo(scopeName);
    pushEmptyVariableSet();
    list.add(createLabel(new Label(Scope.MAIN.toString())))
        .add(createPush(LR))
        .add(Utils.allocateSpaceOnStack(workingSymbolTable))
        .add(visitChildren(ctx))
        .add(Utils.deallocateSpaceOnStack(workingSymbolTable))
        .add(createLoad(R0, new Immediate(0L)))
        .add(createPop(PC))
        .add(createLTORG());
    goUpWorkingSymbolTable();
    popCurrentScopeVariableSet();

    return list;
  }

  /**
   * Sets up stack frame
   * Gets instructions for each param
   * Adds instructions of body
   */
  @Override
  public InstructionList visitFunc(FuncContext ctx) {
    InstructionList list = defaultResult();
    changeWorkingSymbolTableTo(ScopeType.FUNCTION_SCOPE
                               + ctx.funcName.getText());
    pushEmptyVariableSet();

    Label functionLabel = new Label(ScopeType.FUNCTION_SCOPE
                                    + ctx.funcName.getText());

    if (ctx.paramList() != null) {
      visitParamList(ctx.paramList());
    }

    if (DEBUGGING) {
      System.err.println(functionLabel);
    }

    list.add(createLabel(functionLabel));
    list.add(createPush(LR))
        .add(Utils.allocateSpaceOnStack(workingSymbolTable))
        .add(visitStatList(ctx.statList()))
        .add(createPop(PC))
        .add(createLTORG());
    popCurrentScopeVariableSet();
    goUpWorkingSymbolTable();

    return list;
  }

  /**
   * Sets internal boolean (that informs us if a variable is a parameter) to
   * true
   */
  @Override
  public InstructionList visitParam(ParamContext ctx) {
    String name = ctx.name.getText();
    addVariableToCurrentScope(name);
    Variable var = (Variable) workingSymbolTable.lookupAll(name);
    var.setAsParam();
    return null;
  }

  /**
   * Gets offset for initialised Variable
   * Adds instructions for storing to the variable
   * Includes variable name to Variables declared in the scope
   */
  @Override
  public InstructionList visitInitStat(InitStatContext ctx) {
    String varName = ctx.ident().getText();
    Variable var = (Variable) workingSymbolTable.get(varName);
    long varOffset = var.getOffset();


    InstructionList list = storeToOffset(varOffset,
                                         var.getType(),
                                         ctx.assignRHS());
    addVariableToCurrentScope(varName);
    return list;
  }

  @Override
  public InstructionList visitAssignStat(AssignStatContext ctx) {
    InstructionList list = defaultResult();
    if (ctx.assignLHS().ident() != null) {
      String varName = ctx.assignLHS().ident().getText();
      Variable var = getMostRecentBindingForVariable(varName);
      long varOffset = getAccumulativeOffsetForVariable(varName);
      return storeToOffset(varOffset, var.getType(), ctx.assignRHS());
    } else {
      Register result = accMachine.peekFreeRegister();
      list.add(visitAssignRHS(ctx.assignRHS()));
      Register addr = accMachine.peekFreeRegister();

      list.add(visitAssignLHS(ctx.assignLHS()));
      Type varType = ctx.assignLHS().returnType;

      if (Type.isBool(varType) || Type.isChar(varType)) {
        list.add(accMachine.getInstructionList(STRB, result, addr));
      } else {
        list.add(accMachine.getInstructionList(STR, result, addr));
      }

      accMachine.pushFreeRegister(addr);
      accMachine.pushFreeRegister(result);
    }

    return list;
  }

  @Override
  public InstructionList visitAssignLHS(AssignLHSContext ctx) {
    InstructionList list = defaultResult();
    isAssigning = true;
    list.add(visitChildren(ctx));
    isAssigning = false;
    return list;
  }

  /**
   * Gets instructions for expr to read
   * Adds instruction to move expr in r0
   * Adds instruction to call for correct procedure
   */
  @Override
  public InstructionList visitReadStat(ReadStatContext ctx) {
    InstructionList list = defaultResult();
    Register reg = accMachine.popFreeRegister();

    if (ctx.assignLHS().ident() != null) {
      String name = ctx.assignLHS().ident().getText();
      Long offset = getAccumulativeOffsetForVariable(name);
      list.add(createAdd(reg, SP, new Immediate(offset)));
    } else if (ctx.assignLHS().pairElem() != null) {
      list.add(visitPairElem(ctx.assignLHS().pairElem()));
    } else {
      list.add(visitArrayElem(ctx.assignLHS().arrayElem()));
    }

    Label readLabel;
    if (Type.isInt(ctx.assignLHS().returnType)) {
      readLabel = new Label("p_read_int");
      helperFunctions.add(ReadFunctions.readInt(data));
    } else {
      readLabel = new Label("p_read_char");
      helperFunctions.add(ReadFunctions.readChar(data));
    }

    list.add(createMove(R0, reg))
        .add(createBranchLink(readLabel));
    accMachine.pushFreeRegister(reg);

    return list;
  }

  /**
   * Adds the correct print helper function for expression type
   * Gets instructions which call the print procedure
   * Adds print new line if necessary
   */
  @Override
  public InstructionList visitPrintStat(PrintStatContext ctx) {
    InstructionList list = defaultResult();
    Label printLabel;
    InstructionList printInstructions = null;
    Type returnType = ctx.expr().returnType;
    if (Type.isString(returnType)) {
      printLabel = new Label("p_print_string");
      printInstructions = PrintFunctions.printString(data);
    } else if (Type.isInt(returnType)) {
      printLabel = new Label("p_print_int");
      printInstructions = PrintFunctions.printInt(data);
    } else if (Type.isChar(returnType)){
      printLabel = new Label("putchar");
    } else if (Type.isBool(returnType)) {
      printLabel = new Label("p_print_bool");
      printInstructions = PrintFunctions.printBool(data);
    } else {
      printLabel = new Label("p_print_reference");
      printInstructions = PrintFunctions.printReference(data);
    }
    Utils.addFunctionToHelpers(printInstructions, helperFunctions);
    Register result = accMachine.peekFreeRegister();
    list.add(printExpression(ctx.expr(), printLabel, result));
    if (ctx.PRINTLN() != null) {
      Utils.printNewLine(list, data, helperFunctions);
    }
    accMachine.pushFreeRegister(result);

    return list;
  }

  /**
   * Adds instructions of the expr to free
   * Get instruction to mov the expression in R0
   * Call for free_pair procedure
   */
  @Override
  public InstructionList visitFreeStat(FreeStatContext ctx) {
    InstructionList list = defaultResult();

    Register result = accMachine.peekFreeRegister();
    list.add(visitExpr(ctx.expr()))
        .add(createMove(R0, result))
        .add(createBranchLink(new Label("p_free_pair")))
        .add(createMove(result, new Immediate(0L)))
        .add(createStore(result, SP, new Immediate(0L)));

    Utils.addThrowRuntimeErrorFunctionsToHelpers(data, helperFunctions);
    helperFunctions.add(freePair(data));
    accMachine.pushFreeRegister(result);

    return list;
  }

  /**
   * Moves the exit code to the relevant register
   * Calls for the exit procedure
   */
  @Override
  public InstructionList visitExitStat(ExitStatContext ctx) {
    InstructionList list = defaultResult();

    Register result = accMachine.peekFreeRegister();
    list.add(visitExpr(ctx.expr()));
    accMachine.pushFreeRegister(result);
    list.add(createMove(R0, result))
        .add(createBranchLink(new Label("exit")));

    return list;
  }

  /**
   * Generates instructions to move result value to result register
   * Adds instructions to free stack space
   * Adds pop PC
   */
  @Override
  public InstructionList visitReturnStat(ReturnStatContext ctx) {
    InstructionList list = defaultResult();
    Register resultReg = accMachine.peekFreeRegister();
    list.add(visitExpr(ctx.expr()))
        .add(createMove(R0, resultReg))
        .add(Utils.deallocateSpaceOnStackFromReturn(workingSymbolTable))
        .add(createPop(PC));
    accMachine.pushFreeRegister(resultReg);

    return list;
  }

  /**
   * Increments while count
   * Adds instructions for body
   * Adds comparison for the condition for the while loop
   */
  @Override
  public InstructionList visitWhileStat(WhileStatContext ctx) {
    ++whileCount;
    InstructionList list = defaultResult();

    String whileScope = Scope.WHILE.toString() + whileCount;
    changeWorkingSymbolTableTo(whileScope);
    pushEmptyVariableSet();

    Label predicate = new Label("predicate_" + whileCount);
    Label body = new Label("while_body_" + whileCount);
    Operand trueOp = new Immediate(1L);

    list.add(createBranch(predicate))
        .add(createLabel(body))
        .add(Utils.allocateSpaceOnStack(workingSymbolTable))
        .add(visitStatList(ctx.statList()))
        .add(Utils.deallocateSpaceOnStack(workingSymbolTable))
        .add(createLabel(predicate));

    popCurrentScopeVariableSet();
    goUpWorkingSymbolTable();

    Register result = accMachine.peekFreeRegister();
    list.add(visitExpr(ctx.expr()))
        .add(createCompare(result, trueOp))
        .add(createBranchEqual(body));
    accMachine.pushFreeRegister(result);

    return list;
  }

  /**
   * Increments if count
   * Adds comparison instructions for condition
   * Adds relevant bodies and branches
   */
  @Override
  public InstructionList visitIfStat(IfStatContext ctx) {
    ++ifCount;
    InstructionList list = defaultResult();

    Register predicate = accMachine.peekFreeRegister();
    list.add(visitExpr(ctx.expr()));
    list.add(createCompare(predicate, new Immediate(0L)));
    // predicate no longer required
    accMachine.pushFreeRegister(predicate);

    Label elseLabel = new Label("else_" + ifCount);
    Label continueLabel = new Label("fi_" + ifCount);
    if (ctx.ELSE() != null) {
      list.add(createBranchEqual(elseLabel))
          .add(getInstructionsForIfBranch(Scope.THEN.toString(), ctx.thenStat))
          .add(createBranch(continueLabel))
          .add(createLabel(elseLabel))
          .add(getInstructionsForIfBranch(Scope.ELSE.toString(), ctx.elseStat));
    } else {
      list.add(createBranchEqual(continueLabel))
          .add(getInstructionsForIfBranch(Scope.THEN.toString(), ctx.thenStat));
    }
    list.add(createLabel(continueLabel));

    return list;
  }

  /**
   * Get instructions to set stack frame
   * Adds instructions for body
   */
  @Override
  public InstructionList visitBeginStat(BeginStatContext ctx) {
    ++beginCount;
    InstructionList list = defaultResult();

    String beginScope = Scope.BEGIN.toString() + beginCount;
    changeWorkingSymbolTableTo(beginScope);
    pushEmptyVariableSet();

    list.add(Utils.allocateSpaceOnStack(workingSymbolTable))
        .add(visitStatList(ctx.statList()))
        .add(Utils.deallocateSpaceOnStack(workingSymbolTable));

    popCurrentScopeVariableSet();
    goUpWorkingSymbolTable();

    return list;
  }

  /**
   * Allocate size on the heap for the pair elems
   * Store the elements at the respective addresses in the heap
   */
  @Override
  public InstructionList visitNewPair(NewPairContext ctx) {
    InstructionList list = defaultResult();
    Label malloc = new Label("malloc");
    Register result = accMachine.popFreeRegister();
    Immediate sizeOfObject = new Immediate(PAIR_SIZE);
    list.add(allocateSpaceForNewPair(malloc, sizeOfObject));
    list.add(accMachine.getInstructionList(MOV, result, R0));
    Long accSize = 0L;

    for (ExprContext exprCtx : ctx.expr()) {
      Long size = (long) exprCtx.returnType.getSize();
      Register next = accMachine.peekFreeRegister();
      list.add(allocateSpaceForPairElem(malloc, exprCtx, size, next));
      accMachine.pushFreeRegister(next);
      list.add(createStore(R0, result, new Immediate(accSize)));
      accSize += ADDRESS_SIZE;
    }

    return list;
  }

  /**
   * Gets instructions for argList
   * Adds instruction to create space on the stack for the args
   */
  @Override
  public InstructionList visitCall(CallContext ctx) {
    InstructionList list = defaultResult();
    String functionName = ScopeType.FUNCTION_SCOPE + ctx.funcName.getText();
    Label functionLabel = new Label(functionName);

    if (ctx.argList() != null) {
      list.add(visitArgList(ctx.argList()));
    }
    list.add(createBranchLink(functionLabel));
    Register result = accMachine.popFreeRegister();
    if (ctx.argList() != null) {
      Operand size = new Immediate(Utils.totalListSize(ctx.argList().expr()));
      list.add(createAdd(SP, SP, size));
    }
    list.add(accMachine.getInstructionList(MOV, result, R0));

    return list;
  }

  /**
   * Gets instructions to store args on stack
   */
  @Override
  public InstructionList visitArgList(ArgListContext ctx) {
    InstructionList list = defaultResult();

    for (int i = ctx.expr().size() - 1; i >= 0; i--) {
      ExprContext exprCtx = ctx.expr(i);
      Register result = accMachine.peekFreeRegister();
      Long varSize = (long) -exprCtx.returnType.getSize();
      Operand size = new Immediate(varSize);
      list.add(visitExpr(exprCtx));
      if (varSize == -ADDRESS_SIZE) {
        list.add(createStore(result, SP, size));
      } else {
        list.add(createStoreByte(result, SP, size));
      }
      accMachine.pushFreeRegister(result);
      argOffset -= varSize;
    }
    argOffset = 0L;

    return list;
  }

  /**
   * Sets up stack frame for body
   * Adds instructions for body
   */
  private InstructionList getInstructionsForIfBranch(String branchName,
                                                     StatListContext ctx) {
    InstructionList list = defaultResult();

    String branchScope = branchName + ifCount;
    changeWorkingSymbolTableTo(branchScope);
    pushEmptyVariableSet();
    list.add(Utils.allocateSpaceOnStack(workingSymbolTable))
        .add(visitStatList(ctx))
        .add(Utils.deallocateSpaceOnStack(workingSymbolTable));
    popCurrentScopeVariableSet();
    goUpWorkingSymbolTable();

    return list;
  }

  /**
   * Gets instructions for first comparisonOper
   * If there are more operands, adds instructions to place each subsequent
   * operand and to evaluate the logical operation
   */
  @Override
  public InstructionList visitLogicalOper(LogicalOperContext ctx) {
    InstructionList list = defaultResult();
    Register dst1 = accMachine.peekFreeRegister();
    list.add(visitComparisonOper(ctx.first));
    if (!ctx.otherExprs.isEmpty()) {
      Register dst2;
      // for loop used instead of visitChildren so only 2 registers used up
      InstructionList logicalInstr;
      for (int i = 0; i < ctx.ops.size(); i++) {
        ComparisonOperContext otherExpr = ctx.otherExprs.get(i);
        dst2 = accMachine.peekFreeRegister();

        list.add(visitComparisonOper(otherExpr));

        if (ctx.ops.get(i).getText().equals(Utils.getToken(WACCParser.AND))){
          logicalInstr = accMachine.getInstructionList(InstructionType.AND,
                                                       dst1, dst1, dst2);
        } else {
          logicalInstr = accMachine.getInstructionList(ORR, dst1, dst1, dst2);
        }

        list.add(logicalInstr);
        accMachine.pushFreeRegister(dst2);
      }
    }

    return list;
  }

  /**
   * Gets instructions for first addOper
   * If there is another operand, adds instructions to place the second
   * operand and to evaluate the ordering operation
   */
  @Override
  public InstructionList visitOrderingOper(OrderingOperContext ctx) {
    InstructionList list = defaultResult();

    Register dst1 = accMachine.peekFreeRegister();
    list.add(visitAddOper(ctx.first));
    if (ctx.second != null) {
      Register dst2 = accMachine.peekFreeRegister();
      Operand trueOp = new Immediate((long) 1);
      Operand falseOp = new Immediate((long) 0);
      list.add(visitAddOper(ctx.second))
          .add(accMachine.getInstructionList(CMP, dst1, dst2));
      if (ctx.GT() != null){
        list.add(createMovGt(dst1, trueOp))
            .add(createMovLe(dst1, falseOp));
      } else if (ctx.GE() != null) {
        list.add(createMovGe(dst1, trueOp))
            .add(createMovLt(dst1, falseOp));
      } else if (ctx.LT() != null) {
        list.add(createMovLt(dst1, trueOp))
            .add(createMovGe(dst1, falseOp));
      } else if (ctx.LE() != null) {
        list.add(createMovLe(dst1, trueOp))
            .add(createMovGt(dst1, falseOp));
      }

      accMachine.pushFreeRegister(dst2);
    }

    return list;
  }

  /**
   * Gets instructions for first addOper
   * If there is another operand, adds instructions to place the second
   * operand and to evaluate the equality operation
   */
  @Override
  public InstructionList visitEqualityOper(EqualityOperContext ctx) {
    InstructionList list = defaultResult();
    Register dst1 = accMachine.peekFreeRegister();
    list.add(visitAddOper(ctx.first));
    if (ctx.second != null) {
      Register dst2 = accMachine.peekFreeRegister();

      long trueLong = (ctx.EQ() != null) ? 1 : 0;
      long falseLong = (ctx.EQ() != null) ? 0 : 1;
      Operand trueOp = new Immediate(trueLong);
      Operand falseOp = new Immediate(falseLong);
      list.add(visitAddOper(ctx.second))
          .add(accMachine.getInstructionList(CMP, dst1, dst2))
          .add(createMovEq(dst1, trueOp))
          .add(createMovNe(dst1, falseOp));
      accMachine.pushFreeRegister(dst2);
    }

    return list;
  }

  /**
   * Gets instructions for first multOper
   * If there are more operands, adds instructions to place each subsequent
   * operand and to evaluate the addition (or subtraction) operation
   */
  @Override
  public InstructionList visitAddOper(AddOperContext ctx) {
    Register dst1 = accMachine.peekFreeRegister();
    InstructionList list = defaultResult().add(visitMultOper(ctx.first));
    if (!ctx.otherExprs.isEmpty()) {
      Register dst2;
      // for loop used instead of visitChildren so only 2 registers used up
      for (int i = 0; i < ctx.ops.size(); i++) {
        MultOperContext otherExpr = ctx.otherExprs.get(i);
        dst2 = accMachine.peekFreeRegister();
        String op = ctx.ops.get(i).getText();
        list.add(visitMultOper(otherExpr));
        InstructionType opEnum;
        opEnum = (op.equals(Utils.getToken(PLUS)) ? ADDS : SUBS);
        list.add(accMachine.getInstructionList(opEnum, dst1, dst1, dst2));

        Label throwOverflowError = new Label("p_throw_overflow_error");
        list.add(createBranchLinkVS(throwOverflowError));

        Utils.addRuntimeErrorFunctionsToHelpers(
          RuntimeErrorFunctions.overflowError(data), data, helperFunctions);

        accMachine.pushFreeRegister(dst2);
      }
    }

    return list;
  }

  /**
   * Gets instructions for first atom
   * If there are more operands, adds instructions to place each subsequent
   * operand and to evaluate the multiplication (or div/ mod) operation
   */
  @Override
  public InstructionList visitMultOper(MultOperContext ctx) {
    InstructionList list = defaultResult();
    Register dst1 = accMachine.peekFreeRegister();
    list.add(visitAtom(ctx.first));
    if (!ctx.otherExprs.isEmpty()) {
      Register dst2;
      // for loop used instead of visitChildren so only 2 registers used up
      for (int i = 0; i < ctx.ops.size(); i++) {
        AtomContext otherExpr = ctx.otherExprs.get(i);
        dst2 = accMachine.peekFreeRegister();
        String op = ctx.ops.get(i).getText();
        list.add(visitAtom(otherExpr));
        if (op.equals(Utils.getToken(MUL))){
          Label overflowError = new Label("p_throw_overflow_error");
          list.add(
            accMachine.getInstructionList(SMULL, dst1, dst2))
              .add(accMachine.getInstructionList(CMP, dst2, dst1,
                                                 new Shift(Shifts.ASR, 31)))
              .add(createBranchLinkNotEqual(overflowError));
          Utils.addRuntimeErrorFunctionsToHelpers(
            RuntimeErrorFunctions.overflowError(data), data, helperFunctions);
        } else {
          list.add(divMoves(dst1, dst2, op));
        }
        accMachine.pushFreeRegister(dst2);
      }
    }
    return list;
  }

  /**
   * Gets instructions to get the expression/ variable value
   * Adds the instructions for operations and possible runtime error calls
   */
  @Override
  public InstructionList visitUnaryOper(UnaryOperContext ctx) {
    InstructionList list = defaultResult();
    Register dst = accMachine.peekFreeRegister();
    if (ctx.ident() != null) {
      list.add(visitIdent(ctx.ident()));
    } else if (ctx.expr() != null) {
      list.add(visitExpr(ctx.expr()));
    }
    if (ctx.NOT() != null) {
      list.add(createEOR(dst, dst, new Immediate(1L)));
    } else if (ctx.MINUS() != null) {
      Label throwOverflowError = new Label("p_throw_overflow_error");
      list.add(createRSBS(dst, dst, new Immediate(0L)))
          .add(createBranchLinkVS(throwOverflowError));

      Utils.addRuntimeErrorFunctionsToHelpers(
        RuntimeErrorFunctions.overflowError(data), data, helperFunctions);
    } else if (ctx.LEN() != null) {
      list.add(createLoad(dst, dst, new Immediate(0L)));
    }

    return list;
  }

  /**
   * returns instruction to move the integer to the register
   */
  @Override
  public InstructionList visitInteger(IntegerContext ctx) {
    InstructionList list = defaultResult();
    Immediate op;
    InstructionList loadOrMove = defaultResult();
    Register reg = accMachine.popFreeRegister();
    SignContext sign = ctx.sign();
    String digits = (sign != null && sign.MINUS() != null) ? "-" : "";
    digits += ctx.INTEGER();
    long value = Long.parseLong(digits);
    if (ctx.CHR() != null){
      String chr = "\'" + (char) ((int) value) + "\'";
      op = new Immediate(chr);
      loadOrMove.add(accMachine.getInstructionList(MOV, reg,
                                                   op));
    } else {
      op = new Immediate(value);
      loadOrMove.add(accMachine.getInstructionList(LDR, reg,
          op));
    }

    return list.add(loadOrMove);
  }

  /**
   * Gets instruction to store bool value
   */
  @Override
  public InstructionList visitBool(BoolContext ctx) {
    InstructionList list = defaultResult();
    Operand op;

    Register reg = accMachine.popFreeRegister();
    String boolLitr = ctx.boolLitr().getText();
    long value = boolLitr.equals("false") ? 0 : 1;
    if (ctx.NOT() != null){
      op = new Immediate(value^1);
    } else {
      op = new Immediate(value);
    }

    return list.add(accMachine.getInstructionList(MOV, reg, op));
  }

  /**
   * Gets instruction to store char value
   */
  @Override
  public InstructionList visitCharacter(CharacterContext ctx) {
    InstructionList list = defaultResult();
    Operand op;
    InstructionList loadOrMove;

    Register reg = accMachine.popFreeRegister();
    String chr = ctx.CHARACTER().getText();
    if (ctx.ORD() != null){
      long value = (int) chr.charAt(1);
      op = new Immediate(value);
      loadOrMove = accMachine.getInstructionList(LDR, reg, op);
    } else {
      if (chr.equals("'\\0'")) {
        op = new Immediate(0L);
      } else {
        op = new Immediate(chr);
      }
      loadOrMove = accMachine.getInstructionList(MOV, reg, op);
    }

    return list.add(loadOrMove);
  }

  /**
   * Gets instruction to store string value or length of string (int)
   */
  @Override
  public InstructionList visitString(StringContext ctx) {
    InstructionList list = defaultResult();

    Register reg = accMachine.popFreeRegister();
    String text = ctx.STRING().getText();
    Operand op = data.addConstString(text);
    list.add(accMachine.getInstructionList(LDR, reg, op));

    if (ctx.LEN() != null) {
      list.add(createLoad(reg, reg, new Immediate(0L)));
    }

    return list;
  }

  /**
   * Get instructions to load the value of a variable
   */
  @Override
  public InstructionList visitIdent(IdentContext ctx) {
    InstructionList list = defaultResult();
    Variable variable = getMostRecentBindingForVariable(ctx.getText());

    Immediate offset
        = new Immediate(getAccumulativeOffsetForVariable(ctx.getText()));
    Register reg = accMachine.popFreeRegister();
    Register sp = SP;

      if (Type.isBool(variable.getType()) || Type.isChar(variable.getType())) {
        list.add(accMachine.getInstructionList(LDRSB, reg, sp, offset));
      } else {
        list.add(accMachine.getInstructionList(LDR, reg, sp, offset));
      }

    return list;
  }

  /**
   * Gets instruction to load null into the required register
   */
  @Override
  public InstructionList visitPairLitr(PairLitrContext ctx) {
    InstructionList list = defaultResult();
    Register result = accMachine.popFreeRegister();
    Operand nullOp = new Immediate(0L);
    return list.add(accMachine.getInstructionList(LDR, result, nullOp));
  }

  /**
   * Gets instructions of pair elems
   * Adds instruction to call for check_null_pointer procedure
   * Adds instruction to load elem in correct address (depending on fst or snd)
   */
  @Override
  public InstructionList visitPairElem(PairElemContext ctx) {
    InstructionList list = defaultResult();
    Register result = accMachine.peekFreeRegister();
    list.add(visitChildren(ctx))
        .add(createMove(R0, result))
        .add(createBranchLink(new Label("p_check_null_pointer")));

    Utils.addRuntimeErrorFunctionsToHelpers(
      RuntimeErrorFunctions.checkNullPointer(data), data, helperFunctions);

    Variable variable = getMostRecentBindingForVariable(ctx.ident().getText());
    PairType pairT = (PairType) variable.getType();
    boolean isStoredByte;
    if (ctx.FST() != null) {
      list.add(createLoad(result, result, new Immediate(0L)));
      isStoredByte = Type.isBool(pairT.getFst()) || Type.isChar(pairT.getFst());
    } else {
      list.add(createLoad(result, result, new Immediate(ADDRESS_SIZE)));
      isStoredByte = Type.isBool(pairT.getSnd()) || Type.isChar(pairT.getSnd());
    }

    if (!isAssigning) {
      list.add(getLoadInstructionForElem(isStoredByte, result, result, 0L));
    }

    return list;
  }

  /**
   * Gets instruction to allocate space for array
   * Adds instructions to store each array elem
   * Adds instructions to store length of array
   */
  @Override
  public InstructionList visitArrayLitr(ArrayLitrContext ctx) {
    InstructionList list = defaultResult();
    long bytesToAllocate = ADDRESS_SIZE;
    long typeSize = 0;
    long numberOfElems = ctx.expr().size();

    if (numberOfElems != 0) {
      typeSize = Utils.getTypeSize(ctx);
      bytesToAllocate += typeSize * numberOfElems;
    }

    Label malloc = new Label("malloc");
    Register addressOfArray = accMachine.popFreeRegister();
    list.add(Utils.allocateArrayAddress(bytesToAllocate, malloc, addressOfArray,
                                        accMachine));
    long offset = ADDRESS_SIZE;
    for (ExprContext elem : ctx.expr()) {
      Register result = accMachine.peekFreeRegister();
      storeArrayElem(list, addressOfArray, offset, elem, result);
      offset += typeSize;
      accMachine.pushFreeRegister(result);
    }

    Register lengthOfArray = accMachine.popFreeRegister();
    list.add(Utils.storeLengthOfArray(numberOfElems, addressOfArray,
                                      lengthOfArray));
    accMachine.pushFreeRegister(lengthOfArray);

    return list;
  }

  /**
   * Gets instruction to put the address of the array in the register
   * Adds instructions to check index is out of bound
   * Adds instruction to store element in register
   */
  @Override
  public InstructionList visitArrayElem(ArrayElemContext ctx) {
    InstructionList list = defaultResult();
    Register result = accMachine.peekFreeRegister();

    list.add(visitIdent(ctx.ident()));
    for (ExprContext exprCtx : ctx.expr()) {
      Register helper = accMachine.peekFreeRegister();
      Label checkArrayBounds = new Label("p_check_array_bounds");
      list.add(visitExpr(exprCtx))
          .add(createMove(R0, helper))
          .add(createMove(R1, result))
          .add(createBranchLink(checkArrayBounds))
          .add(createAdd(result, result, new Immediate(ADDRESS_SIZE)));

      boolean isStoredByte
        = Type.isChar(ctx.returnType) || Type.isBool(ctx.returnType);
      list.add(getAddInstruction(isStoredByte, result, helper));
      if (!isAssigning) {
        list.add(getLoadInstructionForElem(isStoredByte, result, result, 0L));
      }

      Utils.addRuntimeErrorFunctionsToHelpers(
        RuntimeErrorFunctions.checkArrayBounds(data), data, helperFunctions);
      accMachine.pushFreeRegister(helper);
    }
    return list;
  }
}
