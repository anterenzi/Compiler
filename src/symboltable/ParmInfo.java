/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package symboltable;

import lexicalanalyzer.TokenType;

public class ParmInfo {
    
    TokenType type;
    boolean isArray;
    int upperbound;
    int lowerbound;
    
    public ParmInfo()
    {
        
    }
    
    public ParmInfo(TokenType initType, boolean array)
    {
       type = initType;
       isArray = array;
    }
    
    public ParmInfo(TokenType initType, boolean array, int upper, int lower)
    {
       type = initType;
       isArray = array;
       upperbound = upper;
       lowerbound = lower;
    }
    
    public TokenType getType()
    {
        return type;
    }
    
    public boolean isArray()
    {
        return isArray;
    }
    
    public int getUpper()
    {
        return upperbound;
    }
    
    public int getLower()
    {
        return lowerbound;
    }
}
