package wacc;

import antlr.WACCLexer;
import antlr.WACCParser;
import arm11.InstructionList;
import bindings.Binding;
import bindings.PairType;
import bindings.Type;
import bindings.Types;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;
import wacc.error.WACCErrorHandler;
import wacc.error.WACCLexerErrorListener;

import java.io.IOException;

public class WACCCompile {
  public static void main(String[] args) throws Exception {

    ANTLRInputStream input = new ANTLRInputStream(System.in);
    WACCLexer lexer = new WACCLexer(input);
    CommonTokenStream tokenStream = new CommonTokenStream(lexer);

    WACCErrorHandler errorHandler = new WACCErrorHandler(tokenStream);
    ParseTree tree = performLexicalAnalysis(errorHandler, tokenStream);

    if (errorHandler.printLexingErrors()) {
      System.exit(100);
    }

    SymbolTable<String, Binding> top
        = performSemanticAnalysis(tree, errorHandler);

    checkForErrors(errorHandler);

    performCodeGeneration(tree, top);
  }

  private static ParseTree performLexicalAnalysis(WACCErrorHandler errorHandler,
                     CommonTokenStream tokenStream) throws IOException {

    WACCParser parser = new WACCParser(tokenStream);
    WACCLexerErrorListener errorListener = new WACCLexerErrorListener();

    parser.removeErrorListeners();
    parser.addErrorListener(errorListener);
    ParseTree tree = parser.prog();
    errorHandler.complainAboutLexing(errorListener.getErrors());

    return tree;
  }

  private static SymbolTable<String, Binding> performSemanticAnalysis(
                                              ParseTree tree,
                                              WACCErrorHandler errorHandler) {
    SymbolTable<String, Binding> top = createTopSymbolTable();

    WACCSymbolTableFiller buildSTVisitor
        = new WACCSymbolTableFiller(top, errorHandler);
    buildSTVisitor.visit(tree);

    WACCTypeChecker typeChecker = new WACCTypeChecker(top, errorHandler);
    typeChecker.visit(tree);

    return top;
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
    top.put(Types.UNDEFINED_T.toString(), new Type(Types.UNDEFINED_T));
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

  private static void performCodeGeneration(ParseTree tree,
                                            SymbolTable<String, Binding> top) {
    CodeGenerator codeGenerator = new CodeGenerator(top);
    InstructionList program = codeGenerator.visit(tree);
    System.out.println(program);
  }

}