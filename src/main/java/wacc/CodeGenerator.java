package wacc;

import antlr.WACCParser;
import arm11.*;
import bindings.*;

import java.util.HashSet;
import java.util.List;


import static arm11.HeapFunctions.freePair;

// TODO: document functions
// TODO: Make utils file

public class CodeGenerator extends WACCVisitor<InstructionList> {
  
  private AccumulatorMachine accMachine;

  private static final long ADDRESS_SIZE = 4L;
  private static final long PAIR_SIZE = 2 * ADDRESS_SIZE;
  private DataInstructions data;
  private HashSet<InstructionList> helperFunctions;

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

  private String getToken(int index){
    String tokenName = WACCParser.tokenNames[index];
    assert(tokenName.charAt(0) != '\'');
    return tokenName.substring(1, tokenName.length() - 1);
  }

  @Override
  public InstructionList visitProg(WACCParser.ProgContext ctx) {
    accMachine.resetFreeRegisters();
    String scopeName = Scope.PROG.toString();
    changeWorkingSymbolTableTo(scopeName);
    InstructionList program = defaultResult();
    // visit all the functions and add their instructions
    InstructionList functions = defaultResult();
    for (WACCParser.FuncContext function : ctx.func()) {
      functions.add(visitFunc(function));
    }
    InstructionList main = visitMain(ctx.main());
    program.add(data.getInstructionList());
    // CHECKED
    program.add(InstructionFactory.createText());
    Label mainLabel = new Label(WACCVisitor.Scope.MAIN.toString());
    // CHECKED
    program.add(InstructionFactory.createGlobal(mainLabel));

    program.add(functions).add(main);

    // Add the helper functions
    for (InstructionList instructionList : helperFunctions) {
      program.add(instructionList);
    }

    goUpWorkingSymbolTable();
    return program;
  }

  @Override
  public InstructionList visitMain(WACCParser.MainContext ctx) {
    String scopeName = Scope.MAIN.toString();
    changeWorkingSymbolTableTo(scopeName);
    pushEmptyVariableSet();

    InstructionList list = defaultResult();

    Label label = new Label(Scope.MAIN.toString());
    Register r0 = ARM11Registers.R0;
    Immediate imm = new Immediate((long) 0);
    // CHECKED --ALL
    list.add(InstructionFactory.createLabel(label))
        .add(InstructionFactory.createPush(ARM11Registers.LR))
        .add(allocateSpaceOnStack())
        .add(visitChildren(ctx))
        .add(deallocateSpaceOnStack())
        .add(InstructionFactory.createLoad(r0, imm))
        .add(InstructionFactory.createPop(ARM11Registers.PC))
        .add(InstructionFactory.createLTORG());

    goUpWorkingSymbolTable();
    popCurrentScopeVariableSet();
    return list;
  }

  private InstructionList allocateSpaceOnStack() {
    InstructionList list = defaultResult();
    List<Binding> variables = workingSymbolTable.filterByClass(Variable.class);
    long stackSpaceVarSize = 0;

    for (Binding b : variables) {
      Variable v = (Variable) b;
      if (!v.isParam()) {
        stackSpaceVarSize += v.getType().getSize();
      }
    }

    // TODO: deal with '4095', the max size... of something
    if (stackSpaceVarSize > 0) {
      Immediate imm = new Immediate(stackSpaceVarSize);
      Register sp = ARM11Registers.SP;
      // CHECKED
      list.add(InstructionFactory.createSub(sp, sp, imm));
    }

    String scopeName = workingSymbolTable.getName();
    Binding scopeB = workingSymbolTable.getEnclosingST().get(scopeName);
    NewScope scope = (NewScope) scopeB;
    scope.setStackSpaceSize(stackSpaceVarSize);

    long offset = addOffsetsToVariables(variables);
    addOffsetToParam(variables, offset);

    return list;
  }

  private static void addOffsetToParam(List<Binding> variables, long offset) {
    offset += 4;

    for (Binding b : variables) {
      Variable v = (Variable) b;
      if (v.isParam()) {
        v.setOffset(offset);
        offset += v.getType().getSize();
      }
    }
  }

