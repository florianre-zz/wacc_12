package wacc;

import antlr.WACCParser;
import antlr.WACCParserBaseVisitor;
import arm11.*;
import org.antlr.v4.runtime.misc.NotNull;

import java.util.HashSet;

// TODO: Do we need top for labels?

public class CodeGenerator extends WACCParserBaseVisitor<InstructionList> {

  private DataInstructions data;
  private HashSet<InstructionList> helperFunctions;

  public CodeGenerator() {
    this.data = new DataInstructions();
    this.helperFunctions = new HashSet<>();
  }

  @Override
  public InstructionList visitProg(WACCParser.ProgContext ctx) {
    InstructionList program = new InstructionList();
    program.add(InstructionFactory.createText());
    Label mainLabel = new Label(WACCVisitor.Scope.MAIN.toString());
    program.add(InstructionFactory.createGlobal(mainLabel));
    program.add(visitMain(ctx.main()));
    return program;
  }

  @Override
  public InstructionList visitMain(WACCParser.MainContext ctx) {

    // TODO: Add the data and helperFunctions sections above and below main

    InstructionList list = new InstructionList();

    Label label = new Label(WACCVisitor.Scope.MAIN.toString());
    list.add(InstructionFactory.createLabel(label));

    Register register
        = ARM11Registers.getRegister(ARM11Registers.ARM11Register.LR);
    list.add(InstructionFactory.createPush(register));

    list.add(visitChildren(ctx));

    Register r0 = ARM11Registers.getRegister(ARM11Registers.ARM11Register.R0);
    Operand value = new Immediate((long) 0);
    list.add(InstructionFactory.createLoad(r0, value));

    register = ARM11Registers.getRegister(ARM11Registers.ARM11Register.PC);
    list.add(InstructionFactory.createPop(register));

    list.add(InstructionFactory.createLTORG());

    return list;
  }

  @Override
  public InstructionList visitSkipStat(WACCParser.SkipStatContext ctx) {
    return new InstructionList();
  }

  @Override
  public InstructionList visitExitStat(WACCParser.ExitStatContext ctx) {

    InstructionList list = new InstructionList();

    Register r0 = ARM11Registers.getRegister(ARM11Registers.ARM11Register.R0);
    Long imm = Long.parseLong(ctx.expr().getText());
    Operand value = new Immediate(imm);
    Label label = new Label("exit");

    list.add(InstructionFactory.createLoad(r0, value));
    list.add(InstructionFactory.createBranchLink(label));

    return list;
  }

//  @Override
//  public InstructionList visitInteger(@NotNull WACCParser.IntegerContext ctx) {
//    InstructionList list = new InstructionList();
//    long intValue = Long.valueOf(ctx.INTEGER().getText());
//    list.add(InstructionFactory.createMov());
//    return list;
//  }

  @Override
  public InstructionList visitPrintStat(
      @NotNull WACCParser.PrintStatContext ctx) {
    InstructionList list = new InstructionList();

    // TODO: find the type of what we are printing

    // We are assuming ints

    // TODO:

    Register r0 = ARM11Registers.getRegister(ARM11Registers.ARM11Register.R0);
    Register r4 = ARM11Registers.getRegister(ARM11Registers.ARM11Register.R4);

    // Assume it saves reult in r4
    list.add(visitExpr(ctx.expr()));

    list.add(InstructionFactory.createMov(r0, r4));
    list.add(InstructionFactory.createBranchLink(new Label("p_print_int")));

    InstructionList printHelperFunction = PrintFunctions.printInt(data);
    helperFunctions.add(printHelperFunction);

    return list;
  }
}
