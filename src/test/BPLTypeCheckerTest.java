import org.junit.Test;
import static org.junit.Assert.*;


public class BPLTypeCheckerTest {
    @Test
    public void testTypeChecker() throws BPLTypeCheckerException {
        BPLTypeChecker checker = new BPLTypeChecker("testfiles/ex1",
            false, false);
        assertNotNull(checker);
    }
}
