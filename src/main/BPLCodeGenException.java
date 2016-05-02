public class BPLCodeGenException extends Exception{
    public BPLCodeGenException(String message) {
        super(message);
    }

    public BPLCodeGenException(int lineNumber, String message) {
        super("Line " + lineNumber + ": " + message);
    }
}