  private static long addOffsetsToVariables(List<Binding> variables) {
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

  private InstructionList deallocateSpaceOnStackFromReturn() {
    InstructionList list = defaultResult();

    long stackSpaceSize = getAccumulativeStackSizeFromReturn();

    if (stackSpaceSize > 0) {
      Operand imm = new Immediate(stackSpaceSize);
      Register sp = ARM11Registers.SP;
      list.add(InstructionFactory.createAdd(sp, sp, imm));
    }

    return list;
  }

  private long getAccumulativeStackSizeFromReturn() {
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

  // TODO: look at making one deallocate function
  private InstructionList deallocateSpaceOnStack() {
    InstructionList list = defaultResult();
    String scopeName = workingSymbolTable.getName();
    Binding scopeB = workingSymbolTable.getEnclosingST().get(scopeName);
    NewScope scope = (NewScope) scopeB;

    long stackSpaceSize = scope.getStackSpaceSize();

    if (stackSpaceSize > 0) {
      Immediate imm = new Immediate(stackSpaceSize);
      Register sp = ARM11Registers.SP;
      // CHECKED
      list.add(InstructionFactory.createAdd(sp, sp, imm));
    }

    return list;
  }

  @Override
  public InstructionList visitSkipStat(WACCParser.SkipStatContext ctx) {
    return defaultResult();
  }

  @Override
  public InstructionList visitExitStat(WACCParser.ExitStatContext ctx) {
    Register result = accMachine.peekFreeRegister();

    return defaultResult()
        .add(visitExpr(ctx.expr()))
        .add(InstructionFactory.createMove(ARM11Registers.R0, result))
        .add(InstructionFactory.createBranchLink(new Label("exit")));
  }

  @Override
  public InstructionList visitWhileStat(WACCParser.WhileStatContext ctx) {
    ++whileCount;
    InstructionList list = defaultResult();

    String whileScope = Scope.WHILE.toString() + whileCount;
    changeWorkingSymbolTableTo(whileScope);
    pushEmptyVariableSet();

    Label predicate = new Label("predicate_" + whileCount);
    Label body = new Label("while_body_" + whileCount);
    Operand trueOp = new Immediate(1L);

    list.add(InstructionFactory.createBranch(predicate))
        .add(InstructionFactory.createLabel(body))
        .add(allocateSpaceOnStack())
        .add(visitStatList(ctx.statList()))
        .add(deallocateSpaceOnStack())
        .add(InstructionFactory.createLabel(predicate));

    popCurrentScopeVariableSet();
    goUpWorkingSymbolTable();

    Register result = accMachine.peekFreeRegister();
    list.add(visitExpr(ctx.expr()))
        .add(InstructionFactory.createCompare(result, trueOp))
        .add(InstructionFactory.createBranchEqual(body));
    accMachine.pushFreeRegister(result);
    
    return list;
  }

  @Override
  public InstructionList visitIfStat(WACCParser.IfStatContext ctx) {
    ++ifCount;
    InstructionList list = defaultResult();

    Register predicate = accMachine.peekFreeRegister();
    list.add(visitExpr(ctx.expr()));
    list.add(InstructionFactory.createCompare(predicate, new Immediate(0L)));
    // predicate no longer required
    accMachine.pushFreeRegister(predicate);

    Label elseLabel = new Label("else_" + ifCount);
    Label continueLabel = new Label("fi_" + ifCount);

    list.add(InstructionFactory.createBranchEqual(elseLabel));
    String thenScope = Scope.THEN.toString() + ifCount;
    list.add(ifBranchInstructions(list, thenScope, ctx.thenStat));
    list.add(InstructionFactory.createBranch(continueLabel));

    list.add(InstructionFactory.createLabel(elseLabel));
    String elseScope = Scope.ELSE.toString() + ifCount;
    list.add(ifBranchInstructions(list, elseScope, ctx.elseStat));
    list.add(InstructionFactory.createLabel(continueLabel));

    return list;
  }

  private InstructionList ifBranchInstructions(InstructionList list,
                                               String thenScope,
                                               WACCParser.StatListContext ctx) {
    changeWorkingSymbolTableTo(thenScope);
    pushEmptyVariableSet();
    list.add(allocateSpaceOnStack())
        .add(visitStatList(ctx))
        .add(deallocateSpaceOnStack());
    popCurrentScopeVariableSet();
    goUpWorkingSymbolTable();
    return list;
  }

  @Override
  public InstructionList visitInitStat(WACCParser.InitStatContext ctx) {
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
  public InstructionList visitAssignStat(WACCParser.AssignStatContext ctx) {
    if (ctx.assignLHS().ident() != null){
      String varName = ctx.assignLHS().ident().getText();
      Variable var = getMostRecentBindingForVariable(varName);
      long varOffset = getAccumulativeOffsetForVariable(varName);
      return storeToOffset(varOffset, var.getType(), ctx.assignRHS());
    }
    return visitChildren(ctx);
  }

  private InstructionList storeToOffset(long varOffset,
                                        Type varType,
                                        WACCParser.AssignRHSContext assignRHS) {
    InstructionList list = defaultResult();

    Register reg = accMachine.peekFreeRegister();

    list.add(visitAssignRHS(assignRHS));

    InstructionList storeInstr;
    Register sp  = ARM11Registers.SP;
    Immediate offset = new Immediate(varOffset);
    if (Type.isBool(varType) || Type.isChar(varType)) {
      storeInstr = accMachine.getInstructionList(InstructionType.STRB,
                                                 reg, sp, offset);
    } else {
      storeInstr = accMachine.getInstructionList(InstructionType.STR,
                                                 reg, sp, offset);
    }

    list.add(storeInstr);
    accMachine.pushFreeRegister(reg);
    return list;
  }

  @Override
  public InstructionList visitReturnStat(WACCParser.ReturnStatContext ctx) {
    InstructionList list = defaultResult();
    Register resultReg = accMachine.peekFreeRegister();
    list.add(visitExpr(ctx.expr()))
        .add(InstructionFactory.createMove(ARM11Registers.R0, resultReg))
        .add(deallocateSpaceOnStackFromReturn())
        .add(InstructionFactory.createPop(ARM11Registers.PC));
    accMachine.pushFreeRegister(resultReg);

    return list;
  }

  @Override
  public InstructionList visitPrintStat(WACCParser.PrintStatContext ctx) {
    InstructionList list = defaultResult();
    Label printLabel;
    Type returnType = ctx.expr().returnType;

    if (Type.isString(returnType)) {
      printLabel = new Label("p_print_string");
      addPrintFunctionToHelpers(PrintFunctions.printString(data));
    } else if (Type.isInt(returnType)) {
      printLabel = new Label("p_print_int");
      addPrintFunctionToHelpers(PrintFunctions.printInt(data));
    } else if (Type.isChar(returnType)){
      printLabel = new Label("putchar");
    } else if (Type.isBool(returnType)) {
      printLabel = new Label("p_print_bool");
      addPrintFunctionToHelpers(PrintFunctions.printBool(data));
    } else {
      printLabel = new Label("p_print_reference");
      addPrintFunctionToHelpers(PrintFunctions.printReference(data));
    }

    Register result = accMachine.peekFreeRegister();
    list.add(printExpression(ctx.expr(), printLabel, result));
    accMachine.pushFreeRegister(result);
    if (ctx.PRINTLN() != null) {
      printNewLine(list);
    }

    return list;
  }

  private InstructionList printExpression(WACCParser.ExprContext ctx,
                                          Label printLabel,
                                          Register result) {
    InstructionList list = defaultResult();
    list.add(visitExpr(ctx))
        .add(InstructionFactory.createMove(ARM11Registers.R0, result))
        .add(InstructionFactory.createBranchLink(printLabel));
    return list;
  }

  private void addPrintFunctionToHelpers(InstructionList printFunction) {
    if (printFunction != null) {
      helperFunctions.add(printFunction);
    }
  }

  @Override
  public InstructionList visitLogicalOper(WACCParser.LogicalOperContext ctx) {
    InstructionList list = defaultResult();
    Register dst1 = accMachine.peekFreeRegister();
    list.add(visitComparisonOper(ctx.first));
    if (!ctx.otherExprs.isEmpty()) {
      Register dst2;
      // for loop used instead of visitChildren so only 2 registers used up
      InstructionList logicalInstr;
      for (int i = 0; i < ctx.ops.size(); i++) {
        WACCParser.ComparisonOperContext otherExpr = ctx.otherExprs.get(i);
        dst2 = accMachine.peekFreeRegister();

        list.add(visitComparisonOper(otherExpr));

        if (ctx.ops.get(i).getText().equals(getToken(WACCParser.AND))){
          logicalInstr = accMachine.getInstructionList(InstructionType.AND,
                                                       dst1, dst1, dst2);
        } else {
          logicalInstr = accMachine.getInstructionList(InstructionType.ORR,
                                                       dst1, dst1, dst2);
        }

        list.add(logicalInstr);
        accMachine.pushFreeRegister(dst2);
      }
    }

    return list;
  }

  @Override
  public InstructionList visitOrderingOper(WACCParser.OrderingOperContext ctx) {
    InstructionList list = defaultResult();

    Register dst1 = accMachine.peekFreeRegister();
    list.add(visitAddOper(ctx.first));
    if (ctx.second != null) {
      Register dst2 = accMachine.peekFreeRegister();
      Operand trueOp = new Immediate((long) 1);
      Operand falseOp = new Immediate((long) 0);
      list.add(visitAddOper(ctx.second))
          .add(accMachine.getInstructionList(InstructionType.CMP, dst1, dst2));
      if (ctx.GT() != null){
        list.add(InstructionFactory.createMovGt(dst1, trueOp))
            .add(InstructionFactory.createMovLe(dst1, falseOp));
      } else if (ctx.GE() != null) {
        list.add(InstructionFactory.createMovGe(dst1, trueOp))
            .add(InstructionFactory.createMovLt(dst1, falseOp));
      } else if (ctx.LT() != null) {
        list.add(InstructionFactory.createMovLt(dst1, trueOp))
            .add(InstructionFactory.createMovGe(dst1, falseOp));
      } else if (ctx.LE() != null) {
        list.add(InstructionFactory.createMovLe(dst1, trueOp))
            .add(InstructionFactory.createMovGt(dst1, falseOp));
      }

      accMachine.pushFreeRegister(dst2);
    }

    return list;
  }

  private void printNewLine(InstructionList list) {
    Label printLabel = new Label("p_print_ln");
    list.add(InstructionFactory.createBranchLink(printLabel));
    InstructionList printHelperFunction = PrintFunctions.printLn(data);
    helperFunctions.add(printHelperFunction);
  }

  @Override
  public InstructionList visitEqualityOper(WACCParser.EqualityOperContext ctx) {
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
          .add(accMachine.getInstructionList(InstructionType.CMP, dst1, dst2))
          .add(InstructionFactory.createMovEq(dst1, trueOp))
          .add(InstructionFactory.createMovNe(dst1, falseOp));
      accMachine.pushFreeRegister(dst2);
    }

    return list;
  }

  @Override
  public InstructionList visitAddOper(WACCParser.AddOperContext ctx) {
    InstructionList list = defaultResult();
    Register dst1 = accMachine.peekFreeRegister();
    list.add(visitMultOper(ctx.first));
    if (!ctx.otherExprs.isEmpty()) {
      Register dst2;
      // for loop used instead of visitChildren so only 2 registers used up
      for (int i = 0; i < ctx.ops.size(); i++) {
        InstructionList arithmeticInstr = defaultResult();
        WACCParser.MultOperContext otherExpr = ctx.otherExprs.get(i);
        dst2 = accMachine.peekFreeRegister();
        String op = ctx.ops.get(i).getText();

        list.add(visitMultOper(otherExpr));
        if (op.equals(getToken(WACCParser.PLUS))){
          arithmeticInstr.add(
            accMachine.getInstructionList(InstructionType.ADDS,
                                          dst1, dst1, dst2));
        } else {
          arithmeticInstr.add(
            accMachine.getInstructionList(InstructionType.SUBS,
                                          dst1, dst1, dst2));
        }
        list.add(arithmeticInstr);
        accMachine.pushFreeRegister(dst2);
      }
    }

    return list;
  }

  @Override
  public InstructionList visitMultOper(WACCParser.MultOperContext ctx) {
    InstructionList list = defaultResult();
    Register dst1 = accMachine.peekFreeRegister();
    list.add(visitAtom(ctx.first));
    if (!ctx.otherExprs.isEmpty()) {
      Register dst2;
      // for loop used instead of visitChildren so only 2 registers used up
      for (int i = 0; i < ctx.ops.size(); i++) {
        InstructionList arithmeticInstr = defaultResult();
        WACCParser.AtomContext otherExpr = ctx.otherExprs.get(i);
        dst2 = accMachine.peekFreeRegister();
        String op = ctx.ops.get(i).getText();

        list.add(visitAtom(otherExpr));
        if (op.equals(getToken(WACCParser.MUL))){
          arithmeticInstr.add(
            accMachine.getInstructionList(InstructionType.SMULL, dst1, dst2));
        } else {
          arithmeticInstr.add(divMoves(dst1, dst2, op));
        }
        list.add(arithmeticInstr);
        accMachine.pushFreeRegister(dst2);
      }
    }

    return list;
  }

  private InstructionList divMoves(Register dst1, Register dst2, String op) {
    InstructionList list =  defaultResult();
    list.add(accMachine.getInstructionList(InstructionType.DIVMOD, dst1, dst2));
    if (op.equals(getToken(WACCParser.DIV))){
      list.add(InstructionFactory.createDiv())
          .add(InstructionFactory.createMove(dst1, ARM11Registers.R0));
    } else {
      list.add(InstructionFactory.createMod())
          .add(InstructionFactory.createMove(dst1, ARM11Registers.R1));
    }
    return list;
  }

  @Override
  public InstructionList visitUnaryOper(WACCParser.UnaryOperContext ctx) {
    InstructionList list = defaultResult();
    Register dst = accMachine.peekFreeRegister();
    if (ctx.ident() != null) {
      list.add(visitIdent(ctx.ident()));
    } else if (ctx.expr() != null) {
      list.add(visitExpr(ctx.expr()));
    }
    // CHECKED --ALL
    if (ctx.NOT() != null) {
      list.add(InstructionFactory.createEOR(dst, dst, new Immediate(1L)));
    } else if (ctx.MINUS() != null) {
      list.add(InstructionFactory.createRSBS(dst, dst, new Immediate(0L)));
    } else if (ctx.LEN() != null) {
      list.add(InstructionFactory.createLoad(dst, dst, new Immediate(0L)));
    }

    return list;
  }

  @Override
  public InstructionList visitInteger(WACCParser.IntegerContext ctx) {
    InstructionList list = defaultResult();

    Immediate op;
    InstructionList loadOrMove = defaultResult();
    Register reg = accMachine.popFreeRegister();
    String digits = ctx.INTEGER().getText();
    long value = Long.parseLong(digits);

    if (ctx.CHR() != null){
      String chr = "\'" + (char) ((int) value) + "\'";
      op = new Immediate(chr);
      loadOrMove.add(accMachine.getInstructionList(InstructionType.MOV,
                                                   reg,
                                                   op));
    } else {
      op = new Immediate(value);
      loadOrMove.add(accMachine.getInstructionList(InstructionType.LDR,
                                                   reg,
                                                   op));
    }

    return list.add(loadOrMove);
  }

  @Override
  public InstructionList visitCall(WACCParser.CallContext ctx) {
    InstructionList list = defaultResult();
    String functionName = ScopeType.FUNCTION_SCOPE + ctx.funcName.getText();
    Label functionLabel = new Label(functionName);

    if (ctx.argList() != null) {
      list.add(visitArgList(ctx.argList()));
    }
    // CHECKED --ALL
    list.add(InstructionFactory.createBranchLink(functionLabel));
    Register result = accMachine.popFreeRegister();
    if (ctx.argList() != null) {
      Operand size = new Immediate(totalListSize(ctx.argList().expr()));
      list.add(InstructionFactory.createAdd(ARM11Registers.SP,
          ARM11Registers.SP, size));
    }
    // TODO: check reasoning
    list.add(accMachine.getInstructionList(InstructionType.MOV,
                                           result,
                                           ARM11Registers.R0));

    return list;
  }

  private Long totalListSize(List<? extends WACCParser.ExprContext> exprs) {
    Long totalSize = 0L;
    for (WACCParser.ExprContext exprCtx : exprs) {
      totalSize += exprCtx.returnType.getSize();
    }
    return totalSize;
  }

  @Override
  public InstructionList visitArgList(WACCParser.ArgListContext ctx) {
    InstructionList list = defaultResult();

    for (int i = ctx.expr().size() - 1; i >= 0; i--) {
      WACCParser.ExprContext exprCtx = ctx.expr(i);
      Register result = accMachine.peekFreeRegister();
      Long varSize = (long) -exprCtx.returnType.getSize();
      Operand size = new Immediate(varSize);
      list.add(visitExpr(exprCtx));
      if (varSize == -ADDRESS_SIZE) {
        list.add(InstructionFactory.createStore(result,
                                                ARM11Registers.SP,
                                                size));
      } else {
        list.add(InstructionFactory.createStoreByte(result,
                                                    ARM11Registers.SP,
                                                    size));
      }
      accMachine.pushFreeRegister(result);
    }
    return list;
  }

  @Override
  public InstructionList visitBool(WACCParser.BoolContext ctx) {
    InstructionList list = defaultResult();

    Operand op;
    Register reg = accMachine.popFreeRegister();

    String boolLitr = ctx.boolLitr().getText();
    long value = boolLitr.equals("false") ? 0 : 1;

    if (ctx.NOT() != null){
      // ^ is XOR
      op = new Immediate(value^1);
    } else {
      op = new Immediate(value);
    }
    return list.add(accMachine.getInstructionList(InstructionType.MOV,
                                                  reg,
                                                  op));
  }

  @Override
  public InstructionList visitCharacter(WACCParser.CharacterContext ctx) {
    InstructionList list = defaultResult();

    Operand op;
    InstructionList loadOrMove;
    Register reg = accMachine.popFreeRegister();
    String chr = ctx.CHARACTER().getText();

    if (ctx.ORD() != null){
      long value = (int) chr.charAt(1);
      op = new Immediate(value);
      loadOrMove = accMachine.getInstructionList(InstructionType.LDR, reg, op);
    } else {
      op = new Immediate(chr);
      loadOrMove = accMachine.getInstructionList(InstructionType.MOV, reg, op);
    }

    return list.add(loadOrMove);
  }

  @Override
  public InstructionList visitString(WACCParser.StringContext ctx) {
    InstructionList list = defaultResult();

    Operand op;
    Register reg = accMachine.popFreeRegister();
    String text = ctx.STRING().getText();
    op = data.addConstString(text);
    list.add(accMachine.getInstructionList(InstructionType.LDR, reg, op));

    if (ctx.LEN() != null) {
      // TODO: allow for 0 default offset
      list.add(InstructionFactory.createLoad(reg, reg, new Immediate(0L)));
    }
    return list;
  }

  @Override
  public InstructionList visitIdent(WACCParser.IdentContext ctx) {
    InstructionList list = defaultResult();
    Variable variable = getMostRecentBindingForVariable(ctx.getText());
    
    Immediate offset 
        = new Immediate(getAccumulativeOffsetForVariable(ctx.getText()));
    Register reg = accMachine.popFreeRegister();
    Register sp = ARM11Registers.SP;

      if (Type.isBool(variable.getType()) || Type.isChar(variable.getType())) {
        // TODO: implement this
        list.add(accMachine.getInstructionList(InstructionType.LDRSB,
                                               reg,
                                               sp,
                                               offset));
      } else {
        list.add(accMachine.getInstructionList(
            InstructionType.LDR, reg, sp, offset));
      }

    return list;
  }


  @Override
  public InstructionList visitFreeStat(WACCParser.FreeStatContext ctx) {
    InstructionList list = defaultResult();

    Register result = accMachine.peekFreeRegister();
    list.add(visitExpr(ctx.expr()))
        .add(InstructionFactory.createMove(ARM11Registers.R0, result))
        .add(InstructionFactory.createBranchLink(new Label("p_free_pair")));

    helperFunctions.add(freePair(data));
    return list;
  }

  @Override
  public InstructionList visitNewPair(WACCParser.NewPairContext ctx) {
    InstructionList list = defaultResult();
    Label malloc = new Label("malloc");
    Register result = accMachine.popFreeRegister();
    Immediate sizeOfObject = new Immediate(PAIR_SIZE);
    list.add(allocateSpaceForNewPair(malloc, sizeOfObject));
    list.add(accMachine.getInstructionList(InstructionType.MOV,
                                           result,
                                           ARM11Registers.R0));

    Long accSize = 0L;
    for (WACCParser.ExprContext exprCtx : ctx.expr()) {
      Long size = (long) exprCtx.returnType.getSize();
      Register next = accMachine.peekFreeRegister();
      list.add(allocateSpaceForPairElem(malloc, exprCtx, size, next));
      accMachine.pushFreeRegister(next);
      list.add(InstructionFactory.createStore(ARM11Registers.R0,
                                              result,
                                              new Immediate(accSize)));
      accSize += size;
    }

    return list;
  }

  private InstructionList allocateSpaceForPairElem(Label malloc,
                                                   WACCParser.ExprContext
                                                     exprCtx,
                                                   Long size,
                                                   Register next) {
    InstructionList list = defaultResult();
    list.add(visitExpr(exprCtx))
        .add(InstructionFactory.createLoad(ARM11Registers.R0,
                                           new Immediate(size)))
        .add(InstructionFactory.createBranchLink(malloc))
        .add(InstructionFactory.createStore(next,
                                            ARM11Registers.R0,
                                            new Immediate(0L)));
    return list;
  }

  private InstructionList allocateSpaceForNewPair(Label malloc,
                                                  Immediate sizeOfObject) {
    InstructionList list = defaultResult();
    list.add(InstructionFactory.createLoad(ARM11Registers.R0, sizeOfObject))
        .add(InstructionFactory.createBranchLink(malloc));
    return list;
  }

  @Override
  public InstructionList visitBeginStat(WACCParser.BeginStatContext ctx) {
    ++beginCount;
    InstructionList list = defaultResult();

    String beginScope = Scope.BEGIN.toString() + beginCount;
    changeWorkingSymbolTableTo(beginScope);
    pushEmptyVariableSet();

    list.add(allocateSpaceOnStack())
            .add(visitStatList(ctx.statList()))
            .add(deallocateSpaceOnStack());

    popCurrentScopeVariableSet();
    goUpWorkingSymbolTable();

    return list;
  }

  @Override
  public InstructionList visitReadStat(WACCParser.ReadStatContext ctx) {
    InstructionList list = defaultResult();

    Register reg = accMachine.popFreeRegister();
    Register dst = ARM11Registers.R0;
    Long offset = 0L;

    // TODO:
    String name;
    if (ctx.assignLHS().ident() != null) {
      name = ctx.assignLHS().ident().getText();
      offset = getAccumulativeOffsetForVariable(name);
    } else if (ctx.assignLHS().arrayElem() != null) {

    } else {

    }

    Immediate imm = new Immediate(offset);
    InstructionList printHelperFunction;
    Label readLabel;

    if (Type.isInt((Type) ctx.assignLHS().returnType)) {
      readLabel = new Label("p_read_int");
      printHelperFunction = ReadFunctions.readInt(data);
    } else {
      readLabel = new Label("p_read_char");
      printHelperFunction = ReadFunctions.readChar(data);
    }

    // CHECKED
    list.add(accMachine.getInstructionList(InstructionType.ADD,
                                           reg,
                                           ARM11Registers.SP,
                                           imm))
        .add(InstructionFactory.createMove(dst, reg))
        .add(InstructionFactory.createBranchLink(readLabel));
    accMachine.pushFreeRegister(reg);
    helperFunctions.add(printHelperFunction);

    return list;
  }

  @Override
  public InstructionList visitPairLitr(WACCParser.PairLitrContext ctx) {
    Register result = accMachine.popFreeRegister();
    Operand nullOp = new Immediate(0L);
    return
      defaultResult().add(accMachine.getInstructionList(InstructionType.LDR,
                                                        result,
                                                        nullOp));
  }

  @Override
  public InstructionList visitPairElem(WACCParser.PairElemContext ctx) {
    InstructionList list = defaultResult();
    Register result = accMachine.peekFreeRegister();
    list.add(visitChildren(ctx));

    if (ctx.FST() != null) {
      list.add(InstructionFactory.createLoad(result, result,
                                              new Immediate(0L)));
    } else {
      list.add(InstructionFactory.createLoad(result, result,
                                              new Immediate(ADDRESS_SIZE)));
    }

    list.add(InstructionFactory.createLoad(result, result, new Immediate(0L)));

    return list;
  }

  @Override
  public InstructionList visitArrayLitr(WACCParser.ArrayLitrContext ctx) {
    InstructionList list = defaultResult();
    long bytesToAllocate = ADDRESS_SIZE;
    long typeSize = 0;
    long numberOfElems = ctx.expr().size();

    if (numberOfElems != 0) {
      typeSize = getTypeSize(ctx);
      bytesToAllocate += typeSize * numberOfElems;
    }

    Label malloc = new Label("malloc");
    Register addressOfArray = accMachine.popFreeRegister();
    list.add(allocateArrayAddress(bytesToAllocate, malloc, addressOfArray));

    long offset = ADDRESS_SIZE;
    for (WACCParser.ExprContext elem : ctx.expr()) {
      Register result = accMachine.peekFreeRegister();
      storeArrayElem(list, addressOfArray, offset, elem, result);
      offset += typeSize;
      accMachine.pushFreeRegister(result);
    }
    
    Register lengthOfArray = accMachine.peekFreeRegister();
    list.add(storeLengthOfArray(numberOfElems, addressOfArray, lengthOfArray));
    accMachine.pushFreeRegister(lengthOfArray);

    return list;
  }

  private InstructionList storeLengthOfArray(long numberOfElems,
                                             Register addressOfArray,
                                             Register lengthOfArray) {

    InstructionList list = defaultResult();
    list.add(InstructionFactory.createLoad(lengthOfArray,
                                           new Immediate(numberOfElems)))
        .add(InstructionFactory.createStore(lengthOfArray,
                                            addressOfArray,
                                            new Immediate(0L)));
    return list;
  }

  private InstructionList allocateArrayAddress(long bytesToAllocate,
                                               Label malloc,
                                               Register addressOfArray) {
    InstructionList list = defaultResult();
    list.add(InstructionFactory.createLoad(ARM11Registers.R0,
                                           new Immediate(bytesToAllocate)))
        .add(InstructionFactory.createBranchLink(malloc))
        .add(accMachine.getInstructionList(InstructionType.MOV,
                                           addressOfArray,
                                           ARM11Registers.R0));
    return list;
  }

  private void storeArrayElem(InstructionList list,
                              Register addressOfArray,
                              long offset,
                              WACCParser.ExprContext elem,
                              Register result) {
    Immediate imm = new Immediate(offset);
    list.add(visitExpr(elem))
        .add(InstructionFactory.createStore(result, addressOfArray, imm));
  }

  private long getTypeSize(WACCParser.ArrayLitrContext ctx) {
    Type returnType = ctx.expr().get(0).returnType;
    return returnType.getSize();
  }

  @Override
  public InstructionList visitParam(WACCParser.ParamContext ctx) {
    String name = ctx.name.getText();
    addVariableToCurrentScope(name);
    // set the variable as a param
    Variable var = (Variable) workingSymbolTable.lookupAll(name);
    var.setAsParam();
    return null;
  }

  @Override
  public InstructionList visitFunc(WACCParser.FuncContext ctx) {
    InstructionList list = defaultResult();
    changeWorkingSymbolTableTo(ScopeType.FUNCTION_SCOPE
            + ctx.funcName.getText());
    pushEmptyVariableSet();

    Label functionLabel = new Label(ScopeType.FUNCTION_SCOPE
            + ctx.funcName.getText());

    if (ctx.paramList() != null) {
      visitParamList(ctx.paramList());
    }

    // CHECKED --ALL

    list.add(InstructionFactory.createLabel(functionLabel));
    list.add(InstructionFactory.createPush(ARM11Registers.LR))
            .add(allocateSpaceOnStack())
            .add(visitStatList(ctx.statList()))
            .add(InstructionFactory.createPop(ARM11Registers.PC))
            .add(InstructionFactory.createLTORG());
    popCurrentScopeVariableSet();
    goUpWorkingSymbolTable();

    return list;
  }
}
