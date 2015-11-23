package wacc;

import antlr.WACCParser;
import arm11.*;
import bindings.Binding;
import bindings.Variable;
import org.antlr.v4.runtime.misc.NotNull;

import java.util.List;

// TODO: review codestyle check errors
// TODO: Do we need top for labels?

public class CodeGenerator extends WACCVisitor<InstructionList> {

  public CodeGenerator(SymbolTable top) {
    super(top);
  }

  @Override
  public InstructionList visitProg(WACCParser.ProgContext ctx) {
    String scopeName = Scope.PROG.toString();
    changeWorkingSymbolTableTo(scopeName);
    InstructionList program = new InstructionList();
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

    InstructionList list = new InstructionList();

    Label label = new Label(Scope.MAIN.toString());
    list.add(InstructionFactory.createLabel(label));

    Register register = ARM11Registers.getRegister(14);
    list.add(InstructionFactory.createPush(register));

    list.add(allocateSpaceOnStack());
    list.add(visitChildren(ctx));
    list.add(deallocateSpaceOnStack());

    Register R0 = ARM11Registers.getRegister(0);
    Operand value = new Immediate((long) 0);
    list.add(InstructionFactory.createLoad(R0, value));

    register = ARM11Registers.getRegister(15);
    list.add(InstructionFactory.createPop(register));

    list.add(InstructionFactory.createLTORG());

    goUpWorkingSymbolTable();
    return list;
  }

  private InstructionList allocateSpaceOnStack() {
    InstructionList list = new InstructionList();
    List<Binding> variables = workingSymbolTable.filterByClass(Variable.class);
    long stackSpaceSize = 0;
    for (Binding b : variables) {
      Variable v = (Variable) b;
      stackSpaceSize += v.getType().getSize();
    }
    // TODO: deal with '4095', the max size... of something
    if (stackSpaceSize > 0) {
      Operand imm = new Immediate(stackSpaceSize);
      Register sp = ARM11Registers.getRegister(13);
      list.add(InstructionFactory.createSub(sp, sp, imm));
    }

    return list;
  }


  private InstructionList deallocateSpaceOnStack() {
    InstructionList list = new InstructionList();
    List<Binding> variables = workingSymbolTable.filterByClass(Variable.class);
    long stackSpaceSize = 0;
    for (Binding b : variables) {
      Variable v = (Variable) b;
      stackSpaceSize += v.getType().getSize();
    }

    if (stackSpaceSize > 0) {
      Operand imm = new Immediate(stackSpaceSize);
      Register sp = ARM11Registers.getRegister(13);
      list.add(InstructionFactory.createAdd(sp, sp, imm));
    }

    return list;
  }

  @Override
  public InstructionList visitSkipStat(WACCParser.SkipStatContext ctx) {
    return new InstructionList();
  }

  @Override
  public InstructionList visitExitStat(WACCParser.ExitStatContext ctx) {
    InstructionList list = new InstructionList();

    Register R0 = ARM11Registers.getRegister(0);
    Long imm = Long.parseLong(ctx.expr().getText());
    Operand value = new Immediate(imm);
    Label label = new Label("exit");

    list.add(InstructionFactory.createLoad(R0, value));
    list.add(InstructionFactory.createBranchLink(label));

    return list;
  }

  @Override
  public InstructionList visitInitStat(@NotNull WACCParser.InitStatContext ctx) {
    // TODO: put value in a free register
    // TODO: move what's in that register to the stack with the offset for this value
    // TODO: change return value
    return new InstructionList();
  }
}
