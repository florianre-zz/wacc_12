package wacc.error;

import org.antlr.v4.runtime.*;
import java.util.ArrayList;
import java.util.List;

public class WACCLexerErrorListener extends BaseErrorListener {

    List<String> errors = new ArrayList<>();

    @Override
    public <T extends Token> void syntaxError(Recognizer<T, ?> recognizer, T offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
                StringBuilder message = new StringBuilder();
        message.append("  at ")
                .append(String.format("%4d", line))
                .append(":").append(String.format("%02d", charPositionInLine))
                .append(" -- ").append("Syntax Error: ")
                .append(msg);
        errors.add(message.toString());
    }

    public List<String> getErrors() {
        return errors;
    }

}
