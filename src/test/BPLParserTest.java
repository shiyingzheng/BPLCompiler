import org.junit.Test;
import static org.junit.Assert.*;


public class BPLParserTest {
    @Test
    public void testGetParseTree() throws BPLParserException {
        BPLParser parser = new BPLParser("testfiles/parserTests/simplest_id_test");
        assertNotNull(parser.getParseTree());
    }

    @Test
    public void testStatement() throws BPLParserException {
        BPLParser parser = new BPLParser("testfiles/parserTests/simplest_id_test");
        assertEquals(parser.toString(), "Line 1: PROGRAM\n" +
            "\tLine 1: STATEMENT_LIST\n" +
            "\t\tLine 1: STATEMENT\n" +
            "\t\t\tLine 1: EXPRESSION_STMT\n" +
            "\t\t\t\tLine 1: VariableNode id = x\n" +
            "\t\tLine -1: <empty>"
        );
    }

    @Test(expected=BPLParserException.class)
    public void testNoClosingCurlyBrace() throws BPLParserException {
        BPLParser parser = new BPLParser("testfiles/parserTests/compound_statement_exception");
        parser.getParseTree();
    }
}
