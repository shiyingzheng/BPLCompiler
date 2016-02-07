import org.junit.Test;
import static org.junit.Assert.*;

public class TokenTest {
    @Test
    public void testTokenize(){
        Token t = new Token(5, "meow", 12);
        assertNotNull(t);
        assertEquals(t.kind, 5);
        assertEquals(t.value, "meow");
        assertEquals(t.line_num, 12);
    }
}
