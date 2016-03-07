import java.io.*;
import java.util.Scanner;


class BPLScanner {
    private Scanner scanner;

    private String curLine;
    private int curLineNum;
    private int curIndex;

    private int position;

    public BPLScanner(String fileName){
        File inputFile;
        try {
            inputFile = new File(fileName);
            this.scanner = new Scanner(inputFile);
        }
        catch (FileNotFoundException e){
            System.err.println("File not found!");
            System.exit(-1);
        }
        this.curLineNum = 0;
        this.position = 0;
    }

    public boolean hasNextToken() throws BPLScannerException {
        if (this.curLine == null || this.lineEnded()){
            if (this.hasNextLine()){
                this.goNextLine();
            }
            else {
                return false;
            }
        }
        while (true){
            if (!lineEnded()){
                if (Character.isWhitespace(this.currentChar())) {
                    this.goNextChar();
                }
                else if (this.currentCharEquals('/')) {
                    this.goNextChar();
                    if (this.currentCharEquals('*')) {
                        this.skipComment();
                    }
                    else {
                        this.goPrevChar();
                        return true;
                    }
                }
                else {
                    return true;
                }
            }
            else if (this.hasNextLine()){
                this.goNextLine();
            }
            else {
                return false;
            }
        }
    }

    public Token getNextToken() throws BPLScannerException {
        if (!hasNextToken()){
            throw new BPLScannerException(this.curLineNum,
                "no more token. Please use the hasNextToken() function to check first.");
        }

        String token = "";
        int type = -1;

        char c = this.currentChar();

        if (Character.isDigit(c)){
            type = Token.T_NUM;
            token = this.getNumTokenString();
        }
        else if (c == '"'){
            type = Token.T_STRING;
            token = this.getStringTokenString();
        }
        else if (Character.isLetter(c)) {
            token = this.getKeywordOrIDString();
            type = this.getKeywordOrIDType(token);
        }
        else {
            token = this.getSymbolString();
            type = getSymbolType(token);
        }

        return new Token(type, token, this.curLineNum, this.position++);
    }


    /** Private methods */

    private boolean hasNextLine(){
        return this.scanner.hasNextLine();
    }

    // goes to next character on the same line
    // if out of bounds, will be handled by hasNextToken()
    private void goNextChar(){
        this.curIndex++;
    }

    // goes to previous character on the same line, if exists
    private void goPrevChar(){
        if (this.curIndex > 0){
            this.curIndex--;
        }
    }

    private void goNextLine(){
        this.curLine = this.scanner.nextLine();
        this.curIndex = 0;
        this.curLineNum++;
    }

    private boolean lineEnded(){
        return this.curIndex >= this.curLine.length();
    }

    private boolean currentCharEquals(char c){
        return !this.lineEnded()
            && this.curLine.charAt(this.curIndex) == c;
    }

    private char currentChar(){
        return this.curLine.charAt(this.curIndex);
    }

    private void skipComment() throws BPLScannerException {
        String temp = this.curLine.substring(this.curIndex);
        while (temp.indexOf("*/") == -1){
            if (this.hasNextLine()){
                this.goNextLine();
                temp = this.curLine;
            }
            else {
                throw new BPLScannerException(this.curLineNum, "comment not closed with */");
            }
        }
        this.curIndex = this.curLine.indexOf("*/") + 2;
    }

    private String getNumTokenString() {
        String token = "";
        while (!this.lineEnded()){
            char c = this.currentChar();
            this.goNextChar();
            if (Character.isDigit(c)){
                token += c;
            }
            else {
                this.goPrevChar();
                break;
            }
        }
        return token;
    }

    private String getStringTokenString() throws BPLScannerException {
        String token = "";
        boolean valid = false;
        this.goNextChar();
        while (!this.lineEnded()){
            char c = this.currentChar();
            this.goNextChar();
            if (c == '"'){
                valid = true;
                break;
            }
            else {
                token += c;
            }
        }
        if (!valid) {
            throw new BPLScannerException(this.curLineNum,
                "closing quote for string not found on the same line");
        }
        return token;
    }

