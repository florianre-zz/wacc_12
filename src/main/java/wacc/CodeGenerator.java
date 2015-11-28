package wacc;

import antlr.WACCParser;
import arm11.*;
import bindings.Binding;
import bindings.Type;
import bindings.Variable;
import bindings.NewScope;
import org.antlr.v4.runtime.misc.NotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Stack;

// TODO: Do we need top for labels?

public class CodeGenerator extends WACCVisitor<InstructionList> {

  private DataInstructions data;
  private HashSet<InstructionList> helperFunctions;
  private Stack<Register> freeRegisters;

  public CodeGenerator(SymbolTable top) {
    super(top);
    this.data = new DataInstructions();
    this.helperFunctions = new HashSet<>();
    this.freeRegisters = new Stack<>();
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
    // TODO: Remove
    System.err.println(top);
    resetFreeRegisters();
    String scopeName = Scope.PROG.toString();
    changeWorkingSymbolTableTo(scopeName);
    InstructionList program = defaultResult();
    InstructionList main = visitMain(ctx.main());
    program.add(data.getInstructionList());
    program.add(InstructionFactory.createText());
    Label mainLabel = new Label(WACCVisitor.Scope.MAIN.toString());
    program.add(InstructionFactory.createGlobal(mainLabel));
    program.add(main);

    // Add the helper functions
    for (InstructionList instructionList : helperFunctions) {
      program.add(instructionList);
    }

    goUpWorkingSymbolTable();
    return program;
  }

  private void resetFreeRegisters() {
    freeRegisters.clear();
    freeRegisters.push(ARM11Registers.R12);
    freeRegisters.push(ARM11Registers.R11);
    freeRegisters.push(ARM11Registers.R10);
    freeRegisters.push(ARM11Registers.R9);
    freeRegisters.push(ARM11Registers.R8);
    freeRegisters.push(ARM11Registers.R7);
    freeRegisters.push(ARM11Registers.R6);
    freeRegisters.push(ARM11Registers.R5);
    freeRegisters.push(ARM11Registers.R4);
  }

  @Override
  public InstructionList visitMain(WACCParser.MainContext ctx) {
    String scopeName = Scope.MAIN.toString();
    changeWorkingSymbolTableTo(scopeName);
    pushEmptyVariableSet();

    InstructionList list = defaultResult();

    Label label = new Label(Scope.MAIN.toString());
    Register r0 = ARM11Registers.R0;
    Operand imm = new Immediate((long) 0);

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
    // TODO: account for newScopes within...
    InstructionList list = defaultResult();
    List<Binding> variables = workingSymbolTable.filterByClass(Variable.class);
    long stackSpaceSize = 0;
    for (Binding b : variables) {
      Variable v = (Variable) b;
      stackSpaceSize += v.getType().getSize();
    }
    // TODO: deal with '4095', the max size... of something
    if (stackSpaceSize > 0) {
      Operand imm = new Immediate(stackSpaceSize);
      Register sp = ARM11Registers.SP;
      list.add(InstructionFactory.createSub(sp, sp, imm));
    }
    String scopeName = workingSymbolTable.getName();
    Binding scopeB = workingSymbolTable.getEnclosingST().get(scopeName);
    NewScope scope = (NewScope) scopeB;
    scope.setStackSpaceSize(stackSpaceSize);

    for (Binding b : variables) {
      Variable v = (Variable) b;
      System.err.println(b);
      stackSpaceSize -= v.getType().getSize();
      long offset = stackSpaceSize;
      v.setOffset(offset);
    }

    return list;
  }

