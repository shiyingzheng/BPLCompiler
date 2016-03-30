public class BPLTypeCheckerException extends Exception{
    public BPLTypeCheckerException(String message) {
        super(message);
    }

    public BPLTypeCheckerException(int lineNumber, String message) {
        super("Line " + lineNumber + ": " + message);
    }
}
