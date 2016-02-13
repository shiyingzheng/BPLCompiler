public class BPLScannerException extends Exception{
    public BPLScannerException(int lineNumber, String message){
        super("Line " + lineNumber + ": " + message);
    }
}
