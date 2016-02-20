public class BPLParser {
    private BPLScanner scanner;
    private BPLParseTreeNode tree;
    private Token cached;

    public BPLParser(String fileName)
            throws BPLParserException, BPLScannerException {
        this.scanner = new BPLScanner(fileName);
        this.tree = this.parse();
    }

    public BPLParseTreeNode getParseTree(){
        return this.tree;
    }

    public String toString(){
        return this.toStringHelper(this.tree, 0);
    }

    private String toStringHelper(BPLParseTreeNode tree, int depth){
        String s = "";
        for (int i = 0; i < depth; i++){
            s += "\t";
        }
        s += tree.toString();
        for (int i = 0; i < tree.numChildren(); i++){
            s += this.toStringHelper(tree.getChild(i), depth + 1);
        }
        return s;
    }

    /** BPL grammar rules */
    private BPLParseTreeNode parse()
             throws BPLParserException, BPLScannerException {
        BPLParseTreeNode stmt = this.statement();
        BPLParseTreeNode program = new BPLParseTreeNode("PROGRAM",
            1, stmt.getLineNumber());
        program.setChild(0, stmt);
        return program;
    }

    private BPLParseTreeNode statement()
            throws BPLParserException, BPLScannerException {
        BPLParseTreeNode expStmt = this.expressionStatement();
        BPLParseTreeNode stmt = new BPLParseTreeNode("STATEMENT",
            1, expStmt.getLineNumber());
        stmt.setChild(0, expStmt);
        return stmt;
    }

    private BPLParseTreeNode expressionStatement()
            throws BPLParserException, BPLScannerException {

        BPLParseTreeNode exp = this.expression();
        BPLParseTreeNode expStmt = new BPLParseTreeNode("EXPRESSION_STMT",
            1, exp.getLineNumber());
        expStmt.setChild(0, exp);

        if (this.hasNextToken()){
            Token t = this.getNextToken();
            if (t.getType() != Token.T_SEMI){
                throw new BPLParserException(exp.getLineNumber(),
                    "Unsupported grammar rule.");
            }
        }
        else {
            throw new BPLParserException(exp.getLineNumber(),
                "Expecting more tokens.");
        }

        return expStmt;
    }

    private BPLParseTreeNode expression()
            throws BPLScannerException, BPLParserException {
        if (!this.hasNextToken()){
            throw new BPLParserException("Expecting more tokens.");
        }

        Token t = this.getNextToken();
        if (t.getType() != Token.T_ID){
            throw new BPLParserException("Unsupported grammar rule.");
        }

        VariableNode node = new VariableNode(t.getValue(), t.getLineNumber());

        return node;
    }

    /** Methods to get tokens */
    private boolean hasNextToken() throws BPLScannerException {
        if (cached != null){
            return true;
        }
        return this.scanner.hasNextToken();
    }

    private Token getNextToken() throws BPLScannerException {
        if (this.cached != null){
            Token temp = cached;
            cached = null;
            return temp;
        }
        return this.scanner.getNextToken();
    }

    private void cacheToken(Token t) throws BPLParserException {
        if (this.cached != null){
            throw new BPLParserException("Cannot cache more than 1 token");
        }
        cached = t;
    }

    /** End of private methods */

    public static void main(String args[])
            throws BPLScannerException, BPLParserException{
        String fileName = args[0];
        BPLParser parser = new BPLParser(fileName);
        System.out.println(parser.toString());
    }
}
