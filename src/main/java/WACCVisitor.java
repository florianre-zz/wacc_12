import antlr.*;

public class WACCVisitor extends WACCParserBaseVisitor<Void> {

  public Void visitProg(WACCParser.ProgContext ctx) {
    System.out.println("Good morning, who's awake!?");
    return visitChildren(ctx);
  }

  public Void visitFunc(WACCParser.FuncContext ctx) {
    System.out.println("I found a function definition!");
    System.out.println(ctx);
//    System.out.print("Type info: ");
//    //need to visit function args in a loop
//    for (int i = 0; i < ctx.params.size(); i++) {
//      visit(ctx.params.get(i));
//    }
//    System.out.print(" => ");
//    //visit function return type (note this is out of normal tree order)
//    visitChildren(ctx.param);
    return null;
  }

//  public Void visitBaseType(WACCParser.BaseTypeContext ctx) {
//    System.out.print(ctx.value);
//    return null;
//  }
}
