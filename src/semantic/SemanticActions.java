package semantic;

import java.lang.*;
import errors.*;
import java.util.*;
import lexicalanalyzer.*;
import parser.*;
import symboltable.*;
import drivers.*;
import rhs.*;

/**
 * Contains all the semantic actions
 * (dispersed throughout the grammar)
 * 
 * Outputs TVI code (code for "The Vassar Interpreter")
 * The ParserDriver calls various semantic actions.
 * 
 * Pseudocode was provided for the semantic actions.
 */

public class SemanticActions {

    private Stack<Object> semanticStack;
    private Quadruples quads;
    private boolean insert;
    private boolean isArray;
    private boolean global;
    private int globalMemory;
    private int localMemory;
    private int tableSize = 100;
    private int global_store;
    private int local_store;
    private LinkedList etrueX;
    private LinkedList efalseX;
    private LinkedList skip_elseX;
    private SymbolTable globalTable;
    private SymbolTable constantTable;
    private SymbolTable localTable;
    private SymbolTableEntry currentFunction;
    private Stack<Integer> parmcount;
    private Stack<LinkedList<ParmInfo>> nextparm;
    private Stack<Object> helpStack;
    private int tempCount;
    //used for debugging purposes
    private HashSet<Integer> actionList;

    public SemanticActions() {
        semanticStack = new Stack<Object>();
        quads = new Quadruples();
        insert = false;
        isArray = false;
        // isParm = false;
        global = true;
        globalMemory = 0;
        localMemory = 0;
        global_store = 0;
        local_store = 0;
        globalTable = new SymbolTable(tableSize);
        constantTable = new SymbolTable(tableSize);
        InstallBuiltins(globalTable);
        currentFunction = null;
        parmcount = new Stack<Integer>();
        nextparm = new Stack<LinkedList<ParmInfo>>();
        helpStack = new Stack<Object>();
        tempCount = 0;
        actionList = new HashSet<Integer>();
    }