  private InstructionList deallocateSpaceOnStack() {
    InstructionList list = defaultResult();
    List<Binding> variables = workingSymbolTable.filterByClass(Variable.class);
    long stackSpaceSize = 0;
    for (Binding b : variables) {
      Variable v = (Variable) b;
      stackSpaceSize += v.getType().getSize();
    }

    if (stackSpaceSize > 0) {
      Operand imm = new Immediate(stackSpaceSize);
      Register sp = ARM11Registers.SP;
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

    InstructionList list = defaultResult();

    Register r0 = ARM11Registers.R0;
    Operand value = new Immediate(Long.parseLong(ctx.expr().getText()));
    Label label = new Label("exit");

    list.add(InstructionFactory.createLoad(r0, value));
    list.add(InstructionFactory.createBranchLink(label));

    return list;
  }

  @Override
  public InstructionList visitIfStat(@NotNull WACCParser.IfStatContext ctx) {

    ++ifCount;
    InstructionList list = defaultResult();

    Register predicate = freeRegisters.peek();
    list.add(visitExpr(ctx.expr()));
    list.add(InstructionFactory.createCompare(predicate,
                                              new Immediate((long)0)));
    // No longer required
    freeRegisters.push(predicate);
    Label elseLabel = new Label("else_" + ifCount);
    list.add(InstructionFactory.createBranchEqual(elseLabel));

    String thenScope = Scope.THEN.toString() + ifCount;
    changeWorkingSymbolTableTo(thenScope);

    pushEmptyVariableSet();
    list.add(allocateSpaceOnStack())
      .add(visitStatList(ctx.thenStat))
      .add(deallocateSpaceOnStack());
    popCurrentScopeVariableSet();

    goUpWorkingSymbolTable();

    Label continueLabel = new Label("fi_" + ifCount);
    list.add(InstructionFactory.createBranch(continueLabel));

    list.add(InstructionFactory.createLabel(elseLabel));

    String elseScope = Scope.ELSE.toString() + ifCount;
    changeWorkingSymbolTableTo(elseScope);

    pushEmptyVariableSet();
    list.add(allocateSpaceOnStack())
      .add(visitStatList(ctx.elseStat))
      .add(deallocateSpaceOnStack());
    popCurrentScopeVariableSet();
    list.add(InstructionFactory.createLabel(continueLabel));

    goUpWorkingSymbolTable();

    return list;
  }

  @Override
  public InstructionList visitInitStat(WACCParser.InitStatContext ctx) {

    String varName = ctx.ident().getText();
    Variable var = (Variable) workingSymbolTable.get(varName);
    long varOffset = var.getOffset();

    InstructionList list = storeToOffset(varOffset, var.getType(), ctx.assignRHS());
    addVariableToCurrentScope(varName);
    return list;
  }

  @Override
  public InstructionList visitAssignStat(WACCParser.AssignStatContext ctx) {
    if (ctx.assignLHS().ident() != null){
      String varName = ctx.assignLHS().ident().getText();
      Variable var = getMostRecentBindingForVariable(varName);
      long varOffset = var.getOffset();
      return storeToOffset(varOffset, var.getType(), ctx.assignRHS());
    }
    return visitChildren(ctx);
  }

  private InstructionList storeToOffset(long varOffset,
                                        Type varType,
                                          WACCParser.AssignRHSContext assignRHS) {
    InstructionList list = defaultResult();
    // TODO: move the pop to visitAssignRHS
    Register reg = freeRegisters.peek();

    list.add(visitAssignRHS(assignRHS));

    Instruction storeInstr;
    Register sp  = ARM11Registers.SP;
    Operand offset = new Immediate(varOffset);
    if (Type.isBool(varType) || Type.isChar(varType)){
      storeInstr = InstructionFactory.createStoreBool(reg, sp, offset);
    } else {
      storeInstr = InstructionFactory.createStore(reg, sp, offset);
    }

    list.add(storeInstr);
    freeRegisters.push(reg);
    return list;
  }

  @Override
  public InstructionList visitPrintStat(WACCParser.PrintStatContext ctx) {
    InstructionList list = defaultResult();
    Label printLabel;
    InstructionList printFunction = null;
    Type returnType = (Type) ctx.expr().returnType;

    if (Type.isString(returnType)) {
      printFunction = PrintFunctions.printString(data);
      printLabel = new Label("p_print_string");
      data.addConstString(ctx.expr().getText());
    } else if (Type.isInt((Type) ctx.expr().returnType)) {
      printFunction = PrintFunctions.printInt(data);
      printLabel = new Label("p_print_int");
    } else if (Type.isChar((Type) ctx.expr().returnType)){
      printLabel = new Label("putchar");
    } else if (Type.isBool((Type) ctx.expr().returnType)) {
      printFunction = PrintFunctions.printBool(data);
      printLabel = new Label("p_print_bool");
    } else {
      printFunction = PrintFunctions.printReference(data);
      printLabel = new Label("p_print_reference");
    }

    Register result = freeRegisters.peek();
    list.add(visitExpr(ctx.expr()))
       .add(InstructionFactory.createMov(ARM11Registers.R0, result))
       .add(InstructionFactory.createBranchLink(printLabel));
    addPrintFunctionToHelpers(printFunction);
    if (ctx.PRINTLN() != null) {
      printNewLine(list);
    }
    freeRegisters.push(result);

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
    Register dst1 = freeRegisters.peek();
    list.add(visitComparisonOper(ctx.first));
    if (!ctx.otherExprs.isEmpty()) {
      Register dst2;
      // for loop used instead of visitChildren so only 2 registers used up
      Instruction logicalInstr;
      for (int i = 0; i < ctx.ops.size(); i++) {
        WACCParser.ComparisonOperContext otherExpr = ctx.otherExprs.get(i);
        dst2 = freeRegisters.peek();

        if (ctx.ops.get(i).getText().equals(getToken(WACCParser.AND))){
          logicalInstr = InstructionFactory.createAnd(dst1, dst1, dst2);
        } else {
          logicalInstr = InstructionFactory.createOrr(dst1, dst1, dst2);
        }

        list.add(visitComparisonOper(otherExpr))
            .add(logicalInstr);
        freeRegisters.push(dst2);
      }
    }

    return list;
  }

  @Override
  public InstructionList visitOrderingOper(WACCParser.OrderingOperContext ctx) {
    InstructionList list = defaultResult();

    Register dst1 = freeRegisters.peek();
    list.add(visitAddOper(ctx.first));
    if (ctx.second != null) {
      Register dst2 = freeRegisters.peek();
      Operand trueOp = new Immediate((long) 1);
      Operand falseOp = new Immediate((long) 0);
      list.add(visitAddOper(ctx.second))
          .add(InstructionFactory.createCompare(dst1, dst2));
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

      freeRegisters.push(dst2);
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
    Register dst1 = freeRegisters.peek();
    list.add(visitAddOper(ctx.first));
    if (ctx.second != null) {
      Register dst2 = freeRegisters.peek();

      long trueLong = (ctx.EQ() != null) ? 1 : 0;
      long falseLong = (ctx.EQ() != null) ? 0 : 1;
      Operand trueOp = new Immediate(trueLong);
      Operand falseOp = new Immediate(falseLong);
      list.add(visitAddOper(ctx.second))
          .add(InstructionFactory.createCompare(dst1, dst2))
          .add(InstructionFactory.createMovEq(dst1, trueOp))
          .add(InstructionFactory.createMovNe(dst1, falseOp));
      freeRegisters.push(dst2);
    }

    return list;
  }

  @Override
  public InstructionList visitAddOper(WACCParser.AddOperContext ctx) {
    InstructionList list = defaultResult();
    Register dst1 = freeRegisters.peek();
    list.add(visitMultOper(ctx.first));
    if (!ctx.otherExprs.isEmpty()) {
      Register dst2;
      // for loop used instead of visitChildren so only 2 registers used up
      for (int i = 0; i < ctx.ops.size(); i++) {
        InstructionList arithmeticInstr = defaultResult();
        WACCParser.MultOperContext otherExpr = ctx.otherExprs.get(i);
        dst2 = freeRegisters.peek();
        String op = ctx.ops.get(i).getText();

        if (op.equals(getToken(WACCParser.PLUS))){
          arithmeticInstr.add(InstructionFactory.createAdds(dst1, dst1, dst2));
        } else {
          arithmeticInstr.add(InstructionFactory.createSubs(dst1, dst1, dst2));
        }

        list.add(visitMultOper(otherExpr))
            .add(arithmeticInstr);
        freeRegisters.push(dst2);
      }
    }

    return list;
  }

  @Override
  public InstructionList visitMultOper(WACCParser.MultOperContext ctx) {
    InstructionList list = defaultResult();
    Register dst1 = freeRegisters.peek();
    list.add(visitAtom(ctx.first));
    if (!ctx.otherExprs.isEmpty()) {
      Register dst2;
      // for loop used instead of visitChildren so only 2 registers used up
      for (int i = 0; i < ctx.ops.size(); i++) {
        InstructionList arithmeticInstr = defaultResult();
        WACCParser.AtomContext otherExpr = ctx.otherExprs.get(i);
        dst2 = freeRegisters.peek();
        String op = ctx.ops.get(i).getText();

        if (op.equals(getToken(WACCParser.MUL))){
          // TODO: check this is right for all cases
          arithmeticInstr.add(InstructionFactory.createSmull(dst1,
                                                             dst2,
                                                             dst1,
                                                             dst2));
        } else if (op.equals(getToken(WACCParser.DIV))){
          arithmeticInstr.add(divMoves(dst1, dst2))
              .add(InstructionFactory.createDiv())
              .add(InstructionFactory.createMov(dst1, ARM11Registers.R0));
        } else {
          arithmeticInstr.add(divMoves(dst1, dst2))
              .add(InstructionFactory.createMod())
              .add(InstructionFactory.createMov(dst1, ARM11Registers.R1));
        }

        list.add(visitAtom(otherExpr))
            .add(arithmeticInstr);
        freeRegisters.push(dst2);
      }
    }

    return list;
  }

  private InstructionList divMoves(Operand dst1, Operand dst2) {
    return defaultResult()
        .add(InstructionFactory.createMov(ARM11Registers.R0, dst1))
        .add(InstructionFactory.createMov(ARM11Registers.R1, dst2));
  }

  @Override
  public InstructionList visitUnaryOper(WACCParser.UnaryOperContext ctx) {
    InstructionList list = defaultResult();
    Register dst = freeRegisters.peek();
    if (ctx.ident() != null) {
      list.add(visitIdent(ctx.ident()));
    } else if (ctx.expr() != null) {
      list.add(visitExpr(ctx.expr()));
    }

    if (ctx.NOT() != null) {
      Operand imm = new Immediate((long) 1);
      list.add(InstructionFactory.createEOR(dst, dst, imm));
    } else if (ctx.MINUS() != null) {
      Operand imm = new Immediate((long) 0);
      list.add(InstructionFactory.createRSBS(dst, dst, imm));
    } else if (ctx.LEN() != null) {
      list.add(InstructionFactory.createLoad(dst, dst, 0));
    }

    return list;
  }

  @Override
  public InstructionList visitInteger(WACCParser.IntegerContext ctx) {
    InstructionList list = defaultResult();

    Operand op;
    Instruction loadOrMove;
    Register reg = freeRegisters.pop();
    String digits = ctx.INTEGER().getText();
    long value = Long.parseLong(digits);

    if (ctx.CHR() != null){
      String chr = "\'" + (char) ((int) value) + "\'";
      op = new Immediate(chr);
      loadOrMove = InstructionFactory.createMov(reg, op);
    } else {
      op = new Immediate(value);
      loadOrMove = InstructionFactory.createLoad(reg, op);
    }

    return list.add(loadOrMove);
  }

  @Override
  public InstructionList visitBool(WACCParser.BoolContext ctx) {
    InstructionList list = defaultResult();

    Operand op;
    Instruction move;
    Register reg = freeRegisters.pop();

    String boolLitr = ctx.boolLitr().getText();
    long value = boolLitr.equals("false") ? 0 : 1;

    if (ctx.NOT() != null){
      // ^ is XOR
      op = new Immediate(value^1);
    } else {
      op = new Immediate(value);
    }
    move = InstructionFactory.createMov(reg, op);

    return list.add(move);
  }

  @Override
  public InstructionList visitCharacter(WACCParser.CharacterContext ctx) {
    InstructionList list = defaultResult();

    Operand op;
    Instruction loadOrMove;
    Register reg = freeRegisters.pop();
    String chr = ctx.CHARACTER().getText();

    if (ctx.ORD() != null){
      long value = (int) chr.charAt(1);
      op = new Immediate(value);
      loadOrMove = InstructionFactory.createLoad(reg, op);
    } else {
      op = new Immediate(chr);
      loadOrMove = InstructionFactory.createMov(reg, op);
    }

    return list.add(loadOrMove);
  }

  @Override
  public InstructionList visitString(WACCParser.StringContext ctx) {
    InstructionList list = defaultResult();

    Operand op;
    Register reg = freeRegisters.pop();
    String text = ctx.STRING().getText();
    op = data.addConstString(text);
    list.add(InstructionFactory.createLoad(reg, op));

    if (ctx.LEN() != null) {
      // TODO: allow for 0 default offset
      list.add(InstructionFactory.createLoad(reg, reg, 0));
    }
    return list;
  }

  @Override
  public InstructionList visitIdent(WACCParser.IdentContext ctx) {
    InstructionList list = defaultResult();

    Variable variable = getMostRecentBindingForVariable(ctx.getText());
    long offset = variable.getOffset();
    Register reg = freeRegisters.pop();
    Register sp = ARM11Registers.SP;

    if (Type.isBool(variable.getType()) || Type.isChar(variable.getType())) {
      list.add(InstructionFactory.createLoadStoredBool(reg, sp, offset));
    } else {
      list.add(InstructionFactory.createLoad(reg, sp, offset));
    }

    return list;
  }

  public InstructionList visitReadStat(WACCParser.ReadStatContext ctx) {
    InstructionList list = defaultResult();
    Register reg = freeRegisters.pop();
    Register dst;
    Immediate imm = new Immediate((long) 0);
    InstructionList printHelperFunction;
    Label readLabel;

    if (Type.isInt((Type) ctx.assignLHS().returnType)) {
      readLabel = new Label("p_read_int");
      dst = ARM11Registers.SP;
      printHelperFunction = ReadFunctions.readInt(data);
    } else {
      readLabel = new Label("p_read_char");
      dst = ARM11Registers.R0;
      printHelperFunction = ReadFunctions.readChar(data);
    }

    list.add(InstructionFactory.createAdd(reg, ARM11Registers.SP, imm))
        .add(InstructionFactory.createMov(dst, reg))
        .add(InstructionFactory.createBranchLink(readLabel));
    helperFunctions.add(printHelperFunction);

    return list;
  }

}