    private String getKeywordOrIDString() {
        String token = "";
        token += this.currentChar();
        this.goNextChar();
        while (!this.lineEnded()){
            char c = this.currentChar();
            this.goNextChar();
            if (Character.isLetter(c) || Character.isDigit(c)
                    || c == '_'){
                token += c;
            }
            else {
                this.goPrevChar();
                break;
            }
        }
        return token;
    }

    private int getKeywordOrIDType(String token) {
        if (token.equals("int")){
            return Token.T_INT;
        }
        else if (token.equals("void")){
            return Token.T_VOID;
        }
        else if (token.equals("string")){
            return Token.T_KWSTRING;
        }
        else if (token.equals("if")){
            return Token.T_IF;
        }
        else if (token.equals("else")){
            return Token.T_ELSE;
        }
        else if (token.equals("while")){
            return Token.T_WHILE;
        }
        else if (token.equals("return")){
            return Token.T_RETURN;
        }
        else if (token.equals("write")){
            return Token.T_WRITE;
        }
        else if (token.equals("writeln")){
            return Token.T_WRITELN;
        }
        else if (token.equals("read")){
            return Token.T_READ;
        }

        return Token.T_ID;
    }

    private String getSymbolString() throws BPLScannerException {
        String token = "";
        token += this.currentChar();
        this.goNextChar();
        if (token.equals("<")){
            if (this.currentCharEquals('=')) {
                token += "=";
                this.goNextChar();
            }
        }
        else if (token.equals("=")){
            if (this.currentCharEquals('=')) {
                token += "=";
                this.goNextChar();
            }
        }
        else if (token.equals(">")){
            if (this.currentCharEquals('=')) {
                token += "=";
                this.goNextChar();
            }
        }
        else if (token.equals("!")){
            if (this.currentCharEquals('=')) {
                token += "=";
                this.goNextChar();
            }
            else{
                throw new BPLScannerException(this.curLineNum, "illegal token !");
            }
        }
        return token;
    }

    private int getSymbolType(String token) throws BPLScannerException{
        if (token.equals(";")){
            return Token.T_SEMI;
        }
        else if (token.equals(",")){
            return Token.T_COMMA;
        }
        else if (token.equals("[")){
            return Token.T_LBRAC;
        }
        else if (token.equals("]")){
            return Token.T_RBRAC;
        }
        else if (token.equals("{")){
            return Token.T_LCURL;
        }
        else if (token.equals("}")){
            return Token.T_RCURL;
        }
        else if (token.equals("(")){
            return Token.T_LPAREN;
        }
        else if (token.equals(")")){
            return Token.T_RPAREN;
        }
        else if (token.equals("<=")){
            return Token.T_LESSEQ;
        }
        else if (token.equals("<")){
            return Token.T_LESS;
        }
        else if (token.equals("==")){
            return Token.T_EQEQ;
        }
        else if (token.equals("=")) {
            return Token.T_EQ;
        }
        else if (token.equals(">=")){
            return Token.T_GREQ;
        }
        else if (token.equals(">")){
            return Token.T_GR;
        }
        else if (token.equals("!=")){
            return Token.T_NE;
        }
        else if (token.equals("+")){
            return Token.T_PLUS;
        }
        else if (token.equals("-")){
            return Token.T_MINUS;
        }
        else if (token.equals("*")){
            return Token.T_MULT;
        }
        else if (token.equals("/")){
            return Token.T_DIV;
        }
        else if (token.equals("%")){
            return Token.T_PERCENT;
        }
        else if (token.equals("&")){
            return Token.T_AMP;
        }

        this.goPrevChar();
        throw new BPLScannerException(this.curLineNum, "illegal character ascii "
            + (int)this.currentChar());
    }

    /** End of private methods */


    public static void main(String args[]) throws BPLScannerException {
        String fileName = args[0];
        BPLScanner scanner = new BPLScanner(fileName);
        while (scanner.hasNextToken()){
            System.out.println(scanner.getNextToken());
        }
    }
}
