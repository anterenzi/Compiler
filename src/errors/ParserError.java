package errors;

import lexicalanalyzer.*;
import rhs.*;

public class ParserError extends CompilerError {

    public ParserError(Type errorNumber, String message) {
        super(errorNumber, message);
    }

    // Factory methods to generate the lexical exception types.
    public static ParserError SomethingIsWrong() {
        return new ParserError(Type.SOMETHING_WRONG,
                ">>>: ERROR ~~~~");
    }
    public static ParserError TokenMismatch(GrammarSymbol predicted, GrammarSymbol currentToken, int line)
    {
        return new ParserError(Type.TOKEN_MISMATCH,
                ">>>: ERROR: Expecting " + predicted + ", " + currentToken + " found at line " + line);
    }
    public static ParserError RHS999(GrammarSymbol predicted, GrammarSymbol currentToken, int line)
    {
        return new ParserError(Type.RHS_ERROR,
                ">>>: ERROR: Unexpected " + currentToken + " found at line " + line + " while parsing " + predicted);
    }
    public static void TokenMismatchv2(GrammarSymbol predicted, GrammarSymbol currentToken, int line)
    {
        String x = ">>>: ERROR: Expecting " + predicted + ", " + currentToken + " found at line " + line;
        System.out.println(x);
    }
    public static void RHS999v2(GrammarSymbol predicted, GrammarSymbol currentToken, int line)
    {
        String x = ">>>: ERROR: Unexpected " + currentToken +
                " found at line " + line + " while parsing " + predicted;
        System.out.println(x);
    }
}
