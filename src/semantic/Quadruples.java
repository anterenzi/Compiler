/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package semantic;

import java.util.*;

/**
 *
 * @author anterenzi
 */
public class Quadruples {

    private ArrayList<String[]> Quadruple;
    private int nextQuad;

    public Quadruples() {
        Quadruple = new ArrayList<String[]>();
        nextQuad = 0;
    }

    public String getField(int quadIndex, int field) {
        return Quadruple.get(quadIndex)[field];
    }

    public void setField(int quadIndex, int index, String field) {
        Quadruple.get(quadIndex)[index] = field;
    }

    public void setField(int quadIndex, int index, int intField) {
        String field = Integer.toString(intField);
        Quadruple.get(quadIndex)[index] = field;
    }

    public int getNextQuad() {
        return nextQuad;
    }

    public String[] getQuad(int index) {
        return Quadruple.get(index);
    }

    public void addQuad(String[] quad) {
        Quadruple.add(quad);
    }

    public String print() {
        String finalString = "";
        int lineCount = -1;
        for (String quad[] : Quadruple) {
            lineCount += 1;
            if (lineCount != 0) {
                finalString += ":";
            }
            for (int i = 0; i < quad.length; i++) {
                if (quad[i] != null) {
                    if (i == 2 || i == 3)
                    {
                        finalString += ",";
                    }
                    if (lineCount != 0) {
                    finalString += " ";
                    }
                    
                    finalString += quad[i];
                }
            }
            finalString += "\n";
        }
        return finalString;
    }

    public void increment() {
        nextQuad += 1;
    }
}
