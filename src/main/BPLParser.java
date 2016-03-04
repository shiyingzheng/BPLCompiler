import java.util.LinkedList;


public class BPLParser {
    private BPLScanner scanner;
    private BPLParseTreeNode tree;
    private LinkedList<Token> cached;
    private boolean DEBUG = false; // TODO: remove excessive print statements

    public BPLParser(String fileName) throws BPLParserException {
        this.scanner = new BPLScanner(fileName);
        cached = new LinkedList<Token>();
        this.tree = this.parse();
    }

    public BPLParseTreeNode getParseTree(){
        return this.tree;
    }

    public String toString(){
        return this.toStringHelper(this.tree, 0);
    }

    /** Private functions below */

    private String toStringHelper(BPLParseTreeNode tree, int depth){
        String s = "";
        for (int i = 0; i < depth; i++){
            s += "  ";
        }
        s += tree.toString() + "\n";
        for (int i = 0; i < tree.numChildren(); i++){
            s += this.toStringHelper(tree.getChild(i), depth + 1);
        }
        return s;
    }

    /** BPL grammar rules */
    private BPLParseTreeNode parse() throws BPLParserException {
        BPLParseTreeNode decList = this.declarationList();
        BPLParseTreeNode program = new BPLParseTreeNode("PROGRAM",
            1, decList.getLineNumber());
        program.setChild(0, decList);
        return program;
    }

    private BPLParseTreeNode declarationList() throws BPLParserException {
        BPLParseTreeNode declaration = this.declaration();
        BPLParseTreeNode list = null;

        if (this.hasNextToken()){
            list = new BPLParseTreeNode("DECLARATION_LIST",
                2, declaration.getLineNumber());
            BPLParseTreeNode rest = declarationList();
            list.setChild(0, declaration);
            list.setChild(1, rest);
        }
        else {
            list = new BPLParseTreeNode("DECLARATION_LIST",
                1, declaration.getLineNumber());
            list.setChild(0, declaration);
        }

        return list;
    }

    private BPLParseTreeNode declaration() throws BPLParserException {
        Token t = this.getNextToken();
        Token s = this.getNextToken();
        Token p = this.getNextToken();
        this.cacheToken(t);
        this.cacheToken(s);
        this.cacheToken(p);

        BPLParseTreeNode dec = null;
        if (p.isType(Token.T_LPAREN)){
            BPLParseTreeNode funDec = this.funDec();
            dec = new BPLParseTreeNode("DECLARATION", 1,
                funDec.getLineNumber());
            dec.setChild(0, funDec);
        }
        else {
            BPLParseTreeNode varDec = this.varDec();
            dec = new BPLParseTreeNode("DECLARATION", 1,
                varDec.getLineNumber());
            dec.setChild(0, varDec);
        }
        return dec;
    }

    private BPLParseTreeNode varDec() throws BPLParserException {
        BPLParseTreeNode specifier = this.typeSpecifier();

        Token t = this.getNextToken();
        int lineNum = t.getLineNumber();

        BPLParseTreeNode varDec = null;

        if (t.isType(Token.T_MULT)){
            t = this.getNextToken();
            VariableNode var = this.var(t);
            varDec = new BPLParseTreeNode("VARIABLE_DECLARATION",
                3, lineNum);
            varDec.setChild(0, specifier);
            varDec.setChild(1, new BPLParseTreeNode("*", 0, t.getLineNumber()));
            varDec.setChild(2, var);
        }
        else {
            VariableNode var = this.var(t);

            t = this.getNextToken();
            if (t.isType(Token.T_LBRAC)){
                t = this.getNextToken();
                assertTokenType(t, Token.T_NUM);
                IntValueNode num = new IntValueNode(t.getValue(),
                    t.getLineNumber());
                t = this.getNextToken();
                assertTokenType(t, Token.T_RBRAC);
                varDec = new BPLParseTreeNode("VARIABLE_DECLARATION",
                    5, lineNum);
                varDec.setChild(0, specifier);
                varDec.setChild(1, var);
                varDec.setChild(2, new BPLParseTreeNode("[", 0,
                    t.getLineNumber()));
                varDec.setChild(3, num);
                varDec.setChild(4, new BPLParseTreeNode("]", 0,
                    t.getLineNumber()));
            }
            else {
                this.cacheToken(t);
                varDec = new BPLParseTreeNode("VARIABLE_DECLARATION",
                    2, lineNum);
                varDec.setChild(0, specifier);
                varDec.setChild(1, var);
            }
        }

        t = this.getNextToken();
        assertTokenType(t, Token.T_SEMI);

        return varDec;
    }

