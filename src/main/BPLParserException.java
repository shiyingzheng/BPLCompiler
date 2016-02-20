public class BPLParserException extends Exception{
    public BPLParserException(String message){
        super(message);
    }

    public BPLParserException(int lineNumber, String message){
        super("Line " + lineNumber + ": " + message);
    }
}