    public void Execute(SemanticAction action, Token token) throws SemanticError {


        int actionNumber = action.getIndex();
        actionList.add(actionNumber);

        System.out.println("calling action : " + actionNumber + " with token " + token.getType()
                + " with value " + token.getValue());

        switch (actionNumber) {

            case 1: {
                //INSERT/SEARCH = INSERT
                insert = true;
                break;
            }
            case 2: {
                //INSERT/SEARCH = SEARCH
                insert = false;
                break;
            }
            case 3: {
                //TYP = pop TYPE
                Token newToken = (Token) semanticStack.pop();
                TokenType TYP = newToken.getType();

                //IF ARRAY/SIMPLE = ARRAY
                if (isArray) {
                    //UB & LB = pop CONSTANT
                    Token upperBound = (Token) semanticStack.pop();
                    Token lowerBound = (Token) semanticStack.pop();
                    int intUB = Integer.parseInt(upperBound.getValue());
                    int intLB = Integer.parseInt(lowerBound.getValue());

                    //MSIZE = (UB - LB) + 1
                    int msize = intUB - intLB + 1;

                    //FOR EACH ID ON THE SEMANTIC STACK
                    while (!semanticStack.isEmpty()
                            && semanticStack.peek() instanceof Token) {
                        //ID = pop ID
                        Token currentID = (Token) semanticStack.pop();
                        String idValue = currentID.getValue();

                        //Make the Array Entry
                        ArrayEntry newEntry = new ArrayEntry(idValue, TYP, intUB, intLB);
                        insertHelper(newEntry);

                        //id^.type = TYP 
                        currentID.setType(TYP);
                        //Edit the memory of the entries
                        memoryHelper(newEntry, msize);
                    }
                } else {
                    //Repeat above code except for a VARIABLE ENTRY
                    //instead of an ARRAY ENTRY
                    while (!semanticStack.isEmpty()
                            && semanticStack.peek() instanceof Token) {

                        //ID = pop id
                        Token currentID = (Token) semanticStack.pop();
                        String idValue = currentID.getValue();

                        //Create a new variable entry
                        VariableEntry newEntry = new VariableEntry(idValue, TYP);
                        //inserr the new entry
                        insertHelper(newEntry);

                        //id^.type = TYP
                        currentID.setType(TYP);
                        //modify the memory
                        memoryHelper(newEntry);
                    }
                }

                isArray = false;

                break;
            }


            case 4: {
                //push TYPE
                semanticStack.push(token);
                break;
            }

            case 5: {
                insert = false;
                SymbolTableEntry id = (SymbolTableEntry) semanticStack.pop();
                generate("PROCBEGIN", id.getName());
                local_store = quads.getNextQuad();
                generate("alloc", "_");
                break;
            }

            case 6: {
                //ARRAY/SIMPLE = ARRAY
                isArray = true;
                break;
            }
            case 7: {
                //push CONSTANT
                semanticStack.push(token);
                break;
            }

            case 9: {
                //for each id on the semantic stack
                //isnert id in symbol table
                while (!semanticStack.isEmpty()
                        && semanticStack.peek() instanceof Token) {
                    Token newToken = (Token) semanticStack.pop();
                    VariableEntry newEntry = new VariableEntry(
                            newToken.getValue(), newToken.getType());
                    insertHelper(newEntry);
                }
                insert = false;
                generate("CODE");
                generate("call", "main", "" + 0);
                generate("exit");
                break;
            }

            case 11: {
                global = true;
                localTable.deleteAll();
                currentFunction = null;
                quads.setField(local_store, 1, localMemory);
                generate("free", "" + localMemory);
                generate("PROCEND");
                break;
            }

            case 13: {
                //push ID
                semanticStack.push(token);
                break;
            }

            case 15: {
                FunctionEntry id = new FunctionEntry(token.getValue());
                insertHelper(id);
                semanticStack.push(id);
                SymbolTableEntry $$fun_name = create("fun_name", TokenType.INTEGER);
                id.setResult($$fun_name);
                global = false;
                localTable = new SymbolTable(tableSize);
                localMemory = 0;
                break;
            }

            case 16: {
                Token type = (Token) semanticStack.pop();
                SymbolTableEntry id = (SymbolTableEntry) semanticStack.peek();
                
                id.setType(type.getType());
                SymbolTableEntry fun_name = (SymbolTableEntry) semanticStack.peek();
                fun_name.setType(type.getType());
                currentFunction = id;
                
                break;
            }

            case 17: {
                ProcedureEntry id = new ProcedureEntry(token.getValue());
                insertHelper(id);
                memoryHelper(id);
                semanticStack.push(id);
                global = false;
                localTable = new SymbolTable(tableSize);
                localMemory = 0;
                break;
            }

            case 19: {
                parmcount.push(0);
                break;
            }

            case 20: {
                SymbolTableEntry id = (SymbolTableEntry) semanticStack.get(0);
                int parm = parmcount.pop();
                id.setNumParam(parm);
                break;
            }

            case 21: {
                Token type = (Token) semanticStack.pop();
                SymbolTableEntry proc = (SymbolTableEntry) semanticStack.get(0);

                while (semanticStack.peek() instanceof Token) {
                    if (isArray) {
                        ArrayEntry newArray;
                        Token constant1 = (Token) semanticStack.pop();
                        Token constant2 = (Token) semanticStack.pop();
                        Token topStack = (Token) semanticStack.pop();
                        int upper = Integer.parseInt(constant1.getValue());
                        int lower = Integer.parseInt(constant2.getValue());

                        newArray = new ArrayEntry(topStack.getValue(), type.getType(),
                                upper, lower);
                        newArray.setParm();
                        ParmInfo thisparm = new ParmInfo(type.getType(), true, upper, lower);
                        proc.addParm(thisparm);
                        insertHelper(newArray);

                    } else {
                        Token topStack = (Token) semanticStack.pop();

                        VariableEntry var = new VariableEntry(topStack.getValue(), type.getType());
                        var.setParm();
                        ParmInfo thisparm = new ParmInfo(type.getType(), false);
                        proc.addParm(thisparm);
                        insertHelper(var);
                    }
                    proc.setAddress(localMemory);
                    localMemory += 1;
                    proc.setType(type.getType());
                    int parm = parmcount.pop();
                    parm += 1;
                    parmcount.push(parm);
                }

                isArray = false;
                break;
            }

            case 22: {
                etypeRelationalHelper();
                backpatch(etrueX, quads.getNextQuad());
                break;
            }

            case 24: {
                int beginloop = quads.getNextQuad();
                semanticStack.push(beginloop);
                break;
            }

            case 25: {
                etypeRelationalHelper();
                backpatch(etrueX, quads.getNextQuad());
                break;
            }

            case 26: {
                if (semanticStack.peek() instanceof ETYPE) {
                    semanticStack.pop();
                }

                efalseX = (LinkedList) semanticStack.pop();
                etrueX = (LinkedList) semanticStack.pop();
                int beginloop = (int) semanticStack.pop();

                generate("goto " + beginloop);
                backpatch(efalseX, quads.getNextQuad());
                break;
            }

            case 27: {
                skip_elseX = makelist(quads.getNextQuad());
                semanticStack.push(skip_elseX);
                generate("goto", "_");
                backpatch(efalseX, quads.getNextQuad());
                break;
            }

            case 28: {
                skip_elseX = (LinkedList) semanticStack.pop();
                efalseX = (LinkedList) semanticStack.pop();
                etrueX = (LinkedList) semanticStack.pop();
                backpatch(skip_elseX, quads.getNextQuad());
                break;
            }

            case 29: {
                //HACK FIX
                if (semanticStack.peek() instanceof ETYPE) {
                    semanticStack.pop();
                }

                efalseX = (LinkedList) semanticStack.pop();
                etrueX = (LinkedList) semanticStack.pop();
                backpatch(efalseX, quads.getNextQuad());
                break;
            }

            case 30: {
                SymbolTableEntry newEntry = null;
                newEntry = lookupHelper(token);
                //Not found?? ERROR
                if (newEntry == null) {
                    throw SemanticError.UNDECLARED_VARIABLE(actionNumber, token);
                }
                //Otherwise, push it onto the stack!
                if (newEntry != null) {
                    semanticStack.push(newEntry);
                }
                semanticStack.push(ETYPE.ARITHMETIC);
                break;
            }

            case 31: {
                etypeArithmeticHelper();
                SymbolTableEntry id2 = (SymbolTableEntry) semanticStack.pop();
                SymbolTableEntry offset = (SymbolTableEntry) semanticStack.pop();
                SymbolTableEntry id1 = (SymbolTableEntry) semanticStack.pop();

                if (typecheck(id1, id2) == 3) {
                    //throw SemanticError.DUMMY();
                    //ERROR
                    //Just kidding--apparently not!
                    SymbolTableEntry $$temp = create("temp", TokenType.REAL);
                    generate("ltof", id1, $$temp);
                    if (offset == null || offset.getName() == null) {
                        generate("move", $$temp, id1);
                    } else {
                        generate("stor", $$temp, offset, id1);
                    }
                } else if (typecheck(id1, id2) == 2) {
                    SymbolTableEntry $$temp = create("temp", TokenType.REAL);
                    generate("ltof", id2, $$temp);
                    if (offset == null || offset.getName() == null) {
                        generate("move", $$temp, id1);
                    } else {
                        generate("stor", $$temp, offset, id1);
                    }
                } else {
                    if (offset == null || offset.getName() == null) {
                        generate("move", id2, id1);
                    } else {
                        generate("stor", id2, offset, id1);
                    }
                }
                break;
            }

            case 32: {
                etypeArithmeticHelper();

                SymbolTableEntry thisEntry = lookupHelper(token);
                if (!(thisEntry instanceof ArrayEntry)) {
                    throw SemanticError.NOT_ARRAY(actionNumber, thisEntry.getType());
                }
                break;
            }

            case 33: {
                etypeArithmeticHelper();
                SymbolTableEntry id = (SymbolTableEntry) semanticStack.pop();

                if (id.getType() != TokenType.INTEGER) {
                    throw SemanticError.NOT_INTEGER(actionNumber, id.getType());
                    //ERROR!
                }
                SymbolTableEntry $$temp = create("temp", TokenType.INTEGER);

                int x = 0;
                for (int i = 0; i < semanticStack.size(); i++) {
                    if (semanticStack.get(i) instanceof ArrayEntry) {
                        x = i;
                        break;
                    }
                }
                ArrayEntry array_name = (ArrayEntry) semanticStack.get(x);
                generate("sub", id, "" + array_name.getLower(), $$temp);
                semanticStack.push($$temp);
                break;
            }

            case 34: {
                ETYPE etype = (ETYPE) semanticStack.pop();

                if (semanticStack.get(0) instanceof FunctionEntry) {
                    //Execute(SemanticAction.action52, token);
                } else {
                    //push NULL OFFSET
                    semanticStack.push(new SymbolTableEntry());
                }
                break;
            }

            case 35: {
                parmcount.push(0);
                SymbolTableEntry id = lookupHelper(token);
                LinkedList<ParmInfo> next = id.getParmInfo();
                nextparm.push(next);
                break;
            }

            case 36: {
                semanticStack.pop();

                SymbolTableEntry id = (SymbolTableEntry) semanticStack.pop();
                if (id.getParamNumber() != 0) {
                    throw SemanticError.NO_PARAMS(actionNumber, id);
                    //ERROR
                }
                generate("call", id, "" + 0);
                break;
            }

            case 37: {
                etypeArithmeticHelper();
                SymbolTableEntry id = (SymbolTableEntry) semanticStack.peek();
                if (!(id instanceof VariableEntry || id instanceof ConstantEntry
                        || id.isFunctionResult() || id instanceof ArrayEntry)) {
                    throw SemanticError.NOT(actionNumber, id);
                    //ERROR
                }

                int top = parmcount.pop();
                top += 1;
                parmcount.push(top);

                SymbolTableEntry proc_or_fun = null;
                for (int i = 0; i < semanticStack.size(); i++) {
                    if (semanticStack.get(i) instanceof ProcedureEntry) {
                        proc_or_fun = (ProcedureEntry) semanticStack.get(i);
                        break;
                    }
                    if (semanticStack.get(i) instanceof FunctionEntry) {
                        proc_or_fun = (FunctionEntry) semanticStack.get(i);
                        break;
                    }
                }

                if (!(proc_or_fun.getName().equals("read")
                        || proc_or_fun.getName().equals("write"))) {
                    int pCount = parmcount.peek();
                    LinkedList<ParmInfo> next = nextparm.peek();
                    ParmInfo thisParmInfo = next.getFirst();

                    if (pCount != proc_or_fun.getParamNumber()) {
                        SemanticError.INCORRECT_PARAM_NUMBER(pCount, proc_or_fun.getParamNumber());
                        //ERROR
                    }
                    if (id.getType() != thisParmInfo.getType()) {
                        SemanticError.INCORRECT_TYPE(id.getType(), thisParmInfo.getType());
                        //ERROR
                    }
                    if (thisParmInfo.isArray()) {
                        if (!(id.getUpper() != thisParmInfo.getUpper()
                                || id.getLower() != thisParmInfo.getLower())) {
                            SemanticError.DUMMY();
                            //ERROR
                        }
                    }
                }

                break;
            }

            case 38: {
                etypeArithmeticHelper();
                semanticStack.push(token);
                break;
            }

            case 39: {
                etypeArithmeticHelper();
                SymbolTableEntry id2 = (SymbolTableEntry) semanticStack.pop();
                Token operator = (Token) semanticStack.pop();
                SymbolTableEntry id1 = (SymbolTableEntry) semanticStack.pop();

                if (typecheck(id1, id2) == 2) {
                    SymbolTableEntry $$temp1 = create("temp1", TokenType.REAL);
                    generate("ltof", id2, $$temp1);
                    generate(operator.getOpType(), id1, $$temp1, "_");
                } else if (typecheck(id1, id2) == 3) {
                    SymbolTableEntry $$temp1 = create("temp1", TokenType.REAL);
                    generate("ltof", id1, $$temp1);
                    generate(operator.getOpType(), $$temp1, id2, "_");
                } else {
                    generate(operator.getOpType(), id1, id2, "_");
                }
                generate("goto", "_");

                etrueX = makelist(quads.getNextQuad() - 2);
                efalseX = makelist(quads.getNextQuad() - 1);
                semanticStack.push(etrueX);
                semanticStack.push(efalseX);
                semanticStack.push(ETYPE.RELATIONAL);

                break;
            }

            case 40: {
                //push sign (this token = unary op)
                semanticStack.push(token);
                break;
            }

            case 41: {
                etypeArithmeticHelper();
                SymbolTableEntry id = (SymbolTableEntry) semanticStack.pop();
                Token sign = (Token) semanticStack.pop();

                if (sign.getType() == TokenType.UNARYMINUS) {
                    SymbolTableEntry $$temp = create("temp", token.getType());
                    generate("uminus", id, $$temp);
                    semanticStack.push($$temp);
                } else {
                    semanticStack.push(id);
                }

                semanticStack.push(ETYPE.ARITHMETIC);
                break;
            }

            case 42: {
                ETYPE etype = (ETYPE) semanticStack.pop();
                if (token.getOpType().equals("or")) {
                    if (!(etype == ETYPE.RELATIONAL)) {
                        SemanticError.ERELATIONAL();
                    }
                    backpatch(efalseX, quads.getNextQuad());
                } else {
                    if (!(etype == ETYPE.ARITHMETIC)) {
                        SemanticError.ERELATIONAL();
                    }
                }

                //token should be an operator
                semanticStack.push(token);
                break;
            }

            case 43: {
                ETYPE etype = (ETYPE) semanticStack.pop();
                if (etype == ETYPE.RELATIONAL) {

                    LinkedList efalse2 = (LinkedList) semanticStack.pop();
                    LinkedList etrue2 = (LinkedList) semanticStack.pop();
                    Token operator = (Token) semanticStack.pop();
                    LinkedList efalse1 = (LinkedList) semanticStack.pop();
                    LinkedList etrue1 = (LinkedList) semanticStack.pop();
                    String opString = operator.getOpType();

                    if (opString.equals("or")) {
                        LinkedList etrue = merge(etrue1, etrue2);
                        LinkedList efalse = efalse2;

                        etrueX = etrue;
                        efalseX = efalse;

                        semanticStack.push(etrue);
                        semanticStack.push(efalse);
                        semanticStack.push(ETYPE.RELATIONAL);
                    }

                } else {

                    SymbolTableEntry id2 = (SymbolTableEntry) semanticStack.pop();
                    Token operator = (Token) semanticStack.pop();
                    SymbolTableEntry id1 = (SymbolTableEntry) semanticStack.pop();
                    String opString = operator.getOpType();

                    if (typecheck(id1, id2) == 0) {
                        SymbolTableEntry $$temp = create("temp", TokenType.INTEGER);
                        generate(opString, id1, id2, $$temp);
                        semanticStack.push($$temp);
                    } else if (typecheck(id1, id2) == 1) {
                        SymbolTableEntry $$temp = create("temp", TokenType.REAL);
                        generate("f" + opString, id1, id2, $$temp);
                        semanticStack.push($$temp);
                    } else if (typecheck(id1, id2) == 2) {
                        SymbolTableEntry $$temp1 = create("temp1", TokenType.REAL);
                        generate("ltof", id2, $$temp1);
                        SymbolTableEntry $$temp2 = create("temp2", TokenType.REAL);
                        generate("f" + opString, $$temp1, $$temp2);
                        semanticStack.push($$temp2);
                    } else if (typecheck(id1, id2) == 3) {
                        SymbolTableEntry $$temp1 = create("temp1", TokenType.REAL);
                        generate("ltof", id1, $$temp1);
                        SymbolTableEntry $$temp2 = create("temp2", TokenType.REAL);
                        generate("f" + opString, $$temp1, id2, $$temp2);
                        semanticStack.push($$temp2);
                    }
                    semanticStack.push(ETYPE.ARITHMETIC);
                }
                break;
            }

            case 44: {
                ETYPE etype = (ETYPE) semanticStack.pop();
                if (etype == ETYPE.RELATIONAL) {
                    if (token.getOpType().equals("and")) {
                        backpatch(etrueX, quads.getNextQuad());
                    }
                }
                semanticStack.push(token);
                break;
            }

            case 45: {
                ETYPE etype = (ETYPE) semanticStack.pop();
                if (etype == ETYPE.RELATIONAL) {

                    LinkedList efalse2 = (LinkedList) semanticStack.pop();
                    LinkedList etrue2 = (LinkedList) semanticStack.pop();
                    Token operator = (Token) semanticStack.pop();
                    LinkedList efalse1 = (LinkedList) semanticStack.pop();
                    LinkedList etrue1 = (LinkedList) semanticStack.pop();
                    String opString = operator.getOpType();

                    if (operator.getOpType().equals("and")) {
                        LinkedList etrue = etrue2;
                        LinkedList efalse = merge(efalse1, efalse2);
                        etrueX = etrue;
                        efalseX = efalse;
                        semanticStack.push(etrue);
                        semanticStack.push(efalse);
                        semanticStack.push(ETYPE.RELATIONAL);
                    }

                } else {
                    SymbolTableEntry id2 = (SymbolTableEntry) semanticStack.pop();
                    Token operator = (Token) semanticStack.pop();
                    SymbolTableEntry id1 = (SymbolTableEntry) semanticStack.pop();
                    String opString = operator.getOpType();

                    if (typecheck(id1, id2) != 0 && operator.getType() == TokenType.MULOP
                            && operator.getValue().equals("4")) {
                        SemanticError.MOD(actionNumber, id1, id2);
                    }
                    if (typecheck(id1, id2) == 0) {
                        if (operator.getType() == TokenType.MULOP && operator.getValue().equals("4")) {
                            SymbolTableEntry $$temp1 = create("temp1", TokenType.INTEGER);
                            generate("move", id1, $$temp1);
                            SymbolTableEntry $$temp2 = create("temp2", TokenType.INTEGER);
                            generate("move", $$temp1, $$temp2);
                            generate("sub", $$temp2, id2, $$temp1);
                            generate("bge", $$temp1, id2, Integer.toString(quads.getNextQuad() - 2));
                            semanticStack.push($$temp2);
                        } else {
                            if (operator.getType() == TokenType.MULOP && operator.getValue().equals("2")) {
                                SymbolTableEntry $$temp1 = create("temp1", TokenType.REAL);
                                generate("ltof", id1, $$temp1);
                                SymbolTableEntry $$temp2 = create("temp2", TokenType.REAL);
                                generate("ltof", id2, $$temp2);
                                SymbolTableEntry $$temp3 = create("temp3", TokenType.REAL);
                                generate("fdiv", $$temp1, $$temp2, $$temp3);
                                semanticStack.push($$temp3);
                            } else {
                                SymbolTableEntry $$temp = create("temp", TokenType.INTEGER);
                                generate(opString, id1, id2, $$temp);
                                semanticStack.push($$temp);
                            }
                        }
                    }
                    if (typecheck(id1, id2) == 1) {
                        if (operator.getType() == TokenType.MULOP && operator.getValue().equals("3")) {
                            SymbolTableEntry $$temp1 = create("temp1", TokenType.INTEGER);
                            generate("ftol", id1, $$temp1);
                            SymbolTableEntry $$temp2 = create("temp2", TokenType.INTEGER);
                            generate("ftol", id2, $$temp2);
                            SymbolTableEntry $$temp3 = create("temp3", TokenType.REAL);
                            generate("div", $$temp1, $$temp2, $$temp3);
                            semanticStack.push($$temp3);
                        } else {
                            SymbolTableEntry $$temp = create("temp", TokenType.REAL);
                            generate("f" + opString, id1, id2, $$temp);
                            semanticStack.push($$temp);
                        }
                    }
                    if (typecheck(id1, id2) == 2) {
                        if (operator.getType() == TokenType.MULOP && operator.getValue().equals("3")) {
                            SymbolTableEntry $$temp1 = create("temp1", TokenType.INTEGER);
                            generate("ftol", id1, $$temp1);
                            SymbolTableEntry $$temp2 = create("temp2", TokenType.INTEGER);
                            generate("div", $$temp1, id2, $$temp2);
                            semanticStack.push($$temp2);
                        } else {
                            SymbolTableEntry $$temp1 = create("temp1", TokenType.REAL);
                            generate("ltof", id2, $$temp1);
                            SymbolTableEntry $$temp2 = create("temp2", TokenType.REAL);
                            generate("f" + opString, id1, $$temp1, $$temp2);
                            semanticStack.push($$temp2);
                        }
                    }
                    if (typecheck(id1, id2) == 3) {
                        if (operator.getType() == TokenType.MULOP && operator.getValue().equals("3")) {
                            SymbolTableEntry $$temp1 = create("temp1", TokenType.INTEGER);
                            generate("ftol", id2, $$temp1);
                            SymbolTableEntry $$temp2 = create("temp2", TokenType.INTEGER);
                            generate("div", id1, $$temp1, $$temp2);
                            semanticStack.push($$temp2);
                        } else {
                            SymbolTableEntry $$temp1 = create("temp1", TokenType.REAL);
                            generate("ltof", id1, $$temp1);
                            SymbolTableEntry $$temp2 = create("temp2", TokenType.REAL);
                            generate("f" + opString, $$temp1, id2, $$temp2);
                            semanticStack.push($$temp2);
                        }
                    }
                }
                semanticStack.push(ETYPE.ARITHMETIC);
                break;
            }

            case 46: {
                //If the token is an identifier, look it up
                //and then push the symbol table entry
                if (token.getType() == TokenType.IDENTIFIER) {
                    SymbolTableEntry thisEntry = lookupHelper(token);
                    if (thisEntry == null) {
                        throw SemanticError.UNDECLARED_VARIABLE(actionNumber, token);
                    }
                    semanticStack.push(thisEntry);
                }
                //If the token is a constant, look it up
                //and then push the symbol table entry
                if (token.getType() == TokenType.INTCONSTANT
                        || token.getType() == TokenType.REALCONSTANT) {
                    SymbolTableEntry thisEntry;
                    thisEntry = constantTable.lookup(token.getValue(), false);
                    if (thisEntry == null) {
                        //if tokentype = intconstant, field = integer
                        if (token.getType() == TokenType.INTCONSTANT) {
                            thisEntry = new ConstantEntry(token.getValue(), TokenType.INTEGER);
                            constantTable.insert(thisEntry);
                        }
                        //if tokentype = realconstant, field = real
                        if (token.getType() == TokenType.REALCONSTANT) {
                            thisEntry = new ConstantEntry(token.getValue(), TokenType.REAL);
                            constantTable.insert(thisEntry);
                        }
                    }
                    //push the symbol table entry
                    semanticStack.push(thisEntry);
                }
                semanticStack.push(ETYPE.ARITHMETIC);
                break;
            }

            case 47: {
                etypeRelationalHelper();
                efalseX = (LinkedList) semanticStack.pop();
                etrueX = (LinkedList) semanticStack.pop();
                
                LinkedList etemp = etrueX;
                etrueX = efalseX;
                efalseX = etemp;

                semanticStack.push(etrueX);
                semanticStack.push(efalseX);
                semanticStack.push(ETYPE.RELATIONAL);

                break;
            }

            case 48: {
                Object etypeCheck = semanticStack.peek();
                SymbolTableEntry topStack;
                if (etypeCheck instanceof ETYPE) {
                    semanticStack.pop();
                    topStack = (SymbolTableEntry) semanticStack.peek();
                } else {
                    topStack = (SymbolTableEntry) etypeCheck;
                }
                
                //If the top isn't null, pop the offset and the id
                if (topStack != null && topStack.getName() != null) {
                    if (topStack.getType() != TokenType.INTEGER) {
                        throw SemanticError.OFFSET_TYPE(actionNumber, topStack.getType());
                    } else {
                        SymbolTableEntry offset = (SymbolTableEntry) semanticStack.pop();
                        SymbolTableEntry id = (SymbolTableEntry) semanticStack.pop();

                        SymbolTableEntry $$temp = create("temp", id.getType());
                        generate("load", id, offset, $$temp);
                        semanticStack.push($$temp);

                    }
                } else {
                    //otherwise pop the offset
                    SymbolTableEntry offset = (SymbolTableEntry) semanticStack.pop();
                }
                semanticStack.push(ETYPE.ARITHMETIC);
                break;
            }

            case 49: {
                etypeArithmeticHelper();
                SymbolTableEntry id = lookupHelper(token);
                parmcount.push(0);
                LinkedList<ParmInfo> next = id.getParmInfo();
                nextparm.push(next);
                break;
            }

            case 50: {
                //Use a temporary stack to find the FE or PE
                Stack tempStack = new Stack<>();
                SymbolTableEntry topStack = (SymbolTableEntry) semanticStack.peek();
                while (!((topStack instanceof FunctionEntry) || (topStack instanceof ProcedureEntry))) {
                    SymbolTableEntry top = (SymbolTableEntry) semanticStack.pop();
                    tempStack.push(top);
                    topStack = (SymbolTableEntry) semanticStack.peek();
                }
                while (!(tempStack.isEmpty())) {
                    SymbolTableEntry thisID = (SymbolTableEntry) tempStack.pop();
                    generate("param", thisID);
                    localMemory += 1;
                }

                SymbolTableEntry id = (SymbolTableEntry) semanticStack.pop();
                LinkedList<ParmInfo> next = nextparm.pop();
                int pCount = parmcount.pop();

                if (pCount != id.getParamNumber()) {
                    SemanticError.INCORRECT_PARAM_NUMBER(pCount, id.getParamNumber());
                    //ERROR
                }

                generate("call", id.getName(), "" + pCount);
                SymbolTableEntry $$temp = create("temp", id.getType());
                generate("move", lookupHelper(id.getResult()), $$temp);
                semanticStack.push($$temp);
                semanticStack.push(ETYPE.ARITHMETIC);
                break;
            }

            case 51: {
                Object topHelp = (Object) semanticStack.peek();
                while (!(topHelp instanceof ProcedureEntry)) {
                    topHelp = (Object) semanticStack.pop();
                    helpStack.push(topHelp);
                }

                SymbolTableEntry top = (SymbolTableEntry) helpStack.pop();

                if (top.getName().toLowerCase().equals("read")) {
                    //57 = 51READ
                    Execute(SemanticAction.action57, token);
                } else if (top.getName().toLowerCase().equals("write")) {
                    //58 = 51WRITE
                    Execute(SemanticAction.action58, token);
                } else {
                    LinkedList<ParmInfo> next = nextparm.pop();
                    int pCount = parmcount.pop();

                    //pop etype
                    helpStack.pop();

                    if (pCount != top.getParamNumber()) {
                        SemanticError.INCORRECT_PARAM_NUMBER(pCount, top.getParamNumber());
                        //ERROR
                    }

                    while (!(helpStack.isEmpty())) {
                        SymbolTableEntry thisID = (SymbolTableEntry) helpStack.pop();
                        generate("param", thisID);
                        localMemory += 1;
                    }
                    generate("call", top.getName(), "" + pCount);
                }
                break;
            }

            case 52: {
                //I guess we don't do this anymore??
                Stack tempStack = new Stack();
                while (!(semanticStack.peek() instanceof FunctionEntry)) {
                    tempStack.push(semanticStack.pop());
                }

                SymbolTableEntry id = (SymbolTableEntry) semanticStack.pop();
                if (!(id instanceof FunctionEntry)) {
                    throw SemanticError.NOT_FUNC(actionNumber, id);
                    //ERROR
                }
                if (!(id.getParamNumber() > 0)) {
                    throw SemanticError.NO_PARAMS(actionNumber, id);
                    //ERROR
                }
                generate("call", id.getName(), "" + 0);
                SymbolTableEntry $$temp = create("temp", id.getType());
                generate("move", lookupHelper(id.getResult()), $$temp);
                semanticStack.push($$temp);
                while (!(tempStack.isEmpty())) {
                    semanticStack.push(tempStack.pop());
                }
                semanticStack.push(ETYPE.ARITHMETIC);
                break;
            }

            case 53: {
                //pop etype
                semanticStack.pop();
                if (semanticStack.peek() instanceof FunctionEntry) {
                    SymbolTableEntry id = (SymbolTableEntry) semanticStack.pop();
                    semanticStack.push(lookupHelper(id.getResult()));
                }
                semanticStack.push(ETYPE.ARITHMETIC);
                break;
            }

            case 54: {
                SymbolTableEntry thisEntry = lookupHelper(token);
                if (!(thisEntry instanceof ProcedureEntry)) {
                    SemanticError.NOT_PROC(actionNumber, thisEntry);
                    //ERROR (what kind?)
                }
                break;
            }

            case 55: {
                quads.setField(global_store, 1, Integer.toString(globalMemory));
                generate("free", Integer.toString(globalMemory));
                generate("PROCEND");
                break;
            }

            case 56: {
                generate("PROCBEGIN main");
                global_store = quads.getNextQuad();
                generate("alloc", "_");
                break;
            }

            //57 = 51READ
            case 57: {
                System.out.println("read stack:");
                dumpStack(helpStack);

                if (helpStack.peek() instanceof ETYPE) {
                    helpStack.pop();
                }

                parmcount.pop();

                while (!(helpStack.isEmpty())) {
                    SymbolTableEntry thisID = (SymbolTableEntry) helpStack.pop();
                    if (thisID.getType() == TokenType.REAL) {
                        generate("finp", thisID);
                    } else {
                        generate("inp", thisID);
                    }
                }
                break;
            }

            case 58: {
                if (helpStack.peek() instanceof ETYPE) {
                    helpStack.pop();
                }
                parmcount.pop();

                while (!(helpStack.isEmpty())) {
                    SymbolTableEntry thisID = (SymbolTableEntry) helpStack.pop();
                    generate("print", thisID.getName() + " = \"" + thisID + "\"");
                    if (thisID.getType() == TokenType.REAL) {
                        generate("foutp", thisID);
                    } else {
                        generate("outp", thisID);
                    }
                    generate("newl");
                }
                break;
            }
        }
        dumpStack(semanticStack);
    }

