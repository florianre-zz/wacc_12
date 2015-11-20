package wacc;

import antlr.WACCParser;
import antlr.WACCParserBaseVisitor;
import arm11.*;
import org.antlr.v4.runtime.misc.NotNull;

public class CodeGenerator extends WACCParserBaseVisitor<InstructionList> {

  @Override
  public InstructionList visitMain(@NotNull WACCParser.MainContext ctx) {

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

    return super.visitMain(ctx);
  }

  @Override
  public InstructionList visitSkipStat(
      @NotNull WACCParser.SkipStatContext ctx) {
    return new InstructionList();
  }

  @Override
  public InstructionList visitExitStat(
      @NotNull WACCParser.ExitStatContext ctx) {
    InstructionList list = new InstructionList();

    Register R0 = ARM11Registers.getRegister(0);
    Long imm = Long.parseLong(ctx.expr().getText());
    Operand value = new Immediate(imm);
    Label label = new Label("");

    list.add(InstructionFactory.createLoad(R0, value));
    list.add(InstructionFactory.createBranchLink(label));

    return list;
  }
}
