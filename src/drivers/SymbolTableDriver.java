//ANDREW TERENZI
//Driver class for the SymbolTable

package drivers;

import errors.LexicalError;
import lexicalanalyzer.*;
import symboltable.*;

public class SymbolTableDriver {

    public SymbolTableDriver() {
        super();
    }

    protected void run(String filename) {
        SymbolTable KeywordTable = new SymbolTable(17);
        SymbolTable GlobalTable = new SymbolTable(37);
        SymbolTable ConstantTable = new SymbolTable(37);

        //The global table initially contains the names of all built-in
        //functions (in our case, read and write, and the (internal)
        //program name main).
        GlobalTable.insert(new FunctionEntry("read"));
        GlobalTable.insert(new FunctionEntry("write"));
        GlobalTable.insert(new FunctionEntry("main"));

        //"Optionally, you may use the symbol table class
        //to define a keyword table containing all the keywords."
        KeywordTable.insert(new SymbolTableEntry("program", TokenType.PROGRAM));
        KeywordTable.insert(new SymbolTableEntry("begin", TokenType.BEGIN));
        KeywordTable.insert(new SymbolTableEntry("end", TokenType.END));
        KeywordTable.insert(new SymbolTableEntry("var", TokenType.VAR));
        KeywordTable.insert(new SymbolTableEntry("function", TokenType.FUNCTION));
        KeywordTable.insert(new SymbolTableEntry("procedure", TokenType.PROCEDURE));
        KeywordTable.insert(new SymbolTableEntry("result", TokenType.RESULT));
        KeywordTable.insert(new SymbolTableEntry("integer", TokenType.INTEGER));
        KeywordTable.insert(new SymbolTableEntry("real", TokenType.REAL));
        KeywordTable.insert(new SymbolTableEntry("array", TokenType.ARRAY));
        KeywordTable.insert(new SymbolTableEntry("of", TokenType.OF));
        KeywordTable.insert(new SymbolTableEntry("if", TokenType.IF));
        KeywordTable.insert(new SymbolTableEntry("then", TokenType.THEN));
        KeywordTable.insert(new SymbolTableEntry("else", TokenType.ELSE));
        KeywordTable.insert(new SymbolTableEntry("while", TokenType.WHILE));
        KeywordTable.insert(new SymbolTableEntry("do", TokenType.DO));
        KeywordTable.insert(new SymbolTableEntry("not", TokenType.NOT));

        Tokenizer tokenizer =
                new Tokenizer(filename);

        Token token;

        try {
            token = tokenizer.GetNextToken();

            while (!(token.getType() == TokenType.ENDOFFILE)) {

                if ((token.getType() == TokenType.INTCONSTANT) || (token.getType() == TokenType.REALCONSTANT)) {
                    // If the token is a constant, add it to constantTable
                    ConstantTable.insert(new ConstantEntry(token.getValue(), token.getType()));
                } else if (token.getType() == TokenType.IDENTIFIER) {

                    //  If it is an identifier add it to Global table
                    // as a variable entry
                    GlobalTable.insert(new VariableEntry(token.getValue(), token.getType()));

                }
                token = tokenizer.GetNextToken();
            }
        } catch (LexicalError ex) {
            System.err.println(ex);
        }

        System.out.println("Printing out keyword table:");
        KeywordTable.dumpTable();
        System.out.println("Printing the global table:");
        GlobalTable.dumpTable();
        System.out.println("Printing the constant table");
        ConstantTable.dumpTable();
    }

    public static void main(String[] args) {
        SymbolTableDriver test = new SymbolTableDriver();
        test.run("src/symboltable/symtabtest.dat");
    }
}