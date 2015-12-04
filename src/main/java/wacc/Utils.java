package wacc;

import antlr.WACCParser;
import bindings.*;
import org.antlr.v4.runtime.ParserRuleContext;
import wacc.error.DeclarationError;
import wacc.error.ErrorHandler;
import wacc.error.TypeAssignmentError;
import wacc.error.TypeError;

public class Utils {

    public static boolean isReadable(Type lhsType) {
        return Type.isInt(lhsType) || Type.isChar(lhsType);
    }

    public static boolean isFreeable(Type exprType) {
        return ArrayType.isArray(exprType) || PairType.isPair(exprType);
    }

    public static void incorrectType(ParserRuleContext ctx,
                                     Type exprType, String expectedType,
                                     ErrorHandler errorHandler) {
        String actual = exprType != null ? exprType.toString() : "'null'";
        errorHandler.complain(
                new TypeAssignmentError(ctx, expectedType, actual)
        );
    }

    public static boolean checkTypesEqual(ParserRuleContext ctx,
                                          Type lhsType, Type rhsType,
                                          ErrorHandler errorHandler) {
        if (lhsType != null) {
            if (!lhsType.equals(rhsType)) {
                incorrectType(ctx, rhsType, lhsType.toString(), errorHandler);
                return false;
            }
            return true;
        } else {
            errorHandler.complain(new TypeError(ctx, "Null Type"));
        }
        return false;
    }

    public static Type lookupTypeInWorkingSymbolTable(
            String key, SymbolTable<String, Binding> workingSymbolTable) {
        Binding b = workingSymbolTable.lookupAll(key);
        if (b instanceof Variable) {
            return ((Variable) b).getType();
        }
        if (b instanceof Function) {
            return ((Function) b).getType();
        }
        return null;
    }

    public static void inconsistentParamCountError(WACCParser.CallContext ctx,
                                             int expectedSize, int actualSize,
                                             ErrorHandler errorHandler) {
        StringBuilder sb = new StringBuilder();
        sb.append("The number of arguments doesn't match function declaration: ");

        sb.append(ctx.getText()).append("\n");
        sb.append("There are currently ").append(actualSize);
        sb.append(" params, there should be ");
        sb.append(expectedSize);

        String errorMsg = sb.toString();
        errorHandler.complain(new DeclarationError(ctx, errorMsg));
    }
}
