package errors;

import lexicalanalyzer.*;
import rhs.*;
import symboltable.*;

public class SemanticError extends CompilerError {

    

    public SemanticError(Type errorNumber, String message) {
        super(errorNumber, message);
    }
    
    public static SemanticError MOD(int action, SymbolTableEntry id1, SymbolTableEntry id2)
    {
        return new SemanticError(Type.MOD,
                ">>>: ERROR during action " + action + 
                " ~~~~ MOD requires integer operands, got" +
                id1.getType() + " and " + id2.getType());
    }
    public static SemanticError UNDECLARED_VARIABLE(int action, Token token)
    {
        return new SemanticError(Type.UNDECLARED_VARIABLE,
                ">>>: ERROR during action " + action + 
                " ~~~~ Undeclared variable: " +
                token.getType());
    }
    
    public static SemanticError OFFSET_TYPE(int action, TokenType token)
    {
        return new SemanticError(Type.OFFSET_TYPE,
                ">>>: ERROR during action " + action + 
                " ~~~~ offset type should be integer, but got: " +
                token);
    }
    
    public static SemanticError NOT_ARRAY(int action, TokenType token)
    {
        return new SemanticError(Type.NOT_ARRAY,
                ">>>: ERROR during action " + action + 
                " ~~~~ symboltableentry should be an array entry, but got: " +
                token);
    }
    
    public static SemanticError NOT_INTEGER(int action, TokenType token)
    {
        return new SemanticError(Type.NOT_INTEGER,
                ">>>: ERROR during action " + action + 
                " ~~~~ symboltableentry should be an integer, but got: " +
                token);
    }
    
    public static SemanticError NO_PARAMS(int action, SymbolTableEntry id)
    {
        return new SemanticError(Type.NO_PARAMS,
                ">>>: ERROR during action " + action + 
                " ~~~~ symboltableentry has no parameters: " + id);
    }
    
    public static SemanticError EARITHMETIC()
    {
        return new SemanticError(Type.EARITHMETIC,
                ">>>: ERROR ETYPE WAS NOT ARITHMETIC");
    }
    
    public static SemanticError ERELATIONAL()
    {
        return new SemanticError(Type.ERELATIONAL,
                ">>>: ERROR ETYPE WAS NOT RELATIONAL");
    }
    
    public static SemanticError DUMMY()
    {
        return new SemanticError(Type.DUMMY,
                "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
    }
    
    public static SemanticError NOT(int action, SymbolTableEntry id)
    {
        return new SemanticError(Type.NOT,
                "During action " + action + "the id " + id +
                "was not a constant entry, variable entry, result, or arrayentry");
    }
    
    public static SemanticError NOT_FUNC(int action, SymbolTableEntry id)
    {
        return new SemanticError(Type.NOT_FUNC,
                "During action " + action + "the id " + id +
                "was not a function entry");
    }
    
    public static SemanticError NOT_PROC(int action, SymbolTableEntry id)
    {
        return new SemanticError(Type.NOT_PROC,
                "During action " + action + "the id " + id +
                "was not a procedure entry");
    }
    
    public static SemanticError INCORRECT_PARAM_NUMBER(int pCount, int paramNumber) {
        return new SemanticError(Type.INCORRECT_PARAM_NUMBER,
                pCount + " does not equal " + paramNumber);
    }
    
    public static SemanticError INCORRECT_TYPE(TokenType type1, TokenType type2) {
        return new SemanticError(Type.INCORRECT_PARAM_NUMBER,
                type1 + " does not equal " + type2);
    }
}
