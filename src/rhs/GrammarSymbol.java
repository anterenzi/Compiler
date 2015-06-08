package rhs;

public interface GrammarSymbol 
{
	boolean isToken();
	boolean isNonTerminal();
	boolean isAction();
        int getIndex();
}