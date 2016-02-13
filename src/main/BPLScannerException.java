public class BPLScannerException extends Exception{
    public BPLScannerException(int lineNumber, String message){
        super("Exception at line " + lineNumber + ": " + message);
    }
}