    public void insertHelper(SymbolTableEntry newEntry) {
        if (global) {
            globalTable.insert(newEntry);
        } else { //Otherwise insert in the local table
            localTable.insert(newEntry);
        }
    }

    public SymbolTableEntry lookupHelper(Token token) {
        SymbolTableEntry newEntry = null;
        if (global) {
            newEntry = globalTable.lookup(token.getValue(), false);
        } else {
            newEntry = localTable.lookup(token.getValue(), false);
            if (newEntry == null) {
                newEntry = globalTable.lookup(token.getValue(), false);
            }
        }
        return newEntry;
    }

    public SymbolTableEntry lookupHelper(String newString) {
        SymbolTableEntry newEntry = null;
        if (global) {
            newEntry = globalTable.lookup(newString, false);
        } else {
            newEntry = localTable.lookup(newString, false);
            if (newEntry == null) {
                newEntry = globalTable.lookup(newString, false);
            }
        }
        return newEntry;
    }

    public SymbolTableEntry lookupHelper(SymbolTableEntry newEntry) {
        if (global) {
            newEntry = globalTable.lookup(newEntry, false);
        } else {
            newEntry = localTable.lookup(newEntry, false);
            if (newEntry == null) {
                newEntry = globalTable.lookup(newEntry, false);
            }
        }
        return newEntry;
    }

