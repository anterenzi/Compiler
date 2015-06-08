//ANDREW TERENZI
package lexicalanalyzer;

public class Token {

    TokenType type;
    String value;

    public Token() {
    }

    public Token(TokenType type) {
        this.type = type;
    }

    public Token(TokenType type, String value) {
        this.type = type;
        this.value = value;
    }

    public Token(TokenType type, int val) {
        this.type = type;
        this.value = Integer.toString(val);
    }

    public TokenType getType() {
        return type;
    }

    public String getValue() {
        return value;
    }
    
    public void setType(TokenType newType) {
        type = newType;
    }

    //uses the stored value to determine
    //which kind of op for addop, relop, mulop
    public String getOpType() {
        int intValue = Integer.parseInt(this.value);
        if (this.type == TokenType.ADDOP) {
            if (intValue == 1) {
                return "add";
            }
            if (intValue == 2) {
                return "sub";
            } else {
                return "or";
            }
        }
        if (this.type == TokenType.RELOP) {
            if (intValue == 1) {
                return "beq";
            }
            if (intValue == 2) {
                return "bne";
            }
            if (intValue == 3) {
                return "blt";
            }
            if (intValue == 4) {
                return "bgt";
            }
            if (intValue == 5) {
                return "ble";
            } else {
                return "bge";
            }
        }
        if (this.type == TokenType.MULOP) {
            if (intValue == 1) {
                return "mul";
            }
            if (intValue == 2) {
                // "/"
                return "div";
            }
            if (intValue == 3) {
                //integer division
                return "div";
            }
            if (intValue == 4) {
                return "mod";
            } else {
                return "and";
            }
        }
        return "";
    }
    
    public String toString()
    {
        return "Token: {type: " + type + ", value: " + value + "}";
    }
}
