package wacc;

import antlr.WACCParser;
import arm11.*;
import bindings.Binding;
import bindings.Type;
import bindings.Variable;

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

  @Override
  public InstructionList visitProg(WACCParser.ProgContext ctx) {
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

    // TODO: Add the data and helperFunctions sections above and below main

    InstructionList list = defaultResult();

    Label label = new Label(Scope.MAIN.toString());
    list.add(InstructionFactory.createLabel(label));

    Register register = ARM11Registers.LR;
    list.add(InstructionFactory.createPush(register));

    list.add(allocateSpaceOnStack());
    list.add(visitChildren(ctx));
    list.add(deallocateSpaceOnStack());

    Register r0 = ARM11Registers.R0;
    Operand imm = new Immediate((long) 0);
    list.add(InstructionFactory.createLoad(r0, imm));

    register = ARM11Registers.PC;
    list.add(InstructionFactory.createPop(register));

    list.add(InstructionFactory.createLTORG());

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

    for (Binding b : variables) {
      Variable v = (Variable) b;
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
  public InstructionList visitInitStat(WACCParser.InitStatContext ctx) {
    InstructionList list = defaultResult();
    addVariableToCurrentScope(ctx.ident().getText());

    // TODO: move the pop to visitAssignRHS
    Register reg = freeRegisters.peek();

    Instruction storeInstr;
    Register sp  = ARM11Registers.SP;
    Variable var = (Variable) workingSymbolTable.get(ctx.ident().getText());
    Operand offset = new Immediate(var.getOffset());

    if (Type.isBool(var.getType()) || Type.isChar(var.getType())){
      storeInstr = InstructionFactory.createStoreBool(reg, sp, offset);
    } else {
      storeInstr = InstructionFactory.createStore(reg, sp, offset);
    }

    list.add(visitAssignRHS(ctx.assignRHS()));
    list.add(storeInstr);
    freeRegisters.push(reg);
    return list;
  }

  public InstructionList visitPrintStat(WACCParser.PrintStatContext ctx) {
    InstructionList list = defaultResult();
    Label printLabel;
    InstructionList printHelperFunction = null;
    Type returnType = (Type) ctx.expr().returnType;

    if (Type.isString(returnType)) {
      printHelperFunction = PrintFunctions.printString(data);
      data.addConstString(ctx.expr().getText());
      printLabel = new Label("p_print_string");
      list.add(visitExpr(ctx.expr()));
    } else if (Type.isInt((Type) ctx.expr().returnType)) {
      printHelperFunction = PrintFunctions.printInt(data);
      printLabel = new Label("p_print_int");
    } else if (Type.isChar((Type) ctx.expr().returnType)){
      printLabel = new Label("putchar");
    } else if (Type.isBool((Type) ctx.expr().returnType)) {
      printHelperFunction = PrintFunctions.printBool(data);
      printLabel = new Label("p_print_bool");
    } else {
      printHelperFunction = PrintFunctions.printReference(data);
      printLabel = new Label("p_print_reference");
    }

    list.add(visitExpr(ctx.expr()));
    list.add(InstructionFactory.createBranchLink(printLabel));

    if (helperFunctions != null) {
      helperFunctions.add(printHelperFunction);
    }

    if (ctx.PRINTLN() != null) {
      printLabel = new Label("p_print_ln");
      list.add(InstructionFactory.createBranchLink(printLabel));
      printHelperFunction = PrintFunctions.printLn(data);
      helperFunctions.add(printHelperFunction);
    }

    return list;
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

    list.add(loadOrMove);
    // TODO: make InstructionList a builder so we wan return list.add(l)
    return list;
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

    list.add(move);
    // TODO: make InstructionList a builder so we wan return list.add(l)
    return list;
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

    list.add(loadOrMove);
    // TODO: make InstructionList a builder so we wan return list.add(l)
    return list;
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

    if (Type.isInt((Type) ctx.assignLHS().returnType)) {
      Register r0 = ARM11Registers.R0;
      // TODO: uses freeRegisters (Stack)
      Register r4 = ARM11Registers.R4;
      Register sp = ARM11Registers.SP;
      Immediate imm = new Immediate((long) 0);
      Label readLabel = new Label("p_read_int");

      list.add(InstructionFactory.createAdd(r4, sp, imm));
      list.add(InstructionFactory.createMov(r0, r4));
      list.add(InstructionFactory.createBranchLink(readLabel));

      InstructionList printHelperFunction = ReadFunctions.readInt(data);
      helperFunctions.add(printHelperFunction);

    } else {
      Register r0 = ARM11Registers.R0;
      // TODO: uses freeRegisters (Stack)
      Register r4 = ARM11Registers.R4;
      Register sp = ARM11Registers.SP;
      Immediate imm = new Immediate((long) 0);
      Label readLabel = new Label("p_read_char");

      list.add(InstructionFactory.createAdd(r4, sp, imm));
      list.add(InstructionFactory.createMov(r0, r4));
      list.add(InstructionFactory.createBranchLink(readLabel));

      InstructionList printHelperFunction = ReadFunctions.readChar(data);
      helperFunctions.add(printHelperFunction);
    }
    return list;
  }

}
