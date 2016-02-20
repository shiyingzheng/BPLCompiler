import org.junit.Test;
import static org.junit.Assert.*;


public class BPLScannerTest {
    @Test
    public void testGetNextToken() throws BPLScannerException {
        BPLScanner scanner = new BPLScanner("testfiles/scannerTests/testfile");
        assertTrue(scanner.hasNextToken());
        Token t1 = scanner.getNextToken();
        Token t2 = new Token(Token.T_INT, "int", 3);
        assertEquals(t1.getType(), t2.getType());
        assertEquals(t1.getValue(), t2.getValue());
        assertEquals(t1.getLineNumber(), t2.getLineNumber());
    }

    @Test
    public void testGetNextIntToken() throws BPLScannerException {
        BPLScanner scanner = new BPLScanner("testfiles/scannerTests/testfile_num");
        assertTrue(scanner.hasNextToken());
        assertEquals("14342432", scanner.getNextToken().getValue());
        int token_num = 1;
        while(scanner.hasNextToken()){
            token_num++;
            Token t = scanner.getNextToken();
            assertEquals(t.getType(), Token.T_NUM);
        }
        assertEquals(4, token_num);
    }

    @Test
    public void testGetNextStringToken() throws BPLScannerException {
        BPLScanner scanner = new BPLScanner("testfiles/scannerTests/testfile_string");
        assertTrue(scanner.hasNextToken());
        assertEquals("meow", scanner.getNextToken().getValue());
        int token_num = 1;
        while(scanner.hasNextToken()){
            token_num++;
            Token t = scanner.getNextToken();
            assertEquals(t.getType(), Token.T_STRING);
        }
        assertEquals(5, token_num);
    }

    @Test
    public void testGetNextPunctuationToken() throws BPLScannerException {
        BPLScanner scanner = new BPLScanner("testfiles/scannerTests/testfile_symbol");
        assertTrue(scanner.hasNextToken());
        int token_num = 0;
        while(scanner.hasNextToken()){
            Token t = scanner.getNextToken();
            if (t.getType() >= 12 && t.getType() <= 32){
                token_num++;
            }
        }
        assertEquals(21, token_num);
    }

    @Test
    public void testGetNextKeywordToken() throws BPLScannerException {
        BPLScanner scanner = new BPLScanner("testfiles/scannerTests/testfile_keywords");
        assertTrue(scanner.hasNextToken());
        int token_num = 0;
        while(scanner.hasNextToken()){
            Token t = scanner.getNextToken();
            if (t.getType() >= 2 && t.getType() <= 11){
                token_num++;
            }
        }
        assertEquals(10, token_num);
    }

    @Test
    public void testGetNextIDToken() throws BPLScannerException {
        BPLScanner scanner = new BPLScanner("testfiles/scannerTests/testfile_id");
        assertTrue(scanner.hasNextToken());
        int token_num = 0;
        while(scanner.hasNextToken()){
            Token t = scanner.getNextToken();
            assertEquals(t.getType(), Token.T_ID);
            token_num++;
        }
        assertEquals(3, token_num);
    }

    @Test
    public void testGetNextTokenIgnoreComment() throws BPLScannerException {
        BPLScanner scanner = new BPLScanner("testfiles/scannerTests/testfile_comment");
        assertTrue(scanner.hasNextToken());
        int token_num = 0;
        while(scanner.hasNextToken()){
            Token t = scanner.getNextToken();
            assertEquals(Token.T_ID, t.getType());
            token_num++;
        }
        assertEquals(2, token_num);
    }

    @Test(expected=BPLScannerException.class)
    public void testGetNextTokenStringException() throws BPLScannerException {
        BPLScanner scanner = new BPLScanner("testfiles/scannerTests/testfile_string_exception");
        assertTrue(scanner.hasNextToken());
        while(scanner.hasNextToken()){
            Token t = scanner.getNextToken();
        }
    }

    @Test(expected=BPLScannerException.class)
    public void testGetNextTokenCommentException() throws BPLScannerException {
        BPLScanner scanner = new BPLScanner("testfiles/scannerTests/testfile_comment_exception");
        assertTrue(scanner.hasNextToken());
    }

    @Test(expected=BPLScannerException.class)
    public void testGetNextTokenIllegalCharacterException() throws BPLScannerException {
        BPLScanner scanner = new BPLScanner("testfiles/scannerTests/testfile_illegal_character_exception");
        assertTrue(scanner.hasNextToken());
        while(scanner.hasNextToken()){
            Token t = scanner.getNextToken();
        }
    }

    @Test(expected=BPLScannerException.class)
    public void testGetNextTokenSymbolException() throws BPLScannerException {
        BPLScanner scanner = new BPLScanner("testfiles/scannerTests/testfile_exclam");
        assertTrue(scanner.hasNextToken());
        while(scanner.hasNextToken()){
            Token t = scanner.getNextToken();
        }
    }

    @Test(expected=BPLScannerException.class)
    public void testGetNextTokenNoNextTokenException() throws BPLScannerException {
        BPLScanner scanner = new BPLScanner("testfiles/scannerTests/testfile_empty");
        Token t = scanner.getNextToken();
    }
}
