//ANDREW TERENZI

package symboltable;

import java.util.HashMap;

public class SymbolTable {

    //Using a HashMap for the hashtable
    HashMap hashtable;

    public SymbolTable() {
        //The key is going to be the name/lexeme
        //The value is going to be the SymbolTableEntry itself
        hashtable = new HashMap<String, SymbolTableEntry>();
    }

    public SymbolTable(int size) {
        //Second constructor for a preset size
        hashtable = new HashMap<String, SymbolTableEntry>(size);
    }

    /*
     * insert or search for a given identifier in the symbol table,
     * and return a pointer to the location in the table at which the
     * identifier was inserted or found.
     */
    public SymbolTableEntry lookup(SymbolTableEntry newEntry, boolean insert) {
        //USE GLOBAL FLAG
        //IF TRUE, SEARCH FOR.
        //OTHERWISE, INSERT
        
        //if the global flag is true
        if (!insert) {
            //search for the entry in the symbol table
            //returns null if the entry is not there
            return (SymbolTableEntry) hashtable.get(newEntry.getName().toLowerCase());
        } else {
            //otherwise insert it
            return insert(newEntry);
        }
    }
    
    public SymbolTableEntry lookup(String newEntry, boolean insert) {
        //USE GLOBAL FLAG
        //IF TRUE, SEARCH FOR.
        //OTHERWISE, INSERT
        
        //if the global flag is true
        if (!insert) {
            //search for the entry in the symbol table
            //returns null if the entry is not there
            SymbolTableEntry thisEntry = (SymbolTableEntry) hashtable.get(newEntry.toLowerCase());
            return thisEntry;
        }
        return null;
    }

    /*
     * search for a given constant, and if not found, insert the constant
     * in the table; in either case a pointer to its table location is
     * returned to the calling routine.
     */
    public SymbolTableEntry insert(SymbolTableEntry newEntry) {
        //if the value is already in the hashtable, just return it
        if (hashtable.containsValue(newEntry)) {
            return newEntry;
        } else {
            //otherwise enter it in, and then return it
            return (SymbolTableEntry) hashtable.put(newEntry.getName().toLowerCase(), newEntry);
        }
    }
    
    /*
     * remove an entry from the table
     */
    public void remove(SymbolTableEntry newEntry) {
        hashtable.remove(newEntry.getName());
    }

    /*
     * dumps the contents of the symbol table
     */
    public void dumpTable() {
        // iterate through the keyset
        for (Object name : hashtable.keySet()) {
            String key = name.toString();
            //use the keys to also get the values
            String value = hashtable.get(name).toString();
            //print out each entry
            System.out.println(key + " (" + value + ")");

        }
    }
    
    public void deleteAll()
    {
        hashtable.clear();
    }
}
