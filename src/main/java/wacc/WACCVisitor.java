package wacc;

import antlr.WACCParser;
import antlr.WACCParserBaseVisitor;

public class WACCVisitor extends WACCParserBaseVisitor<Void> {

  public Void visitProg(WACCParser.ProgContext ctx) {
    System.out.println("Good morning, who's awake!?");
    return visitChildren(ctx);
  }

  public Void visitFunc(WACCParser.FuncContext ctx) {
    System.out.println("I found a function definition!");
    System.out.println(ctx.funcName.getText());
    System.out.print("bindings.Type info: ");
    //need to visit function args in a loop
    for (int i = 0; i < ctx.paramList().param().size(); i++) {
      visit(ctx.paramList().param(i));
    }
    System.out.print(" => ");
    //visit function return type (note this is out of normal tree order)
    visitChildren(ctx.type());

    return null;
  }

//  public Void visitBaseType(WACCParser.BaseTypeContext ctx) {
//    System.out.print(ctx.value);
//    return null;
//  }
}