    private BPLParseTreeNode localDec() throws BPLParserException {
        Token t = this.getNextToken();
        this.cacheToken(t);
        if (!t.isTypeSpecifier()){
            return new BPLParseTreeNode("<empty>", 0, t.getLineNumber());
        }

        BPLParseTreeNode varDec = this.varDec();
        BPLParseTreeNode rest = this.localDec();
        BPLParseTreeNode dec = new BPLParseTreeNode("LOCAL_DEC", 2,
            varDec.getLineNumber());
        dec.setChild(0, varDec);
        dec.setChild(1, rest);
        return dec;
    }

    private BPLParseTreeNode params() throws BPLParserException {
        Token t = this.getNextToken();
        if (!t.isType(Token.T_LPAREN)){
            throw new BPLParserException(t.getLineNumber(),
                "Illegal function params");
        }

        t = this.getNextToken();

        BPLParseTreeNode p = null;
        if (t.isType(Token.T_VOID)){
            p = new BPLParseTreeNode("<empty>", 0, t.getLineNumber());
            t = this.getNextToken();
            if (!t.isType(Token.T_RPAREN)){
                throw new BPLParserException(t.getLineNumber(),
                    "Illegal function params");
            }
        }
        else {
            this.cacheToken(t);
            p = this.paramList();
        }
        BPLParseTreeNode params = new BPLParseTreeNode("PARAMS", 1,
            p.getLineNumber());
        params.setChild(0, p);
        return params;
    }

    private BPLParseTreeNode paramList() throws BPLParserException {
        Token t = this.getNextToken();
        if (t.isType(Token.T_RPAREN)){
            return new BPLParseTreeNode("<empty>", 0, t.getLineNumber());
        }

        this.cacheToken(t);
        BPLParseTreeNode param = this.param();
        t = this.getNextToken();
        if (!t.isType(Token.T_COMMA) && !t.isType(Token.T_RPAREN)){
            throw new BPLParserException(t.getLineNumber(),
                "Illegal function params");
        }
        if (t.isType(Token.T_RPAREN)){
            this.cacheToken(t);
        }

        BPLParseTreeNode rest = this.paramList();
        BPLParseTreeNode list = new BPLParseTreeNode("PARAM_LIST", 2,
            param.getLineNumber());
        list.setChild(0, param);
        list.setChild(1, rest);
        return list;
    }

    private BPLParseTreeNode param() throws BPLParserException {
        BPLParseTreeNode specifier = this.typeSpecifier();
        VariableNode var = this.var(this.getNextToken());
        BPLParseTreeNode param = new BPLParseTreeNode("PARAM", 2,
            specifier.getLineNumber());
        param.setChild(0, specifier);
        param.setChild(1, var);
        return param;
    }

    private BPLParseTreeNode typeSpecifier() throws BPLParserException {
        Token t = this.getNextToken();
        String nodeType = this.typeSpecifierString(t);
        if (nodeType == null){
            throw new BPLParserException(t.getLineNumber(),
                "Invalid type specifier");
        }

        BPLParseTreeNode node = new BPLParseTreeNode(nodeType, 0,
            t.getLineNumber());
        return node;
    }

    private BPLParseTreeNode funDec() throws BPLParserException {
        BPLParseTreeNode specifier = this.typeSpecifier();
        VariableNode id = this.var(this.getNextToken());
        BPLParseTreeNode params = this.params();
        BPLParseTreeNode compound = this.compoundStatement();
        BPLParseTreeNode funDec = new BPLParseTreeNode("FUNCTION",
            4, specifier.getLineNumber());
        funDec.setChild(0, specifier);
        funDec.setChild(1, id);
        funDec.setChild(2, params);
        funDec.setChild(3, compound);
        return funDec;
    }

    private BPLParseTreeNode statementList() throws BPLParserException {
        if (DEBUG){
            System.out.println("statement list");
        }
        Token t = this.getNextToken();
        if (t.isType(Token.T_RCURL)) {
            if (DEBUG){
                System.out.println("end of statement list");
            }
            return new BPLParseTreeNode("<empty>", 0, t.getLineNumber());
        }

        this.cacheToken(t);
        BPLParseTreeNode stmt = this.statement();
        BPLParseTreeNode childList = this.statementList();

        BPLParseTreeNode curlist = new BPLParseTreeNode("STATEMENT_LIST",
            2, stmt.getLineNumber());
        curlist.setChild(0, stmt);
        curlist.setChild(1, childList);
        if (DEBUG){
            System.out.println("end of statement list");
        }
        return curlist;
    }

