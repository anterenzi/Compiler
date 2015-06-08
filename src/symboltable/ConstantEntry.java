package symboltable;

import lexicalanalyzer.*;

public class ConstantEntry extends SymbolTableEntry {
    
    //ConstantEntry (name, type)
    int address;

    boolean parm;
    boolean functionResult;
    boolean reserved;
    
    //constructor methods:
    
    public ConstantEntry() {
    }

    public ConstantEntry(String Name) {
        super(Name);
    }

    public ConstantEntry(String Name, TokenType type) {
        super(Name, type);
    }

    //I changed my value field in Token from Object to String
    //so some oddities occured, this catches them though.
    public ConstantEntry(int object, TokenType type) {
        super(Integer.toString(object), type);
    }

    public boolean isConstant() {
        return true;
    }

    public void print() {

        System.out.println("Constant Entry:");
        System.out.println("   Name    : " + this.getName());
        System.out.println("   Type    : " + this.getType());
        System.out.println();
    }
    
    public String toString() {
        return "ConstantEntry: " + name + ", " + type;
    }
    
        public boolean isFunctionResult() {
        return functionResult;
    }

    public void setFunctionResult() {
        this.functionResult = true;
    }

    public boolean isParameter() {
        return parm;
    }
    
    public void setAddress(int address)
    {
        this.address = address;
    }
    
    public int getAddress() {
        return address;
    }

    public void setParameter(boolean parm) {
        this.parm = parm;
    }

    public void setParm() {
        this.parm = true;
    }

    public void makeReserved() {
        this.reserved = true;
    }
}
