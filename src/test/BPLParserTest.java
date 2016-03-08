import org.junit.Test;
import static org.junit.Assert.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;


public class BPLParserTest {
    @Test
    public void testGetParseTree() throws BPLParserException {
        BPLParser parser = new BPLParser("testfiles/parserTests/ex1");
        assertNotNull(parser.getParseTree());
    }

    @Test
    public void testNoExceptions() throws BPLParserException {
        BPLParser parser = new BPLParser("testfiles/parserTests/factor");
        parser = new BPLParser("testfiles/parserTests/ex1");
        parser = new BPLParser("testfiles/parserTests/ex2");
        parser = new BPLParser("testfiles/parserTests/ex3");
        assertTrue(true);
    }

    @Test
    public void testAssociativity() throws BPLParserException, IOException {
        BPLParser parser = new BPLParser("testfiles/parserTests/associativity");
        String output = parser.toString();
        String expected = new String(Files.readAllBytes(
            Paths.get("testfiles/parserTests/associativity2")));
        assertEquals(expected, output);
    }

    @Test
    public void testFactor() throws BPLParserException, IOException {
        BPLParser parser = new BPLParser("testfiles/parserTests/factor");
        String output = parser.toString();
        String expected = new String(Files.readAllBytes(
            Paths.get("testfiles/parserTests/factor2")));
        assertEquals(expected, output);
    }

    @Test(expected=BPLParserException.class)
    public void testMissingSemicolon() throws BPLParserException {
        BPLParser parser = new BPLParser("testfiles/parserTests/missing_semicolon");
        parser.getParseTree();
    }

    @Test(expected=BPLParserException.class)
    public void testWeirdIf() throws BPLParserException {
        BPLParser parser = new BPLParser("testfiles/parserTests/weird_if");
        parser.getParseTree();
    }

    @Test(expected=BPLParserException.class)
    public void testNoClosingCurlyBrace() throws BPLParserException {
        BPLParser parser = new BPLParser("testfiles/parserTests/compound_statement_exception");
        parser.getParseTree();
    }
}