    public void memoryHelper(SymbolTableEntry newEntry, int msize) {
        if (global) {
            newEntry.setAddress(globalMemory);
            globalMemory += msize;
        } else { // Otherwise, local
            newEntry.setAddress(localMemory);
            localMemory += msize;
        }
    }

    public void memoryHelper(SymbolTableEntry newEntry) {
        if (global) {
            newEntry.setAddress(globalMemory);
            globalMemory += 1;
        } else { // Otherwise, local
            newEntry.setAddress(localMemory);
            localMemory += 1;
        }
    }
    
    //MOVE HELPER DOES THE MOVES FOR EXTRA CONSTANTS
    //PREFIX HELPER FIGURES OUT THE PREFIX

    private void generate(String tviCode, String operand1,
            String operand2, SymbolTableEntry operand3) {
        String[] quad = new String[4];

        moveHelper(operand3);

        quad[0] = tviCode;
        quad[1] = operand1;
        quad[2] = operand2;
        quad[3] = prefixHelper(tviCode, operand3) + Math.abs(operand3.getAddress());
        quads.addQuad(quad);
        quads.increment();
    }

    private void generate(String tviCode, SymbolTableEntry operand1,
            String operand2, SymbolTableEntry operand3) {
        String[] quad = new String[4];

        moveHelper(operand1);
        moveHelper(operand3);

        quad[0] = tviCode;
        quad[1] = prefixHelper(tviCode, operand1) + Math.abs(operand1.getAddress());
        quad[2] = operand2;
        quad[3] = prefixHelper(tviCode, operand3) + Math.abs(operand3.getAddress());
        quads.addQuad(quad);
        quads.increment();
    }

