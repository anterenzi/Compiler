//ANDREW TERENZI
package lexicalanalyzer;

//Creates tokens from an input file
//using a finite state machine.

import errors.*;

public class Tokenizer {

    String filename;
    public CharStream stream;
    //lastToken is used for UNARY vs ADDOP
    TokenType lastToken;

    public Tokenizer(String fyle) {
        filename = fyle;
        stream = new CharStream(filename);
        lastToken = null;
    }

    //essentially copied from CharStreamDriver
    public char getChar() {
        char ch = (char) CharStream.EOF;
        boolean done = false;
        while (!done) {
            try {
                ch = stream.currentChar();
                done = true;
            } catch (LexicalError ex) {
                System.out.println(ex);
            }
        }
        return ch;
    }

    //returns tokens to TokenizerDriver
    //structure based off non-table driven solution
    //from the slides
    public Token GetNextToken() throws LexicalError {
        Token token = new Token();
        char c = getChar();
        if (c == CharStream.EOF) {
            return new Token(TokenType.ENDOFFILE);
        }
        if (c == CharStream.BLANK) {
            //System.out.println("Next character is : BLANK");
            c = getChar();
        }
        //System.out.println("Current char: " + c);
        //try {
            if (Character.isLetter(c)) {
                token = readIdentifier(c);
            } else if (Character.isDigit(c)) {
                token = readNumber(c);
            } else {
                token = readSymbol(c);
            }
        //} catch (LexicalError ex) {
        //    System.out.println(ex);
        //}

        //record the tokentype
        lastToken = token.getType();

        return token;
    }

    //based off readIdentifier from the slides
    public Token readIdentifier(char c) throws LexicalError {
        //builds a string to keep track of lexeme
        String x = "";
        int count = 0;
        boolean toolong = false;

        //loops for letters and digits
        while (is_letter_or_digit(c)) {
            //keeps count so long identifiers make an error
            count++;
            if (count > 50) {
                toolong = true;
            }

            //continue forward
            x += c;
            c = getChar();
        }
        if (!(c == CharStream.BLANK)) {
            stream.pushBack(c);
        }

        //identifier is too long? toss that dud out!!
        if (toolong) {
            throw LexicalError.TooLongIdentifier(x);
        }

        //if the identifier is actually a keyword
        //return that token.
        //otherwise, build a new identifier token
        Token token = checkKeyword(x);
        if (token == null) {
            token = new Token(TokenType.IDENTIFIER, x);
        }
        return token;
    }

    public Token readNumber(char c) throws LexicalError {
        //builds a string to keep track of lexeme
        String x = "";
        int count = 0;
        boolean toolong = false;

        //at this point only accepts digit, period, or e
        while (is_digit(c) || is_period(c) || is_e(c)) {

            //keeps count so long identifiers make an error
            count++;
            if (count > 20) {
                toolong = true;
            }

            //continue forward
            x += c;
            c = getChar();

            //next char is e, build a real with e
            if (is_e(c)) {
                c = getChar();
                return readRealE(c, x);
            }

            //next char is a dot
            if (is_period(c)) {

                c = getChar();

                //is there another dot? DOUBLE DOT!!
                if (is_period(c)) {
                    //pushback twice and make the intconstant
                    stream.pushBack(c);
                    stream.pushBack(c);
                    Token token = new Token(TokenType.INTCONSTANT, Integer.parseInt(x));
                    return token;
                }
                //is there another digit after one dot?
                if (is_digit(c)) {
                    return readReal(c, x);
                } //is there something else??? uh oh...
                else {
                    //there should be a digit after the decimal!
                    throw LexicalError.NoDigitAfterDecimal(x);
                }
            }
        }
        if (!(c == CharStream.BLANK)) {
            stream.pushBack(c);
        }

        //is it too long? throw that out!!
        if (toolong) {
            throw LexicalError.TooLongConstant(x);
        }

        //create and return the int constant
        Token token = new Token(TokenType.INTCONSTANT, Integer.parseInt(x));
        return token;
    }

