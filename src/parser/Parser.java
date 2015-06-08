//ANDREW TERENZI
package parser;

//LL(1) top-down parser.

import java.util.*;
import lexicalanalyzer.*;
import errors.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import rhs.*;
import semantic.*;

public class Parser {

    String filename;
    String output;
    Stack parseStack;
    int[][] parseTable;
    public CharStream stream = null;

    public Parser(String file) {
        filename = file;
        //stack is set to accept grammar symbols
        parseStack = new Stack<GrammarSymbol>();
        //parseTable is set to the size of the matrix manually
        parseTable = new int[35][38];
        parseTable = constructParseTable();
    }
    
    public Parser(String string1, String string2, String string3) {
        filename = string1 + string2 + string3;
        output = string2;
        //stack is set to accept grammar symbols
        parseStack = new Stack<GrammarSymbol>();
        //parseTable is set to the size of the matrix manually
        parseTable = new int[35][38];
        parseTable = constructParseTable();
    }

    public void parse() throws CompilerError {
        //stack gets initialized with EOF and GOAL
        parseStack.push(TokenType.ENDOFFILE);
        //run the tokenizer on the parse data!
        Tokenizer tokenizer =
                new Tokenizer(filename);
        //For semantic action
        Token thisToken = tokenizer.GetNextToken();
        //For semantic action
        Token previousToken = null;
        //we don't want the token itself, just the TYPE
        TokenType currentToken = thisToken.getType();
        parseStack.push(NonTerminal.Goal);

        //Initialize Semantic Actions
        SemanticActions semanticActions = new SemanticActions();

        //LL(1) loop continues until the stack is empty
        while (!parseStack.empty()) {

            //uncomment dumpstack to see the stack each iteration!
            //currently this prints the stack out on one line
            //dumpStack();

            //get the top symbol off the stack
            GrammarSymbol predicted;
            predicted = (GrammarSymbol) parseStack.pop();

            //i was getting a weird error with currenttoken being null
            //works fine this way, might have to modify for final project?
            if (predicted.equals(TokenType.ENDOFFILE)) {
                break;
            }

            //check for terminal
            if (predicted.isToken()) {
                //check for match
                if (predicted.equals(currentToken)) {
                    //match! move to the next token
                    previousToken = thisToken;
                    thisToken = tokenizer.GetNextToken();
                    currentToken = thisToken.getType();
                } else if (!predicted.equals(currentToken)) {
                    //no match is an ERROR

                    //if you want to STOP the parser
                    //throw TokenMismatch without "v2" at the end
                    //v2 does a System.out.print without actually throwing
                    //the error so the parser does not stop
                    ParserError.TokenMismatchv2(predicted, currentToken, tokenizer.stream.thisLineNumber());

                    //enter panic mode to try to recover!
                    panicMode(tokenizer, predicted, currentToken);
                }
                //now check for non-terminal
                //note, there is no case for action, so it just gets popped
                //with nothing else happening
            } else if (predicted.isNonTerminal()) {
                //get the entry in the parse table
                int parseEntry = parseTable[currentToken.getIndex()][predicted.getIndex()];

                //check for an error
                if (parseEntry == 999) {
                    //if you want to STOP the parser
                    //throw RHS999 without "v2" at the end
                    //v2 does a System.out.print without actually throwing
                    //the error so the parser does not stop
                    ParserError.RHS999v2(predicted, currentToken, tokenizer.stream.thisLineNumber());

                    //enter panic mode!
                    panicMode(tokenizer, predicted, currentToken);

                    //(if > 0) is b/c a negative entry has an empty RHS
                } else if (parseEntry > 0) {
                    //get the rule from the RHS
                    RHSTable rhsRules = new RHSTable();
                    GrammarSymbol[] thisRule = new GrammarSymbol[67];
                    thisRule = rhsRules.getRule(parseEntry);

                    //note that they get pushed on in REVERSE order
                    for (int i = thisRule.length - 1; i >= 0; i--) {
                        parseStack.push(thisRule[i]);
                    }
                }
            } else if (predicted.isAction()) {
                semanticActions.Execute((SemanticAction) predicted, previousToken);
            }
        }
        String finalString = semanticActions.getQuad().print();
        System.out.println(finalString);
        writeString(finalString);
        //If you want to know what actions were used, uncomment!
        //System.out.println(semanticActions.getActionList());
    }

