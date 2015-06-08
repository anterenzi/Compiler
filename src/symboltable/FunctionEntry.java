package symboltable;

import lexicalanalyzer.*;
import java.util.LinkedList;

public class FunctionEntry extends SymbolTableEntry {

    //FunctionEntry (name, numberOfParameters, parameterInfo, result)
    int numberOfParameters;
    LinkedList<ParmInfo> parameterInfo;
    String result;

    boolean parm;
    boolean functionResult;
    boolean reserved;

    //constructor methods: 
    public FunctionEntry() {
    }

    public FunctionEntry(String Name) {
        super(Name);
        parameterInfo = new LinkedList<ParmInfo>();
    }
    
    public FunctionEntry(String Name, int paramNum, String rezult) {
        super(Name);
        numberOfParameters = paramNum;
        parameterInfo = new LinkedList<ParmInfo>();
        result = rezult;
    }

    public FunctionEntry(String Name, int paramNum,
            ParmInfo paramInfo, String rezult) {
        super(Name);
        numberOfParameters = paramNum;
        parameterInfo = new LinkedList<ParmInfo>();
        parameterInfo.add(paramInfo);
        result = rezult;
    }

    public boolean isFunction() {
        return true;
    }

    public void print() {

        System.out.println("Function Entry:");
        System.out.println("   Name    : " + this.getName());
        System.out.println("   Number of Parameters    : " + this.getParamNumber());
        //System.out.println("   Parameter Info    : " + this.getParamInfo());
        System.out.println("   Result    : " + this.getResult());
        System.out.println();
    }

    public int getParamNumber() {
        return numberOfParameters;
    }
    
    public LinkedList<ParmInfo> getParmInfo()
    {
        return parameterInfo;
    }
    
    public void addParm(ParmInfo paramInfo)
    {      
        parameterInfo.add(paramInfo);
    }
    
    public void setNumParam(int num)
    {
        numberOfParameters = num;
    }

    public String getResult() {
        return result;
    }
    
    public void setResult(String passedResult)
    {
        result = passedResult;
    }
    
    public void setResult(SymbolTableEntry passedResult)
    {
        result = passedResult.getName();
    }

    public String toString() {
        return "FunctionEntry: " + name + ", " + type;
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
