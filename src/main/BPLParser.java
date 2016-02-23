public class BPLParser {
    private BPLScanner scanner;
    private BPLParseTreeNode tree;
    private Token cached;

    public BPLParser(String fileName) throws BPLParserException {
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
            s += "\n" + this.toStringHelper(tree.getChild(i), depth + 1);
        }
        return s;
    }

    /** BPL grammar rules */
    private BPLParseTreeNode parse() throws BPLParserException {
        //BPLParseTreeNode funDec = this.funDec();
        BPLParseTreeNode funDec = this.funDec();
        BPLParseTreeNode program = new BPLParseTreeNode("PROGRAM",
            1, funDec.getLineNumber());
        program.setChild(0, funDec);
        return program;
    }

    // need declaration list, declaration, var dec, local decs, params

    private BPLParseTreeNode funDec() throws BPLParserException {
        // needs to get TYPE_SPECIFIER, <id>, (PARAMS) before COMPOUND_STMT
        BPLParseTreeNode compound = this.compoundStatement();
        BPLParseTreeNode funDec = new BPLParseTreeNode("FUNCTION",
            4, compound.getLineNumber()); //need to fix linenumber
        funDec.setChild(0, new BPLParseTreeNode("<empty>", 0, -1)); //need fix
        funDec.setChild(1, new BPLParseTreeNode("<empty>", 0, -1)); //need fix
        funDec.setChild(2, new BPLParseTreeNode("<empty>", 0, -1)); //need fix
        funDec.setChild(3, compound);
        return funDec;
    }

    private BPLParseTreeNode statementList() throws BPLParserException {
        Token t = this.getNextToken();
        if (t.getType() == Token.T_RCURL) {
            return new BPLParseTreeNode("<empty>", 0, t.getLineNumber());
        }

        this.cacheToken(t);
        BPLParseTreeNode stmt = this.statement();
        BPLParseTreeNode childList = this.statementList();

        BPLParseTreeNode curlist = new BPLParseTreeNode("STATEMENT_LIST",
            2, stmt.getLineNumber());
        curlist.setChild(0, stmt);
        curlist.setChild(1, childList);
        return curlist;
    }

    private BPLParseTreeNode statement() throws BPLParserException {
        Token t = this.getNextToken();
        BPLParseTreeNode node = null;
        if (t.getType() == Token.T_LCURL) {
            this.cacheToken(t);
            node = this.compoundStatement();
        }
        else {
            this.cacheToken(t);
            node = this.expressionStatement();
        }

        BPLParseTreeNode stmt = new BPLParseTreeNode("STATEMENT",
            1, node.getLineNumber());
        stmt.setChild(0, node);
        return stmt;
    }

    private BPLParseTreeNode compoundStatement() throws BPLParserException {
        Token t = this.getNextToken();
        int lineNum = t.getLineNumber();
        if (t.getType() != Token.T_LCURL) {
            throw new BPLParserException(lineNum,
                "Unsupported grammar rule.");
        }

        // TODO: make local dec a child
        BPLParseTreeNode localDec = new BPLParseTreeNode(null, 0, -1);
        BPLParseTreeNode stmtList = this.statementList();
        BPLParseTreeNode node = new BPLParseTreeNode("COMPOUND_STATEMENT",
            2, lineNum);
        node.setChild(0, localDec);
        node.setChild(1, stmtList);

        return node;
    }

    private BPLParseTreeNode expressionStatement() throws BPLParserException {
        Token t = this.getNextToken();
        if (t.getType() == Token.T_SEMI){
            return new BPLParseTreeNode("<empty>", 0, t.getLineNumber());
        }

        this.cacheToken(t);
        BPLParseTreeNode exp = this.expression();
        BPLParseTreeNode expStmt = new BPLParseTreeNode("EXPRESSION_STMT",
            1, exp.getLineNumber());
        expStmt.setChild(0, exp);

        if (!this.hasNextToken()){
            throw new BPLParserException(exp.getLineNumber(),
                "Unsupported grammar rule.");
        }
        t = this.getNextToken();
        if (t.getType() != Token.T_SEMI){
            throw new BPLParserException(exp.getLineNumber(),
                "Unsupported grammar rule.");
        }

        return expStmt;
    }

    private BPLParseTreeNode expression() throws BPLParserException {
        Token t = this.getNextToken();
        if (t.getType() != Token.T_ID){
            throw new BPLParserException(t.getLineNumber(),
                "Unsupported grammar rule.");
        }

        VariableNode node = new VariableNode(t.getValue(), t.getLineNumber());

        return node;
    }

    /** Methods to get tokens */
    private boolean hasNextToken() throws BPLParserException {
        if (cached != null){
            return true;
        }
        try {
            return this.scanner.hasNextToken();
        }
        catch(Exception e) {
            throw new BPLParserException("Scanner has no more tokens");
        }
    }

    private Token getNextToken() throws BPLParserException {
        if (this.cached != null){
            Token temp = cached;
            cached = null;
            return temp;
        }
        try {
            return this.scanner.getNextToken();
        }
        catch(Exception e) {
            throw new BPLParserException("Scanner ran out of tokens");
        }
    }

    private void cacheToken(Token t) throws BPLParserException {
        if (this.cached != null){
            throw new BPLParserException("Cannot cache more than 1 token");
        }
        cached = t;
    }

    /** End of private methods */

    public static void main(String args[]) throws BPLParserException{
        String fileName = args[0];
        BPLParser parser = new BPLParser(fileName);
        System.out.println(parser.toString());
    }
}