    private void generate(String tviCode, SymbolTableEntry operand1,
            SymbolTableEntry operand2, SymbolTableEntry operand3) {
        String[] quad = new String[4];

        moveHelper(operand1);
        moveHelper(operand2);
        moveHelper(operand3);

        quad[0] = tviCode;
        quad[1] = prefixHelper(tviCode, operand1) + Math.abs(operand1.getAddress());
        quad[2] = prefixHelper(tviCode, operand2) + Math.abs(operand2.getAddress());
        quad[3] = prefixHelper(tviCode, operand3) + Math.abs(operand3.getAddress());
        quads.addQuad(quad);
        quads.increment();
    }

    private void generate(String tviCode, SymbolTableEntry operand1,
            SymbolTableEntry operand2, String operand3) {
        String[] quad = new String[4];

        moveHelper(operand1);
        moveHelper(operand2);
        quad[0] = tviCode;
        quad[1] = prefixHelper(tviCode, operand1) + Math.abs(operand1.getAddress());
        quad[2] = prefixHelper(tviCode, operand2) + Math.abs(operand2.getAddress());
        quad[3] = operand3;
        quads.addQuad(quad);
        quads.increment();
    }

    private void generate(String tviCode, SymbolTableEntry operand1,
            SymbolTableEntry operand2) {
        String[] quad = new String[4];

        moveHelper(operand1);
        moveHelper(operand2);
        quad[0] = tviCode;
        quad[1] = prefixHelper(tviCode, operand1) + Math.abs(operand1.getAddress());
        quad[2] = prefixHelper(tviCode, operand2) + Math.abs(operand2.getAddress());
        quads.addQuad(quad);
        quads.increment();
    }

