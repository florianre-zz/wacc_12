package wacc;

import antlr.WACCLexer;
import antlr.WACCParser;
import arm11.Instruction;
import arm11.InstructionList;
import bindings.Binding;
import bindings.PairType;
import bindings.Type;
import bindings.Types;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;
import wacc.error.WACCErrorHandler;

import java.io.IOException;

public class WACCCompile {
  public static void main(String[] args) throws Exception {

    CommonTokenStream tokens = performLexicalAnalysis();
    WACCErrorHandler errorHandler = new WACCErrorHandler(tokens);

    ParseTree tree = new WACCParser(tokens).prog();

    performSemanticAnalysis(tree, errorHandler);

    checkForErrors(errorHandler);

    performCodeGeneration(tree);

  }

  private static CommonTokenStream performLexicalAnalysis() throws IOException {
    ANTLRInputStream input = new ANTLRInputStream(System.in);
    WACCLexer lexer = new WACCLexer(input);
    return new CommonTokenStream(lexer);
  }

  private static void performSemanticAnalysis(ParseTree tree,
                                              WACCErrorHandler errorHandler) {

//    int numberOfSyntaxErrors = parser.getNumberOfSyntaxErrors();
//    if (numberOfSyntaxErrors > 0) {
//      System.err.println(numberOfSyntaxErrors + " Syntax Errors");
//      System.exit(100);
//    }

    SymbolTable<String, Binding> top = createTopSymbolTable();

    WACCSymbolTableFiller buildSTVisitor
        = new WACCSymbolTableFiller(top, errorHandler);
    buildSTVisitor.visit(tree);

    WACCTypeChecker typeChecker = new WACCTypeChecker(top, errorHandler);
    typeChecker.visit(tree);

  }

  private static SymbolTable<String, Binding> createTopSymbolTable() {
    SymbolTable<String, Binding> top = new SymbolTable<>();
    top.put(Types.INT_T.toString(), new Type(Types.INT_T,
                                             WACCConstants.MIN_INT,
                                             WACCConstants.MAX_INT));
    top.put(Types.BOOL_T.toString(), new Type(Types.BOOL_T, 0, 1));
    top.put(Types.CHAR_T.toString(), new Type(Types.CHAR_T, 0, 255));
    top.put(Types.STRING_T.toString(), new Type(Types.STRING_T));
    top.put(Types.PAIR_T.toString(), new PairType());
    return top;
  }

  private static void checkForErrors(WACCErrorHandler errorHandler) {
    if (errorHandler.hasSyntaxErrors()) {
      errorHandler.printSyntaxErrors();
      System.exit(100);
    } else if (errorHandler.hasSemanticErrors()) {
      errorHandler.printSemanticErrors();
      System.exit(200);
    }
  }

  private static void performCodeGeneration(ParseTree tree) {
    CodeGenerator codeGenerator = new CodeGenerator();
    InstructionList program = codeGenerator.visit(tree);
    System.out.println(program);
  }

}