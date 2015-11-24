package wacc;

import antlr.WACCParser;
import arm11.*;
import bindings.Binding;
import bindings.Variable;
import org.antlr.v4.runtime.misc.NotNull;
import java.util.HashSet;
import java.util.List;

// TODO: Do we need top for labels?

public class CodeGenerator extends WACCVisitor<InstructionList> {

  private DataInstructions data;
  private HashSet<InstructionList> helperFunctions;

  public CodeGenerator(SymbolTable top) {

    super(top);
    this.data = new DataInstructions();
    this.helperFunctions = new HashSet<>();
  }

  //@Override
//  public InstructionList visitInteger(@NotNull WACCParser.IntegerContext ctx) {
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
    String scopeName = Scope.PROG.toString();
    changeWorkingSymbolTableTo(scopeName);
    InstructionList program = defaultResult();
    program.add(InstructionFactory.createText());
    Label mainLabel = new Label(WACCVisitor.Scope.MAIN.toString());
    program.add(InstructionFactory.createGlobal(mainLabel));
    program.add(visitMain(ctx.main()));
    goUpWorkingSymbolTable();
    return program;
  }

  @Override
  public InstructionList visitMain(WACCParser.MainContext ctx) {
    String scopeName = Scope.MAIN.toString();
    changeWorkingSymbolTableTo(scopeName);

    // TODO: Add the data and helperFunctions sections above and below main

    InstructionList list = defaultResult();

    Label label = new Label(Scope.MAIN.toString());
    list.add(InstructionFactory.createLabel(label));

    Register register
        = ARM11Registers.getRegister(ARM11Registers.Reg.LR);
    list.add(InstructionFactory.createPush(register));

    list.add(allocateSpaceOnStack());
    list.add(visitChildren(ctx));
    list.add(deallocateSpaceOnStack());

    Register r0 = ARM11Registers.getRegister(ARM11Registers.Reg.R0);
    Operand value = new Immediate((long) 0);
    list.add(InstructionFactory.createLoad(r0, value));

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
    Long imm = Long.parseLong(ctx.expr().getText());
    Operand value = new Immediate(imm);
    Label label = new Label("exit");

    list.add(InstructionFactory.createLoad(r0, value));
    list.add(InstructionFactory.createBranchLink(label));

    return list;
  }

//  }

//  }

  @Override
  public InstructionList visitInitStat(@NotNull WACCParser.InitStatContext ctx) {
    InstructionList list = defaultResult();

    Register reg = ARM11Registers.getRegister(ARM11Registers.Reg.R4);
    // TODO: take into consideration other types
    long value = Long.parseLong(ctx.assignRHS().getText());

    Operand op = new Immediate(value);
    Register sp  = ARM11Registers.getRegister(ARM11Registers.Reg.SP);
    Binding binding = workingSymbolTable.get(ctx.ident().getText());
    Operand offset = new Immediate(((Variable) binding).getOffset());

    list.add(InstructionFactory.createLoad(reg, op));
    list.add(InstructionFactory.createStore(reg, sp, offset));
    return list;
  }

  public InstructionList visitPrintStat(
      @NotNull WACCParser.PrintStatContext ctx) {
    InstructionList list = defaultResult();

    // TODO: find the type of what we are printing

    // We are assuming ints

    // TODO:

    Register r0 = ARM11Registers.getRegister(ARM11Registers.Reg.R0);
    Register r4 = ARM11Registers.getRegister(ARM11Registers.Reg.R4);

    // Assume it saves result in r4
    list.add(visitExpr(ctx.expr()));

    list.add(InstructionFactory.createMov(r0, r4));
    list.add(InstructionFactory.createBranchLink(new Label("p_print_int")));

    InstructionList printHelperFunction = PrintFunctions.printInt(data);
    helperFunctions.add(printHelperFunction);

    return list;
  }

}