    private void generate(String tviCode, String operand1, String operand2) {
        String[] quad = new String[4];

        quad[0] = tviCode;
        quad[1] = operand1;
        quad[2] = operand2;
        quads.addQuad(quad);
        quads.increment();
    }

    private void generate(String tviCode, String operand1, SymbolTableEntry operand2) {
        String[] quad = new String[4];

        moveHelper(operand2);

        quad[0] = tviCode;
        quad[1] = operand1;
        quad[2] = prefixHelper(tviCode, operand2) + Math.abs(operand2.getAddress());
        quads.addQuad(quad);
        quads.increment();
    }

    private void generate(String tviCode, SymbolTableEntry operand1) {
        String[] quad = new String[4];

        moveHelper(operand1);
        quad[0] = tviCode;
        quad[1] = prefixHelper(tviCode, operand1) + Math.abs(operand1.getAddress());
        quads.addQuad(quad);
        quads.increment();
    }

    private void generate(String tviCode, SymbolTableEntry operand1,
            String operand2) {
        String[] quad = new String[4];

        moveHelper(operand1);
        quad[0] = tviCode;
        quad[1] = prefixHelper(tviCode, operand1) + Math.abs(operand1.getAddress());
        quad[2] = operand2;
        quads.addQuad(quad);
        quads.increment();
    }

