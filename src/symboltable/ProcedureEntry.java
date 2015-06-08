package symboltable;

import java.util.LinkedList;
import lexicalanalyzer.*;

public class ProcedureEntry extends SymbolTableEntry {

    //FunctionEntry (name, numberOfParameters, parameterInfo, result)
    int numberOfParameters;
    LinkedList<ParmInfo> parameterInfo;

    boolean parm;
    boolean functionResult;
    boolean reserved;

    //constructor methods:
    public ProcedureEntry() {
    }

    public ProcedureEntry(String Name) {
        super(Name);
        parameterInfo = new LinkedList<ParmInfo>();
    }
    
    public ProcedureEntry(String Name, int paramNum) {
        super(Name);
        numberOfParameters = paramNum;
        parameterInfo = new LinkedList<ParmInfo>();
    }

    public ProcedureEntry(String Name, int paramNum, ParmInfo paramInfo) {
        super(Name);
        numberOfParameters = paramNum;
        parameterInfo = new LinkedList<ParmInfo>();
        parameterInfo.add(paramInfo);
    }

    public boolean isProcedure() {
        return true;
    }
    
    public void addParm(ParmInfo paramInfo)
    {
        parameterInfo.add(paramInfo);
    }

    public void print() {
        System.out.println("Procedure Entry:");
        System.out.println("   Name    : " + this.getName());
        System.out.println("   Number of Parameters    : " + this.getParamNumber());
        //System.out.println("   Parameter Info    : " + this.getParamInfo());
        System.out.println();
    }

    public int getParamNumber() {
        return numberOfParameters;
    }
    
    public LinkedList<ParmInfo> getParmInfo()
    {
        return parameterInfo;
    }

    public String toString() {
        return "ProcedureEntry: " + name + ", " + type;
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
