//ANDREW TERENZI
package errors;

/**
 * Base class for errors generated by the parts of the compiler.
 */
public abstract class CompilerError extends Exception
{
   /** The type of error.  New types should be added to the enumeration
    * as the compiler generates new errors.
    */
   public enum Type {BAD_COMMENT, ILLEGAL_CHARACTER, UNTERMINATED_COMMENT, TOO_LONG_IDENTIFIER,
        TOO_LONG_CONSTANT, NO_DIGIT_AFTER_DECIMAL, ILL_FORMED_CONSTANT,
        SOMETHING_WRONG, TOKEN_MISMATCH, RHS_ERROR, MOD, UNDECLARED_VARIABLE,
        OFFSET_TYPE, NOT_ARRAY, EARITHMETIC, ERELATIONAL, DUMMY, NOT_INTEGER,
            NO_PARAMS, NOT, INCORRECT_PARAM_NUMBER, INCORRECT_TYPE, NOT_FUNC, NOT_PROC};

   /** The type of error represented by this object.  This field is declared
    * as final and must be set in the constructor.
    */
   protected final Type errorType;

   public CompilerError(Type errorType)
   {
      super("Unknown error");
      this.errorType = errorType;
   }

   public CompilerError(Type errorType, String message)
   {
      super(message);
      this.errorType = errorType;
   }
}