    //form a realconstant that uses a dot (ex. 123.356)
    public Token readReal(char c, String x) throws LexicalError {
        int count = 0;
        boolean toolong = false;

        //starting a real, manually add period
        x += ".";

        //only accepts digit or period
        while (is_digit(c) || is_period(c)) {

            //ideally this would start from the old count, not 0
            //but it's not that big of a deal
            count++;
            if (count > 20) {
                toolong = true;
            }

            //continue forward
            x += c;
            c = getChar();

            if (is_period(c)) {

                //is there another dot? DOUBLE DOT!! (within a real)
                c = getChar();
                if (is_period(c)) {
                    stream.pushBack(c);
                    stream.pushBack(c);
                    Token token = new Token(TokenType.REALCONSTANT, x);
                    return token;
                }
            }
        }
        if (!(c == CharStream.BLANK)) {
            stream.pushBack(c);
        }

        //is it too long? throw that out!!
        if (toolong) {
            throw LexicalError.TooLongConstant(x);
        }

        //create the realconstant and return it
        Token token = new Token(TokenType.REALCONSTANT, x);
        return token;
    }

    //form a realconstant that uses e notation
    public Token readRealE(char c, String x) throws LexicalError {
        int count = 0;
        boolean toolong = false;

        //manually add the e
        x += "e";

        //only accepts digit or period
        while (is_digit(c) || is_period(c)) {

            count++;
            if (count > 20) {
                toolong = true;
            }

            //a period at this point is an ill formed constant
            //BUT!! unsure if it should still look for a double dot
            //couldn't find anything in the examples so for now
            //I'll just have it return an errror either way
            if (is_period(c)) {
                throw LexicalError.IllFormedConstant(x);
            }

            //continue forward
            x += c;
            c = getChar();

        }
        if (!(c == CharStream.BLANK)) {
            stream.pushBack(c);
        }

        //is it too long? throw that out!!
        if (toolong) {
            throw LexicalError.TooLongConstant(x);
        }

        //create the realconstant and return it
        Token token = new Token(TokenType.REALCONSTANT, x);
        return token;
    }

    public Token readSymbol(char c) {
        //uses a switch statement to go through all possible symbols
        //uses a one character look ahead/push back for symbols that need it
        //ex: < could be either < or the start of <=

        String x2 = "";
        String x = c + "";
        switch (x) {
            case "+":
                //plus/minus use the lastToken method to help determine
                //if it's an ADDOP or UNARY
                if (lastToken()) {
                    return new Token(TokenType.ADDOP, 1);
                } else {
                    return new Token(TokenType.UNARYPLUS);
                }
            case "-":
                if (lastToken()) {
                    return new Token(TokenType.ADDOP, 2);
                } else {
                    return new Token(TokenType.UNARYMINUS);
                }
            case "*":
                return new Token(TokenType.MULOP, 1);
            case "/":
                return new Token(TokenType.MULOP, 2);
            case "=":
                return new Token(TokenType.RELOP, 1);
            case ":":
                c = getChar();
                x2 = c + "";
                if (x2.equals("=")) {
                    return new Token(TokenType.ASSIGNOP);
                }
                stream.pushBack(c);
                return new Token(TokenType.COLON);
            case ";":
                return new Token(TokenType.SEMICOLON);
            case ",":
                return new Token(TokenType.COMMA);
            case "(":
                return new Token(TokenType.LEFTPAREN);
            case ")":
                return new Token(TokenType.RIGHTPAREN);
            case "[":
                return new Token(TokenType.LEFTBRACKET);
            case "]":
                return new Token(TokenType.RIGHTBRACKET);
            case ".":
                c = getChar();
                x2 = c + "";
                if (x2.equals(".")) {
                    return new Token(TokenType.DOUBLEDOT);
                }
                stream.pushBack(c);
                return new Token(TokenType.ENDMARKER);
            case "<":
                c = getChar();
                x2 = c + "";
                if (x2.equals(">")) {
                    return new Token(TokenType.RELOP, 2);
                }
                if (x2.equals("=")) {
                    return new Token(TokenType.RELOP, 5);
                }
                stream.pushBack(c);
                return new Token(TokenType.RELOP, 3);
            case ">":
                c = getChar();
                x2 = c + "";
                if (x2.equals("=")) {
                    return new Token(TokenType.RELOP, 6);
                }
                stream.pushBack(c);
                return new Token(TokenType.RELOP, 4);
            default:
                return new Token();
        }
    }

