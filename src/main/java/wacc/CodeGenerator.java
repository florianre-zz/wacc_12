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

  //@Override
//  public InstructionList visitInteger(WACCParser.IntegerContext ctx) {
//    InstructionList list = defaultResult();
//    long intValue = Long.valueOf(ctx.INTEGER().getText());
//    list.add(InstructionFactory.createMov());
//    return list;
//  }
  
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
    freeRegisters.push(ARM11Registers.getRegister(ARM11Registers.Reg.R4));
    freeRegisters.push(ARM11Registers.getRegister(ARM11Registers.Reg.R5));
    freeRegisters.push(ARM11Registers.getRegister(ARM11Registers.Reg.R6));
    freeRegisters.push(ARM11Registers.getRegister(ARM11Registers.Reg.R7));
    freeRegisters.push(ARM11Registers.getRegister(ARM11Registers.Reg.R8));
    freeRegisters.push(ARM11Registers.getRegister(ARM11Registers.Reg.R9));
    freeRegisters.push(ARM11Registers.getRegister(ARM11Registers.Reg.R10));
    freeRegisters.push(ARM11Registers.getRegister(ARM11Registers.Reg.R11));
    freeRegisters.push(ARM11Registers.getRegister(ARM11Registers.Reg.R12));
  }

  @Override
  public InstructionList visitMain(WACCParser.MainContext ctx) {
    String scopeName = Scope.MAIN.toString();
    changeWorkingSymbolTableTo(scopeName);

    // TODO: Add the data and helperFunctions sections above and below main

    InstructionList list = defaultResult();

    Label label = new Label(Scope.MAIN.toString());
    list.add(InstructionFactory.createLabel(label));

    Register register = ARM11Registers.getRegister(ARM11Registers.Reg.LR);
    list.add(InstructionFactory.createPush(register));

    list.add(allocateSpaceOnStack());
    list.add(visitChildren(ctx));
    list.add(deallocateSpaceOnStack());

    Register r0 = ARM11Registers.getRegister(ARM11Registers.Reg.R0);
    Operand imm = new Immediate((long) 0);
    list.add(InstructionFactory.createLoad(r0, imm));

    register = ARM11Registers.getRegister(ARM11Registers.Reg.PC);
    list.add(InstructionFactory.createPop(register));

    list.add(InstructionFactory.createLTORG());

    goUpWorkingSymbolTable();
    return list;
  }


  private InstructionList allocateSpaceOnStack() {
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
      Register sp = ARM11Registers.getRegister(ARM11Registers.Reg.SP);
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
      Register sp = ARM11Registers.getRegister(ARM11Registers.Reg.SP);
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

    Register r0 = ARM11Registers.getRegister(ARM11Registers.Reg.R0);
    Operand value = new Immediate(Long.parseLong(ctx.expr().getText()));
    Label label = new Label("exit");

    list.add(InstructionFactory.createLoad(r0, value));
    list.add(InstructionFactory.createBranchLink(label));

    return list;
  }

  @Override
  public InstructionList visitInitStat(WACCParser.InitStatContext ctx) {
    // TODO: Elliot - Elyas : Make createLoad take correct Operand type
    InstructionList list = defaultResult();

    Register reg = freeRegisters.pop();

    Operand op = null;
    Instruction storeInstr = null;
    Register sp  = ARM11Registers.getRegister(ARM11Registers.Reg.SP);
    Variable var = (Variable) workingSymbolTable.get(ctx.ident().getText());
    Operand offset = new Immediate(var.getOffset());


    Type varType = var.getType();
    String text = ctx.assignRHS().getText();
    if (Type.isInt(varType)) {
      long value = Long.parseLong(text);
      op = new Immediate(value);
      storeInstr = InstructionFactory.createStore(reg, sp, offset);
    } else if (Type.isBool(varType)) {
      long value;
      if (text.equals("true")) {
        value = 1;
      } else {
        value = 0;
      }
      op = new Immediate(value);
      storeInstr = InstructionFactory.createStoreBool(reg, sp, offset);
    } else if (Type.isChar(varType)) {
      op = new Immediate(text);
      storeInstr = InstructionFactory.createStore(reg, sp, offset);
    } else if (Type.isString(varType)) {

      // Strip the "" at the start and end "string" -> string
      op = data.addConstString(text.substring(1, text.length() - 1));
      storeInstr = InstructionFactory.createStore(reg, sp, offset);
    }

    list.add(InstructionFactory.createLoad(reg, op));
    list.add(storeInstr);
    freeRegisters.push(reg);
    return list;
  }

  public InstructionList visitPrintStat(WACCParser.PrintStatContext ctx) {
    InstructionList list = defaultResult();

    Label printLabel;

    if (Type.isString((Type) ctx.expr().returnType)) {
      String stringExpr = ctx.expr().getText();
      // Strip the "" at the start and end "string" -> string
      stringExpr = stringExpr.substring(1, stringExpr.length() - 1);

      data.addConstString(stringExpr);

      Register r0 = ARM11Registers.getRegister(ARM11Registers.Reg.R0);
      // TODO: uses freeRegisters (Stack)
      Register r4 = ARM11Registers.getRegister(ARM11Registers.Reg.R4);
      printLabel = new Label("p_print_string");
      Label labelOfStringExpr = data.getConstStringMap().get(stringExpr);

      list.add(InstructionFactory.createLoad(r4, labelOfStringExpr));
      list.add(InstructionFactory.createMov(r0, r4));
      list.add(InstructionFactory.createBranchLink(printLabel));

      InstructionList printHelperFunction = PrintFunctions.printString(data);
      helperFunctions.add(printHelperFunction);

    } else if (Type.isInt((Type) ctx.expr().returnType)) {
      Register r0 = ARM11Registers.getRegister(ARM11Registers.Reg.R0);
      // TODO: uses freeRegisters (Stack)
      Register r4 = ARM11Registers.getRegister(ARM11Registers.Reg.R4);
      Immediate imm = new Immediate(ctx.expr().getText());
      printLabel = new Label("p_print_int");

      list.add(InstructionFactory.createLoad(r4, imm));
      list.add(InstructionFactory.createMov(r0, r4));
      list.add(InstructionFactory.createBranchLink(printLabel));

      InstructionList printHelperFunction = PrintFunctions.printInt(data);
      helperFunctions.add(printHelperFunction);

    } else if (Type.isChar((Type) ctx.expr().returnType)){
      Register r0 = ARM11Registers.getRegister(ARM11Registers.Reg.R0);
      // TODO: uses freeRegisters (Stack)
      Register r4 = ARM11Registers.getRegister(ARM11Registers.Reg.R4);
      Immediate imm = new Immediate(ctx.expr().getText());
      printLabel = new Label("putchar");

      list.add(InstructionFactory.createMov(r4, imm));
      list.add(InstructionFactory.createMov(r0, r4));
      list.add(InstructionFactory.createBranchLink(printLabel));

    } else if (Type.isBool((Type) ctx.expr().returnType)) {
      Register r0 = ARM11Registers.getRegister(ARM11Registers.Reg.R0);
      // TODO: uses freeRegisters (Stack)
      Register r4 = ARM11Registers.getRegister(ARM11Registers.Reg.R4);
      long value = ctx.expr().getText().equals("true") ? 1 : 0;
      Immediate imm = new Immediate(value);
      printLabel = new Label("p_print_bool");

      list.add(InstructionFactory.createMov(r4, imm));
      list.add(InstructionFactory.createMov(r0, r4));
      list.add(InstructionFactory.createBranchLink(printLabel));

      InstructionList printHelperFunction = PrintFunctions.printBool(data);
      helperFunctions.add(printHelperFunction);

    } else {
      Register r0 = ARM11Registers.getRegister(ARM11Registers.Reg.R0);
      // TODO: uses freeRegisters (Stack)
      Register r4 = ARM11Registers.getRegister(ARM11Registers.Reg.R4);
      Register sp = ARM11Registers.getRegister(ARM11Registers.Reg.SP);
      Operand address = new Address(sp);
      printLabel = new Label("p_print_reference");

      list.add(InstructionFactory.createLoad(r4, address));
      list.add(InstructionFactory.createMov(r0, r4));
      list.add(InstructionFactory.createBranchLink(printLabel));

      InstructionList printHelperFunction = PrintFunctions.printReference(data);
      helperFunctions.add(printHelperFunction);
    }

    if (ctx.PRINTLN() != null) {
      printLabel = new Label("p_print_ln");
      list.add(InstructionFactory.createBranchLink(printLabel));
      InstructionList printHelperFunction = PrintFunctions.printLn(data);
      helperFunctions.add(printHelperFunction);
    }

    return list;
  }

  @Override
  public InstructionList visitReadStat(WACCParser.ReadStatContext ctx) {
    InstructionList list = defaultResult();

    return list;
  }
}
