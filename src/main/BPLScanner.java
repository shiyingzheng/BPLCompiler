import java.io.*;
import java.util.Scanner;


class BPLScanner {
    private Scanner scanner;

    private String curLine;
    private int curLineNum;
    private int curIndex;

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
    }

    public boolean hasNextToken() throws BPLScannerException{
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

        String curToken = "";
        int type = -1;

        char c = this.currentChar();
        this.goNextChar();

        if (Character.isDigit(c)){
            curToken += c;
            type = Token.T_NUM;
            while (!this.lineEnded()){
                c = this.currentChar();
                this.goNextChar();
                if (Character.isDigit(c)){
                    curToken += c;
                }
                else {
                    this.goPrevChar();
                    break;
                }
            }
        }
        else if (c == '"'){
            type = Token.T_STRING;
            boolean valid = false;
            while (!this.lineEnded()){
                c = this.currentChar();
                this.goNextChar();
                if (c == '"'){
                    valid = true;
                    break;
                }
                else {
                    curToken += c;
                }
            }
            if (!valid) {
                throw new BPLScannerException(this.curLineNum,
                    "closing quote for string not found on the same line");
            }
        }
        else if (Character.isLetter(c)) {
            curToken += c;
            while (!lineEnded()){
                c = this.currentChar();
                this.goNextChar();
                if (Character.isLetter(c) || Character.isDigit(c)
                        || c == '_'){
                    curToken += c;
                }
                else {
                    this.goPrevChar();
                    break;
                }
            }
            if (curToken.equals("int")){
                type = Token.T_INT;
            }
            else if (curToken.equals("void")){
                type = Token.T_VOID;
            }
            else if (curToken.equals("string")){
                type = Token.T_KWSTRING;
            }
            else if (curToken.equals("if")){
                type = Token.T_IF;
            }
            else if (curToken.equals("else")){
                type = Token.T_ELSE;
            }
            else if (curToken.equals("while")){
                type = Token.T_WHILE;
            }
            else if (curToken.equals("return")){
                type = Token.T_RETURN;
            }
            else if (curToken.equals("write")){
                type = Token.T_WRITE;
            }
            else if (curToken.equals("writeln")){
                type = Token.T_WRITELN;
            }
            else if (curToken.equals("read")){
                type = Token.T_READ;
            }
            else {
                type = Token.T_ID;
            }
        }
        else {
            curToken += c;
            if (curToken.equals(";")){
                type = Token.T_SEMI;
            }
            else if (curToken.equals(",")){
                type = Token.T_COMMA;
            }
            else if (curToken.equals("[")){
                type = Token.T_LBRAC;
            }
            else if (curToken.equals("]")){
                type = Token.T_RBRAC;
            }
            else if (curToken.equals("{")){
                type = Token.T_LCURL;
            }
            else if (curToken.equals("}")){
                type = Token.T_RCURL;
            }
            else if (curToken.equals("(")){
                type = Token.T_LPAREN;
            }
            else if (curToken.equals(")")){
                type = Token.T_RPAREN;
            }
            else if (curToken.equals("<")){
                if (this.currentCharEquals('=')) {
                    type = Token.T_LESSEQ;
                    curToken += "=";
                    this.goNextChar();
                }
                else{
                    type = Token.T_LESS;
                }
            }
            else if (curToken.equals("=")){
                if (this.currentCharEquals('=')) {
                    type = Token.T_EQEQ;
                    curToken += "=";
                    this.goNextChar();
                }
                else{
                    type = Token.T_EQ;
                }
            }
            else if (curToken.equals(">")){
                if (this.currentCharEquals('=')) {
                    type = Token.T_GREQ;
                    curToken += "=";
                    this.goNextChar();
                }
                else{
                    type = Token.T_GR;
                }
            }
            else if (curToken.equals("!")){
                if (this.currentCharEquals('=')) {
                    type = Token.T_NE;
                    curToken += "=";
                    this.goNextChar();
                }
                else{
                    throw new BPLScannerException(this.curLineNum, "illegal token !");
                }
            }
            else if (curToken.equals("+")){
                type = Token.T_PLUS;
            }
            else if (curToken.equals("-")){
                type = Token.T_MINUS;
            }
            else if (curToken.equals("*")){
                type = Token.T_MULT;
            }
            else if (curToken.equals("/")){
                type = Token.T_DIV;
            }
            else if (curToken.equals("%")){
                type = Token.T_PERCENT;
            }
            else if (curToken.equals("&")){
                type = Token.T_AMP;
            }
            else {
                throw new BPLScannerException(this.curLineNum, "illegal character ascii " + (int)c);
            }
        }

        return new Token(type, curToken, this.curLineNum);
    }

    /** Private methods */

    private boolean hasNextLine(){
        return this.scanner.hasNextLine();
    }

    private void goNextChar(){
        this.curIndex++;
    }

    private void goPrevChar(){
        this.curIndex--;
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

    /** End of private methods */

    public static void main(String args[]) throws BPLScannerException {
        String fileName = args[0];
        BPLScanner scanner = new BPLScanner(fileName);
        while (scanner.hasNextToken()){
            System.out.println(scanner.getNextToken());
        }
    }
}
