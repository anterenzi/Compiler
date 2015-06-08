package drivers;

//Main class for the whole compiler.
//Parsers through the class and generates
//TVI code as the output.

import errors.*;
import parser.*;

public class parserDriver {

    Parser parser;

    public parserDriver(String filename) {
        parser = new Parser(filename);
    }

    public parserDriver() {
        //parser = new Parser("src/semantic/" + "simple" + ".pas");
        String string1 = "src/semantictest/";
        String string2 = "ult-corrected";
        String string3 = ".pas";
        parser = new Parser(string1, string2, string3);
    }

    protected void run() {
        try {
            parser.parse();
        } catch (CompilerError ex) {
            System.out.println(ex);
        }

        System.out.println("Compilation successful.");
    }

    public static void main(String[] args) {
        parserDriver test = new parserDriver();
        test.run();
    }
}