    private BPLParseTreeNode statement() throws BPLParserException {
        if (DEBUG){
            System.out.println("statement");
        }
        Token t = this.getNextToken();
        this.cacheToken(t);

        BPLParseTreeNode node = null;
        if (t.isType(Token.T_LCURL)) {
            node = this.compoundStatement();
        }
        else if (t.isType(Token.T_IF)){
            node = this.ifStatement();
        }
        else if (t.isType(Token.T_WHILE)){
            node = this.whileStatement();
        }
        else if (t.isType(Token.T_RETURN)){
            node = this.returnStatement();
        }
        else if (t.isType(Token.T_WRITE) || t.isType(Token.T_WRITELN)){
            node = this.writeStatement();
        }
        else {
            node = this.expressionStatement();
        }

        BPLParseTreeNode stmt = new BPLParseTreeNode("STATEMENT",
            1, node.getLineNumber());
        stmt.setChild(0, node);

        if (DEBUG){
            System.out.println("end of statement");
        }

        return stmt;
    }

    private BPLParseTreeNode compoundStatement() throws BPLParserException {
        if (DEBUG){
            System.out.println("compound statement");
        }
        Token t = this.getNextToken();
        int lineNum = t.getLineNumber();
        assertTokenType(t, Token.T_LCURL);

        BPLParseTreeNode localDec = this.localDec();
        BPLParseTreeNode stmtList = this.statementList();
        BPLParseTreeNode node = new BPLParseTreeNode("COMPOUND_STATEMENT",
            2, lineNum);
        node.setChild(0, localDec);
        node.setChild(1, stmtList);

        if (DEBUG){
            System.out.println("end of compound statement");
        }

        return node;
    }

    private BPLParseTreeNode expressionStatement() throws BPLParserException {
        if (DEBUG){
            System.out.println("expression statement");
        }

        Token t = this.getNextToken();
        if (t.isType(Token.T_SEMI)){
            return new BPLParseTreeNode("<empty>", 0, t.getLineNumber());
        }

        this.cacheToken(t);
        BPLParseTreeNode exp = this.expression();
        BPLParseTreeNode expStmt = new BPLParseTreeNode("EXPRESSION_STMT",
            1, exp.getLineNumber());
        expStmt.setChild(0, exp);

        t = this.getNextToken();
        assertTokenType(t, Token.T_SEMI);

        if (DEBUG){
            System.out.println(toStringHelper(expStmt, 0));
        }

        return expStmt;
    }

    private BPLParseTreeNode ifStatement() throws BPLParserException {
        Token t = this.getNextToken();

        if (DEBUG){
            System.out.println("if statement");
        }

        assertTokenType(t, Token.T_IF);
        int lineNum = t.getLineNumber();

        t = this.getNextToken();
        assertTokenType(t, Token.T_LPAREN);

        BPLParseTreeNode exp = this.expression();
        t = this.getNextToken();
        assertTokenType(t, Token.T_RPAREN);

        BPLParseTreeNode stmt = this.statement();

        t = this.getNextToken();
        if (t.isType(Token.T_ELSE)){
            BPLParseTreeNode elseStmt = this.statement();
            BPLParseTreeNode ifStmt = new BPLParseTreeNode("IF_STATEMENT",
                3, lineNum);
            ifStmt.setChild(0, exp);
            ifStmt.setChild(1, stmt);
            ifStmt.setChild(2, elseStmt);
            return ifStmt;
        }

        this.cacheToken(t);
        BPLParseTreeNode ifStmt = new BPLParseTreeNode("IF_STATEMENT",
                2, lineNum);
        ifStmt.setChild(0, exp);
        ifStmt.setChild(1, stmt);

        return ifStmt;
    }

