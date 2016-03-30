import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
  TokenTest.class,
  BPLScannerTest.class,
  BPLParseTreeNodeTest.class,
  BPLParserTest.class,
  BPLTypeCheckerTest.class,
})

public class BPLCompilerTestSuite {
  // the class remains empty,
  // used only as a holder for the above annotations
}
