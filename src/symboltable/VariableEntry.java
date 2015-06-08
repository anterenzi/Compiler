package symboltable;

import lexicalanalyzer.*;

public class VariableEntry extends SymbolTableEntry {

    //VariableEntry (name, address, type)
    int address;
    
    boolean parm;
    boolean functionResult;
    boolean reserved;
    
    //constructor methods:
    public VariableEntry() {
    }

    public VariableEntry(String Name) {
        super(Name);
    }

    public VariableEntry(String Name, TokenType type) {
        super(Name, type);
    }

    //I was getting a strange error about not having an Object constructor
    //Probably because of something in earlier code...
    public VariableEntry(Object object, TokenType type) {
        super((String) object, type);
    }

    public boolean isVariable() {
        return true;
    }

    public void print() {

        System.out.println("Variable Entry:");
        System.out.println("   Name    : " + this.getName());
        System.out.println("   Type    : " + this.getType());
        System.out.println("   Address : " + this.getAddress());
        System.out.println();
    }

    //accessor method for the address
    public int getAddress() {
        return address;
    }

    //sets the address
    public void setAddress(int address) {
        this.address = address;
    }
    
    public String toString() {
        return "VariableEntry: " + name + ", " + type;
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
