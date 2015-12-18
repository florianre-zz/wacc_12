//package wacc.ast;
//
//import bindings.Type;
//
//import java.util.List;
//
//enum BinaryOp {}
//enum UnaryOp {}
//
//class ASTNode {
//    Type type;
//}
//
//abstract class Expr extends ASTNode {
//}
//
//abstract class Func extends ASTNode {
//
//}
//
//class BinaryExpr extends Expr {
//    Expr expr1;
//    Expr expr2;
//    BinaryOp op;
//}
//
//class UnaryExpr extends Expr {
//    Expr expr;
//    UnaryOp op;
//}
//
//public abstract class Stat extends Node {}
//
//public class SkipStat extends Stat {}
//public class InitStat extends Stat {
//}
//public class AssignStat extends Stat {}
//public class ReadStat extends Stat {}
//public class FreeStat extends Stat {
//    AST.Expr expr;
//}
//public class ReturnStat extends Stat {
//    AST.Expr expr;
//}
//public class ExitStat extends Stat {
//    AST.Expr expr;
//}
//public class PrintStat extends Stat {
//    AST.Expr expr;
//}
//public class PrintLnStat extends Stat {
//    AST.Expr expr;
//}
//public class IfStat extends Stat {
//    AST.Expr expr;
//}
//public class WhileStat extends Stat {
//    AST.Expr expr;
//}
//public class BeginStat extends Stat {
//    AST.Expr expr;
//}
//
//public class AST {
//
//
//    public class Program {
//        List<Func> functions;
//        List<Stat> main;
//
//        public Program(List<Func> functions, List<Stat> main) {
//            this.functions = functions;
//            this.main = main;
//        }
//    }
//
//
//
//    // prog
//    // paramList
//    // stat - abstract
//        // skip
//        // init
//        // assign
//        // read
//        // free
//        // return
//        // exit
//        // print
//        // println
//        // if (then else fi)
//        // while (do done)
//        // begin (end)
//    // statList
//
//    // assignLHS
//    // assignRHS
//
//
//    // func
//
//
//}