    //check keyword checks the lexeme of an identifier before
    //the token is formed to see if it matches a keyword,
    //in which case a keyword is made instead of an identifier
    public Token checkKeyword(String x) {
        x = x.toLowerCase();

        switch (x) {

            //ALSO DEAL WITH OR, DIV, MOD, AND
            //which are not keywords, but needs an exact string sequence
            //to match

            case "or":
                return new Token(TokenType.ADDOP, 3);
            case "div":
                return new Token(TokenType.MULOP, 3);
            case "mod":
                return new Token(TokenType.MULOP, 4);
            case "and":
                return new Token(TokenType.MULOP, 5);

            //list of keywords:
            case "program":
                return new Token(TokenType.PROGRAM);
            case "begin":
                return new Token(TokenType.BEGIN);
            case "end":
                return new Token(TokenType.END);
            case "var":
                return new Token(TokenType.VAR);
            case "function":
                return new Token(TokenType.FUNCTION);
            case "procedure":
                return new Token(TokenType.PROCEDURE);
            case "result":
                return new Token(TokenType.RESULT);
            case "integer":
                return new Token(TokenType.INTEGER);
            case "real":
                return new Token(TokenType.REAL);
            case "array":
                return new Token(TokenType.ARRAY);
            case "of":
                return new Token(TokenType.OF);
            case "if":
                return new Token(TokenType.IF);
            case "then":
                return new Token(TokenType.THEN);
            case "else":
                return new Token(TokenType.ELSE);
            case "while":
                return new Token(TokenType.WHILE);
            case "do":
                return new Token(TokenType.DO);
            case "not":
                return new Token(TokenType.NOT);
            default:
                return null;
        }
    }

    //copied all of the methods over from character classifier
    //not all are used but leaving in case i tweak things later
    public static boolean is_end_of_input(char ch) {
        return ((ch) == '\0');
    }

    public static boolean is_layout(char ch) {
        return (!is_end_of_input(ch) && (ch) <= ' ');
    }

    public static boolean is_comment_starter(char ch) {
        return ((ch) == '{');
    }

    public static boolean is_comment_stopper(char ch) {
        return ((ch) == '}');
    }

    public static boolean is_uc_letter(char ch) {
        return ('A' <= (ch) && (ch) <= 'Z');
    }

    public static boolean is_lc_letter(char ch) {
        return ('a' <= (ch) && (ch) <= 'z');
    }

    public static boolean is_letter(char ch) {
        return (is_uc_letter(ch) || is_lc_letter(ch));
    }

    public static boolean is_digit(char ch) {
        return ('0' <= (ch) && (ch) <= '9');
    }

    public static boolean is_letter_or_digit(char ch) {
        return (is_letter(ch) || is_digit(ch));
    }

    public static boolean is_operator(char ch) {
        String c = ch + "";
        return (c.equals("+") || c.equals("-")
                || c.equals("*") || c.equals("/"));
    }

    public static boolean is_separator(char ch) {
        String c = ch + "";
        return (c.equals(",") || c.equals(";")
                || c.equals("(") || c.equals(")"));
    }

    public static boolean is_blank(char ch) {
        String c = ch + "";
        return (c.equals(" "));
    }

    public static boolean is_delim(char ch) {
        return (is_operator(ch) || is_separator(ch) || is_blank(ch));
    }

    //added classification
    public static boolean is_period(char ch) {
        String c = ch + "";
        return c.equals(".");
    }

    //checks for letter "e" in realnumber to see if it should be
    //a real instead of an intconstant
    public boolean is_e(char ch) {
        String c = ch + "";
        c = c.toLowerCase();
        return (c.equals("e"));
    }

    //lastToken is used while dealing with + and -
    public boolean lastToken() {
        return (lastToken == TokenType.RIGHTBRACKET || lastToken == TokenType.RIGHTPAREN
                || lastToken == TokenType.IDENTIFIER || lastToken == TokenType.INTCONSTANT
                || lastToken == TokenType.REALCONSTANT);
    }
}
