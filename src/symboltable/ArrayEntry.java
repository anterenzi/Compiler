package symboltable;

import lexicalanalyzer.*;

public class ArrayEntry extends SymbolTableEntry {

    //ArrayEntry (name, address, type, upperBound, lowerBound)
    int address;
    int upperBound;
    int lowerBound;

    boolean parm;
    boolean functionResult;
    boolean reserved;
    
    //constructor methods:
    
    public ArrayEntry() {
    }

    public ArrayEntry(String Name) {
        super(Name);
    }

    public ArrayEntry(String Name, TokenType type) {
        super(Name, type);
    }

    public ArrayEntry(String Name, TokenType type,
            int upper, int lower) {
        super(Name, type);
        upperBound = upper;
        lowerBound = lower;
    }

    public boolean isArray() {
        return true;
    }

    public void print() {

        System.out.println("Array Entry:");
        System.out.println("   Name    : " + this.getName());
        System.out.println("   Type    : " + this.getType());
        System.out.println("   Address : " + this.getAddress());
        System.out.println("   UpperBound    : " + this.getUpper());
        System.out.println("   LowerBound    : " + this.getLower());
        System.out.println();
    }

    public int getAddress() {
        return address;
    }

    public void setAddress(int address) {
        this.address = address;
    }

    public int getUpper() {
        return upperBound;
    }

    public int getLower() {
        return lowerBound;
    }
    
    public String toString() {
        return "ArrayEntry: " + name + ", " + type;
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
