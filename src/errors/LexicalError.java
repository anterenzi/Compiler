//ANDREW TERENZI
package errors;

/**
 * Exception class thrown when a lexical error is encountered.
 */
public class LexicalError extends CompilerError {

    public LexicalError(Type errorNumber, String message) {
        super(errorNumber, message);
    }

    // Factory methods to generate the lexical exception types.
    public static LexicalError BadComment() {
        return new LexicalError(Type.BAD_COMMENT,
                ">>> ERROR: Cannont include { inside a comment.");
    }

    public static LexicalError IllegalCharacter(char c) {
        return new LexicalError(Type.ILLEGAL_CHARACTER,
                ">>> ERROR: Illegal character: " + c);
    }

    public static LexicalError IllegalCharacter(char c, int lineNumber) {
        return new LexicalError(Type.ILLEGAL_CHARACTER,
                ">>> ERROR: Illegal character: " + c
                + " at line: " + lineNumber);
    }

    public static LexicalError UnterminatedComment() {
        return new LexicalError(Type.UNTERMINATED_COMMENT,
                ">>> ERROR: Unterminated comment.");
    }

    public static LexicalError TooLongIdentifier(String x) {
        return new LexicalError(Type.TOO_LONG_IDENTIFIER,
                ">>> ERROR: Identifier is too long: " + x);
    }

    public static LexicalError TooLongConstant(String x) {
        return new LexicalError(Type.TOO_LONG_CONSTANT,
                ">>> ERROR: Constant is too long: " + x);
    }

    public static LexicalError NoDigitAfterDecimal(String x) {
        return new LexicalError(Type.NO_DIGIT_AFTER_DECIMAL,
                ">>> ERROR: No digit after decimal: " + x + ".");
    }

    public static LexicalError IllFormedConstant(String x) {
        return new LexicalError(Type.ILL_FORMED_CONSTANT,
                ">>> ERROR: Ill formed constant: " + x);
    }
}
