import org.junit.Test;
import static org.junit.Assert.*;


public class BPLParseTreeNodeTest {
    @Test
    public void testBPLParseTreeNode() {
        BPLParseTreeNode node = new BPLParseTreeNode("<empty>", 0, 0);
        assertNotNull(node);
        assertEquals(node.toString(), "Line 0: <empty>");
    }
}