    private void generate(String tviCode, String operand1) {
        String[] quad = new String[4];
        quad[0] = tviCode;
        quad[1] = operand1;
        quads.addQuad(quad);
        quads.increment();
    }

    private void generate(String tviCode) {
        String[] quad = new String[4];

        quad[0] = tviCode;
        quads.addQuad(quad);
        quads.increment();
    }

    //GETS THE PREFIXES FOR THE OFFSETS
    private String prefixHelper(String tviCode, SymbolTableEntry id) {
        String prefix = "";
        boolean inLocal = false;

        if (localTable != null && localTable.lookup(id, false) != null) {
            inLocal = true;
        }

        if (tviCode.equals("param")) {
            if (id.isParameter()) {
                return "%";
            } else if (inLocal) {
                return "@%";
            } else {
                return "@_";
            }
        } else {
            if (id.isParameter()) {
                return "^%";
            } else if (inLocal) {
                return "%";
            } else {
                return "_";
            }
        }
    }

    //HELPS GENERATE W/ CONSTANTS
    private void moveHelper(SymbolTableEntry id) {
        if (id instanceof ConstantEntry) {
            SymbolTableEntry $$temp = create("temp", id.getType());
            id.setAddress($$temp.getAddress());
            generate("move", id.getName(), $$temp);
        }
    }