    public void dumpStack() {
        //System.out.println("The current contents of the stack are:");
        //delete the space and change it to println if you want each
        //item to appear on a new line

        //Stack class extends Vector so I dont' have to make a clone!
        for (int i = parseStack.size() - 1; i >= 0; i--) {
            System.out.print(parseStack.get(i) + " ");
        }
        System.out.println();
    }

    public int[][] constructParseTable() {
        //this method uses CHARSTREAM to make the parsetable
        //while this isn't super efficient, this method allows you to
        //change the parsetable file and have it still work.
        //another option, of course, is to manually copy paste in the 2d
        //array, but I like the flexibility of the charstream method
        int[][] parseTableConstructor = new int[35][38];

        //BUILD ENTRY builds a string until a "BLANK" is hit
        //since that's the start of a new number
        String buildEntry = "";

        //used for the table reference
        int indexX = 0;
        int indexY = 0;

        stream = new CharStream("src/parser/parsetable-2const.dat");
        char c = getChar();
        buildEntry += c;

        //while the file isn't over
        while (c != CharStream.EOF) {

            //if the end of the row has been passed,
            //move onto the next row
            //and set the column to 0
            if (indexY == 38) {
                indexX += 1;
                indexY = 0;
            }

            if (c == CharStream.BLANK) {
                //put the table entry into the correct place
                //(converting the spring to an int)
                parseTableConstructor[indexX][indexY] = Integer.parseInt(buildEntry);
                //reset the string builder
                buildEntry = "";
                //move to the next column
                indexY += 1;

            } else {
                //if it's not a blank, add it to the builder
                buildEntry += c;
            }
            //and of course, getChar() keeps the loop moving
            c = getChar();
        }

        //uncomment the following section if you want to print out the result!

        /*
         for (int i = 0; i < 35; i++) {
         for (int j = 0; j < 38; j++) {
         System.out.print(parseTableConstructor[i][j] + " ");
         }
         System.out.println();
         }
         */

        //return the constructed parse table
        return parseTableConstructor;
    }

    //same method used in tokenizer
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

    public void panicMode(Tokenizer tokenizer, GrammarSymbol predicted, GrammarSymbol currentToken) throws CompilerError {
        //this is following heuristics 1-5 of the book

        //if it's a token, then there was a token mismatch
        //rather than doing two separate panic modes, I'm just running
        //the predicate again (heuristic 5)
        if (predicted.isToken()) {
            //if there is a mismatch, move onto the next token
            //since the symbol has already been popped
            tokenizer.GetNextToken();
            //otherwise, the error is thrown because of a RHS rule
        } else {
            //get the parse table entry
            int parseEntry = parseTable[currentToken.getIndex()][predicted.getIndex()];
            //IF a nonterminal generates an empty string, then that can
            //be used a default
            //entries with negative have an empty string on the RHS
            //THUS, check to see if the parse entry is negative
            //and if so, pass in RULE 9 because it's the empty transition
            //(heuristic 4)
            if (parseEntry < 0) {
                RHSTable rhsRules = new RHSTable();
                GrammarSymbol[] thisRule = new GrammarSymbol[67];
                thisRule = rhsRules.getRule(9);
                parseStack.push(thisRule);
            } else {
                //otherwise, we want to look for the token in the
                //synchronizing set.
                //this comprises of follow(a), first(a), and symbols
                //in the hierarchal structure (heuristic 1 & 3)

                //following is an oversimplified version
                //ideally this while loop would be
                //while (currentToken != a member of the synchronizing set)
                //but I'm having trouble implementing that

                //basic version of heuristic 2
                while (currentToken != TokenType.SEMICOLON
                        && currentToken != TokenType.ENDOFFILE) {
                    currentToken = tokenizer.GetNextToken().getType();
                }

                //at first I did not have this section
                //BUT in reponse to your reponse to tim's email,
                //I realized that it wasn't enough for the next token
                //to be a semicolon, it had to MATCH to one on the stack

                while (predicted != TokenType.SEMICOLON
                        && predicted != TokenType.ENDOFFILE) {
                    predicted = (GrammarSymbol) parseStack.pop();
                }
            }
        }
    }

    private void writeString(String finalString) {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter("src/tvi/" + output + ".tvi"));
            writer.write(finalString);

        } catch (IOException e) {
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
            }
        }
    }
}