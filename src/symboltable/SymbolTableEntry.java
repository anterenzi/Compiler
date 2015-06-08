//ANDREW TERENZI

package symboltable;

import java.util.LinkedList;
import lexicalanalyzer.*;

public class SymbolTableEntry {

    //Not every entry needs to have a type according to the list
    //of subclasses on the assignment, but the example code has
    //"super(Name, type);" in VariableEntry, so I'm letting
    //all of the subclasses inherit the type too.
    //If it stays null, well, I guess that's fine.
    String name;
    TokenType type;

    //constructor methods:
    
    public SymbolTableEntry() {
    }

    public SymbolTableEntry(String newName) {
        name = newName;
    }

    public SymbolTableEntry(String newName, TokenType newType) {
        name = newName;
        type = newType;
    }

    //accessor methods:
    
    public String getName() {
        return name;
    }

    public TokenType getType() {
        return type;
    }
    
    public void setType(TokenType newType) {
        type = newType;
    }
    
    public int getAddress() {
        return 9999;
    }
    
    //sets the address
    public void setAddress(int address) {
        //will be overridden
    }
    
    public void setNumParam(int num)
    {
        //override
    }
    
    public void addParm(ParmInfo paramInfo)
    {
        //override
    }
    
    public String toString() {
        return "SymbolTableEntry: " + name + ", " + type;
    }
    
    public LinkedList<ParmInfo> getParmInfo()
    {
        return null;
    }

    public int getParamNumber() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public boolean isFunctionResult() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public int getUpper() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public int getLower() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public String getResult() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public boolean isParameter() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