    //CREATE A TEMP VARIABLE
    private SymbolTableEntry create(String name, TokenType type) {
        //use tempcount to give each variable a unique name
        tempCount += 1;
        name = "$$" + name + tempCount;
        VariableEntry newEntry = new VariableEntry(name, type);

        if (global) {
            newEntry.setAddress(-globalMemory);
            globalTable.insert(newEntry);
            globalMemory += 1;
        } else {
            newEntry.setAddress(-localMemory);
            localTable.insert(newEntry);
            localMemory += 1;
        }
        return newEntry;
    }

    private int typecheck(Token id1, Token id2) {
        if (id1.getType() == TokenType.INTEGER
                && id2.getType() == TokenType.INTEGER) {
            return 0;
        }
        if (id1.getType() == TokenType.REAL
                && id2.getType() == TokenType.REAL) {
            return 1;
        }
        if (id1.getType() == TokenType.REAL
                && id2.getType() == TokenType.INTEGER) {
            return 2;
        }
        if (id1.getType() == TokenType.INTEGER
                && id2.getType() == TokenType.REAL) {
            return 3;
        }
        //error
        return 999;
    }

    private int typecheck(SymbolTableEntry id1, SymbolTableEntry id2) {
        if (id1.getType() == TokenType.INTEGER
                && id2.getType() == TokenType.INTEGER) {
            return 0;
        }
        if (id1.getType() == TokenType.REAL
                && id2.getType() == TokenType.REAL) {
            return 1;
        }
        if (id1.getType() == TokenType.REAL
                && id2.getType() == TokenType.INTEGER) {
            return 2;
        }
        if (id1.getType() == TokenType.INTEGER
                && id2.getType() == TokenType.REAL) {
            return 3;
        }
        //error
        return 999;
    }

    /**
     * Create a new list containing only i, an index into an array of
     * quadruples. Returns a pointer to the list it has made.
     *
     * @param i, an index into an array of quadruples
     * @return pointer to the list it has made
     */
    public LinkedList makelist(int i) {
        LinkedList makelist = new LinkedList<Integer>();
        makelist.add(i);
        return makelist;
    }

    /*
     * Concatenates the lists pointed to by p1 and p2,
     * returns a pointer to the new list
     */
    public LinkedList merge(LinkedList<Integer> p1, LinkedList<Integer> p2) {
        p1.addAll(p2);
        return p1;
    }

    /*
     * Insert i as the target label for each of the statements
     * on the list pointed to by p.
     */
    public void backpatch(LinkedList<Integer> p, int eye) {
        boolean found = false;
        String i = Integer.toString(eye);
        for (int index : p) {
            String[] thisQuad = quads.getQuad(index);
            for (int j = 0; j < 4; j++) {
                String current = thisQuad[j];
                if (current != null && current.equals("_")) {
                    found = true;
                    quads.setField(index, j, eye);
                }
            }
        }
    }

    /**
     * Installs the built in functions for the global table.
     *
     * @param globalTable
     */
    public void InstallBuiltins(SymbolTable globalTable) {
        globalTable.insert(new ProcedureEntry("read"));
        globalTable.insert(new ProcedureEntry("write"));
        globalTable.insert(new ProcedureEntry("main"));
    }

    /**
     * Dumps the contents of the semantic stack
     */
    public void dumpStack(Stack theStack) {
        for (int i = theStack.size() - 1; i >= 0; i--) {
            System.out.print(theStack.get(i) + " \n");
        }
        System.out.println();
    }

    public Quadruples getQuad() {
        return quads;
    }

    //if ETYPE <> ARITHMETIC, ERROR
    private void etypeArithmeticHelper() {
        ETYPE etype = (ETYPE) semanticStack.pop();
        if (!(etype.equals(ETYPE.ARITHMETIC))) {
            SemanticError.EARITHMETIC();
        }
    }

    //if ETYPE <> RELATIONAL, ERROR
    private void etypeRelationalHelper() {
        ETYPE etype = (ETYPE) semanticStack.pop();
        if (!(etype.equals(ETYPE.RELATIONAL))) {
            SemanticError.ERELATIONAL();
        }
    }

    //Wanna know all the actions you 
    public ArrayList<Integer> getActionList() {
        ArrayList<Integer> actions = new ArrayList<Integer>(actionList);
        Collections.sort(actions);
        return actions;
    }
}
