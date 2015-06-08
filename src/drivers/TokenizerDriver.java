//ANDREW TERENZI
//Driver class for the Tokenizer
//Run on "lextest" or "parsetest" to see result.
//Can modify these tests.

package drivers;

import errors.LexicalError;
import lexicalanalyzer.*;

public class TokenizerDriver {

    public TokenizerDriver() {
        super();
    }

    protected void run() {
        Tokenizer tokenizer =
                new Tokenizer("src/drivers/parsetest.dat");
        Token token = new Token();

        try {
            token = tokenizer.GetNextToken();
        } catch (LexicalError ex) {
            System.out.println(ex);
        }

        while (!(token.getType() == TokenType.ENDOFFILE)) {

            //errors return null, so throw that token away
            if (token.getType() == null) {
                try {
                    token = tokenizer.GetNextToken();
                } catch (LexicalError ex) {
                    System.out.println(ex);
                }
                continue;
            }

            System.out.print("Recognized Token:  " + token.getType());
            if ((token.getType() == TokenType.IDENTIFIER) || (token.getType() == TokenType.REALCONSTANT)
                    || (token.getType() == TokenType.INTCONSTANT)) {
                System.out.print(" Value : " + token.getValue());
            } else if ((token.getType() == TokenType.RELOP)
                    || (token.getType() == TokenType.ADDOP) || (token.getType() == TokenType.MULOP)) {
                System.out.print(" OpType : " + token.getOpType());
            }
            System.out.println();

            try {
                token = tokenizer.GetNextToken();
            } catch (LexicalError ex) {
                System.out.println(ex);
            }
        }
        if (token.getType() == TokenType.ENDOFFILE) {
            System.out.println("ENDOFFILE");
        }
    }

    public static void main(String[] args) {
        TokenizerDriver test = new TokenizerDriver();
        test.run();
    }
}
