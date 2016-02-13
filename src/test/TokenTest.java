import org.junit.Test;
import static org.junit.Assert.*;


public class TokenTest {
    @Test
    public void testTokenize(){
        Token t = new Token(5, "meow", 12);
        assertNotNull(t);
        assertEquals(t.getType(), 5);
        assertEquals(t.getValue(), "meow");
        assertEquals(t.getLineNumber(), 12);
    }

    @Test
    public void testToString(){
        Token t = new Token(5, "meow", 12);
        assertEquals("Token 5, string meow, line number 12", t.toString());
    }

    @Test
    public void testIsKind(){
        Token t = new Token(5, "meow", 12);
        assertTrue(t.isType(5));
        assertFalse(t.isType(12));
    }
}
