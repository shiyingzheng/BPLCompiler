import java.io.*;
import java.util.Scanner;


class BPLScanner {
    private Scanner scanner;
    private PushbackInputStream input;

    private String curLine;
    private int curLineNum;
    private String prevChar;
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

    public boolean hasNextToken(){
        if (curLine == null || curIndex + 1 >= curLine.length()){
            while (this.scanner.hasNextLine()){
                curLine = this.scanner.nextLine();
                if (curLine.trim() != ""){
                    this.curLine = "";
                    this.curIndex = 0;
                    this.curLineNum += 1;
                    return true;
                }
            }
        }
        else if (curIndex + 1 < curLine.length()){
            return true;
        }
        return false;
    }

    public Token getNextToken(){
        if (!hasNextLine()){
            //throw exception
        }

        String curToken = "";
        if (prevChar != null) {
            curToken += prevChar;
            prevChar = null;
        }



        // while there is something left, tokenize it
        // if not, set hasNextToken to False
        // things to throw exceptions for:
        //   open and closed comments
        //   open and closed strings on one line
        //   illegal character
        //   token class will throw token type does not exist exception
        return null;
    }

    public static void main(String args[]){
        String fileName = args[0];
        BPLScanner scanner = new BPLScanner(fileName);
        if (scanner.hasNextToken()){
            System.out.println("meow");
        }
        // take in argument
        // call getNextToken until there's none left, catch exception
    }
}