    private BPLParseTreeNode whileStatement() throws BPLParserException {
        Token t = this.getNextToken();
        assertTokenType(t, Token.T_WHILE);
        int lineNum = t.getLineNumber();

        t = this.getNextToken();
        assertTokenType(t, Token.T_LPAREN);

        BPLParseTreeNode exp = this.expression();
        t = this.getNextToken();
        assertTokenType(t, Token.T_RPAREN);

        BPLParseTreeNode stmt = this.statement();

        BPLParseTreeNode whileStmt = new BPLParseTreeNode("IF_STATEMENT",
                2, lineNum);
        whileStmt.setChild(0, exp);
        whileStmt.setChild(1, stmt);

        return whileStmt;
    }

    private BPLParseTreeNode returnStatement() throws BPLParserException {
        Token t = this.getNextToken();
        assertTokenType(t, Token.T_RETURN);
        int lineNum = t.getLineNumber();

        t = this.getNextToken();
        if (t.isType(Token.T_SEMI)){
            BPLParseTreeNode ret = new BPLParseTreeNode("RETURN_STATEMENT",
                0, lineNum);
        }

        this.cacheToken(t);
        BPLParseTreeNode exp = this.expression();

        t = this.getNextToken();
        assertTokenType(t, Token.T_SEMI);

        BPLParseTreeNode ret = new BPLParseTreeNode("RETURN_STATEMENT",
            1, lineNum);
        ret.setChild(0, exp);
        return ret;
    }

    private BPLParseTreeNode writeStatement() throws BPLParserException {
        Token t = this.getNextToken();
        int lineNum = t.getLineNumber();
        if (t.isType(Token.T_WRITELN)){
            t = this.getNextToken();
            assertTokenType(t, Token.T_LPAREN);
            t = this.getNextToken();
            assertTokenType(t, Token.T_RPAREN);
            t = this.getNextToken();
            assertTokenType(t, Token.T_SEMI);
            BPLParseTreeNode writeln = new BPLParseTreeNode("WRITELN_STATEMENT",
                0, lineNum);
            return writeln;
        }

        t = this.getNextToken();
        assertTokenType(t, Token.T_LPAREN);

        BPLParseTreeNode exp = this.expression();

        t = this.getNextToken();
        assertTokenType(t, Token.T_RPAREN);
        t = this.getNextToken();
        assertTokenType(t, Token.T_SEMI);

        BPLParseTreeNode write = new BPLParseTreeNode("WRITE_STATEMENT",
            1, lineNum);
        write.setChild(0, exp);

        return write;
    }

    private BPLParseTreeNode expression() throws BPLParserException {
        BPLParseTreeNode var = this.var(this.getNextToken());
        BPLParseTreeNode exp = new BPLParseTreeNode("EXPRESSION",
            1, var.getLineNumber());
        exp.setChild(0, var);
        return exp;
    }

    private VariableNode var(Token t) throws BPLParserException {
        assertTokenType(t, Token.T_ID);
        return new VariableNode(t.getValue(), t.getLineNumber());
    }

    /** Methods to get tokens */
    private boolean hasNextToken() throws BPLParserException {
        if (cached.size() > 0){
            return true;
        }
        try {
            return this.scanner.hasNextToken();
        }
        catch(Exception e) {
            String lineNum = e.getMessage().split(":")[0];
            throw new BPLParserException(lineNum + ": Unexpected EOF");
        }
    }

    private Token getNextToken() throws BPLParserException {
        if (cached.size() > 0){
            Token t = cached.poll();
            return t;
        }
        try {
            Token t = this.scanner.getNextToken();
            return t;
        }
        catch(Exception e) {
            String lineNum = e.getMessage().split(":")[0];
            throw new BPLParserException(lineNum + ": Unexpected EOF");
        }
    }

    private void cacheToken(Token t) throws BPLParserException {
        cached.offer(t);
    }

    private String typeSpecifierString(Token t){
        if (t.isType(Token.T_INT)){
            return "INT_SPECIFIER";
        }
        if (t.isType(Token.T_VOID)){
            return "VOID_SPECIFIER";
        }
        if (t.isType(Token.T_KWSTRING)){
            return "STRING_SPECIFIER";
        }
        return null;
    }

    private void assertTokenType(Token t, int type) throws BPLParserException {
        if (!t.isType(type)){
            throw new BPLParserException(t.getLineNumber(),
                "Unsupported grammar rule at \"" + t.getValue() + "\"");
        }
    }

    /** End of private methods */

    public static void main(String args[]) throws BPLParserException{
        String fileName = args[0];
        BPLParser parser = new BPLParser(fileName);
        System.out.println(parser.toString());
    }
}
