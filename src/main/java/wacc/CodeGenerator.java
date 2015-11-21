package wacc;

import antlr.WACCParser;
import antlr.WACCParserBaseVisitor;
import arm11.*;

// TODO: review codestyle check errors
// TODO: Do we need top for labels?

public class CodeGenerator extends WACCParserBaseVisitor<InstructionList> {

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

    InstructionList list = new InstructionList();

    Label label = new Label(WACCVisitor.Scope.MAIN.toString());
    list.add(InstructionFactory.createLabel(label));

    Register register = ARM11Registers.getRegister(14);
    list.add(InstructionFactory.createPush(register));

    list.add(visitChildren(ctx));

    Register R0 = ARM11Registers.getRegister(0);
    Operand value = new Immediate((long) 0);
    list.add(InstructionFactory.createLoad(R0, value));

    register = ARM11Registers.getRegister(15);
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

    Register R0 = ARM11Registers.getRegister(0);
    Long imm = Long.parseLong(ctx.expr().getText());
    Operand value = new Immediate(imm);
    Label label = new Label("exit");

    list.add(InstructionFactory.createLoad(R0, value));
    list.add(InstructionFactory.createBranchLink(label));

    return list;
  }

}
