import java.util.LinkedList;
import java.util.ArrayList;


public class BPLParser {
    private BPLScanner scanner;
    private BPLParseTreeNode tree;
    private LinkedList<Token> cache;

    public BPLParser(String fileName) throws BPLParserException {
        this.scanner = new BPLScanner(fileName);
        this.cache = new LinkedList<Token>();
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
        IdNode id = null;

        if (t.isType(Token.T_MULT)){
            t = this.getNextToken();
            id = this.makeID(t);
            varDec = new BPLParseTreeNode("POINTER_VARIABLE_DECLARATION",
                2, lineNum);
        }
        else {
            id = this.makeID(t);
            t = this.getNextToken();
            if (t.isType(Token.T_LBRAC)){
                t = this.getNextToken();
                this.assertTokenType(t, Token.T_NUM);
                IntValueNode num = new IntValueNode(t.getValue(),
                    t.getLineNumber());
                t = this.getNextToken();
                this.assertTokenType(t, Token.T_RBRAC);
                varDec = new BPLParseTreeNode("ARRAY_VARIABLE_DECLARATION",
                    3, lineNum);
                varDec.setChild(2, num);
            }
            else {
                this.cacheToken(t);
                varDec = new BPLParseTreeNode("VARIABLE_DECLARATION",
                    2, lineNum);
            }
        }

        varDec.setChild(0, specifier);
        varDec.setChild(1, id);

        t = this.getNextToken();
        this.assertTokenType(t, Token.T_SEMI);

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
        Token t = this.getNextToken();
        int lineNum = t.getLineNumber();

        BPLParseTreeNode param = null;
        IdNode id = null;

        if (t.isType(Token.T_MULT)){
            id = this.makeID(this.getNextToken());
            param = new BPLParseTreeNode("POINTER_PARAM", 2,
                specifier.getLineNumber());
        }
        else{
            id = this.makeID(t);
            t = this.getNextToken();
            if (t.isType(Token.T_LBRAC)){
                t = this.getNextToken();
                this.assertTokenType(t, Token.T_RBRAC);
                param = new BPLParseTreeNode("ARRAY_VARIABLE_DECLARATION",
                    2, lineNum);
            }
            else {
                this.cacheToken(t);
                param = new BPLParseTreeNode("VARIABLE_DECLARATION",
                    2, lineNum);
            }
        }

        param.setChild(0, specifier);
        param.setChild(1, id);

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
        IdNode id = this.makeID(this.getNextToken());
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
        Token t = this.getNextToken();
        if (t.isType(Token.T_RCURL)) {
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

        return stmt;
    }

    private BPLParseTreeNode compoundStatement() throws BPLParserException {
        Token t = this.getNextToken();
        int lineNum = t.getLineNumber();
        this.assertTokenType(t, Token.T_LCURL);

        BPLParseTreeNode localDec = this.localDec();
        BPLParseTreeNode stmtList = this.statementList();
        BPLParseTreeNode node = new BPLParseTreeNode("COMPOUND_STATEMENT",
            2, lineNum);
        node.setChild(0, localDec);
        node.setChild(1, stmtList);

        return node;
    }

    private BPLParseTreeNode expressionStatement() throws BPLParserException {
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
        this.assertTokenType(t, Token.T_SEMI);

        return expStmt;
    }

    private BPLParseTreeNode ifStatement() throws BPLParserException {
        Token t = this.getNextToken();
        this.assertTokenType(t, Token.T_IF);
        int lineNum = t.getLineNumber();

        t = this.getNextToken();
        this.assertTokenType(t, Token.T_LPAREN);

        BPLParseTreeNode exp = this.expression();
        t = this.getNextToken();
        this.assertTokenType(t, Token.T_RPAREN);

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
        this.assertTokenType(t, Token.T_WHILE);
        int lineNum = t.getLineNumber();

        t = this.getNextToken();
        this.assertTokenType(t, Token.T_LPAREN);

        BPLParseTreeNode exp = this.expression();
        t = this.getNextToken();
        this.assertTokenType(t, Token.T_RPAREN);

        BPLParseTreeNode stmt = this.statement();

        BPLParseTreeNode whileStmt = new BPLParseTreeNode("IF_STATEMENT",
                2, lineNum);
        whileStmt.setChild(0, exp);
        whileStmt.setChild(1, stmt);

        return whileStmt;
    }

    private BPLParseTreeNode returnStatement() throws BPLParserException {
        Token t = this.getNextToken();
        this.assertTokenType(t, Token.T_RETURN);
        int lineNum = t.getLineNumber();

        t = this.getNextToken();
        if (t.isType(Token.T_SEMI)){
            BPLParseTreeNode ret = new BPLParseTreeNode("RETURN_STATEMENT",
                0, lineNum);
        }

        this.cacheToken(t);
        BPLParseTreeNode exp = this.expression();

        t = this.getNextToken();
        this.assertTokenType(t, Token.T_SEMI);

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
            this.assertTokenType(t, Token.T_LPAREN);
            t = this.getNextToken();
            this.assertTokenType(t, Token.T_RPAREN);
            t = this.getNextToken();
            this.assertTokenType(t, Token.T_SEMI);
            BPLParseTreeNode writeln = new BPLParseTreeNode("WRITELN_STATEMENT",
                0, lineNum);
            return writeln;
        }

        t = this.getNextToken();
        this.assertTokenType(t, Token.T_LPAREN);

        BPLParseTreeNode exp = this.expression();

        t = this.getNextToken();
        this.assertTokenType(t, Token.T_RPAREN);
        t = this.getNextToken();
        this.assertTokenType(t, Token.T_SEMI);

        BPLParseTreeNode write = new BPLParseTreeNode("WRITE_STATEMENT",
            1, lineNum);
        write.setChild(0, exp);

        return write;
    }

    private BPLParseTreeNode expression() throws BPLParserException {
        ArrayList<Token> tokens = new ArrayList<Token>();
        LinkedList<Token> stack = new LinkedList<Token>();
        Token t = this.getNextToken();

        while (!t.isType(Token.T_SEMI)) {
            if (t.isType(Token.T_EQ) && stack.isEmpty()){
                this.cacheTokens(tokens);
                BPLParseTreeNode var = this.var();
                BPLParseTreeNode subExp = this.expression();
                BPLParseTreeNode exp = new BPLParseTreeNode(
                    "ASSIGNMENT_EXPRESSION", 2, var.getLineNumber());
                exp.setChild(0, var);
                exp.setChild(1, subExp);
                return exp;
            }
            else if (t.isType(Token.T_LBRAC) || t.isType(Token.T_LPAREN)){
                stack.push(t);
            }
            else if (t.isType(Token.T_RBRAC) || t.isType(Token.T_RPAREN)){
                if (stack.isEmpty()){
                    break;
                }
                else {
                    this.assertMatchingBrackets(stack.pop(), t);
                }
            }
            tokens.add(t);
            t = this.getNextToken();
        }

        if (!stack.isEmpty()){
            throw new BPLParserException(t.getLineNumber(),
                "Brackets do not match");
        }

        this.cacheTokens(tokens);
        this.cacheToken(t);

        BPLParseTreeNode comp = this.compExpression();
        BPLParseTreeNode exp = new BPLParseTreeNode("EXPRESSION", 1,
            comp.getLineNumber());
        exp.setChild(0, comp);

        return exp;
    }

    private BPLParseTreeNode var() throws BPLParserException {
        Token t = this.getNextToken();
        int lineNum = t.getLineNumber();
        BPLParseTreeNode var = null;
        IdNode id = null;

        if (t.isType(Token.T_MULT)){

        }
        else {
            id = this.makeID(t);
            t = this.getNextToken();
            if (t.isType(Token.T_LBRAC)){
                t = this.getNextToken();
                BPLParseTreeNode num = this.expression();
                t = this.getNextToken();
                this.assertTokenType(t, Token.T_RBRAC);
                var = new BPLParseTreeNode("ARRAY_VARIABLE",
                    3, lineNum);
                var.setChild(2, num);
            }
            else {
                var = new BPLParseTreeNode("VAR", 1, id.getLineNumber());
            }

        }


        var.setChild(0, id);
        return var;
    }

    private BPLParseTreeNode compExpression() throws BPLParserException {
        // TODO: so much more...
        Token t = this.getNextToken();
        IdNode id = this.makeID(t);
        BPLParseTreeNode var = new BPLParseTreeNode("COMPOUND_EXPRESSION", 1,
            id.getLineNumber());
        var.setChild(0, id);
        return var;
    }

    private IdNode makeID(Token t) throws BPLParserException {
        this.assertTokenType(t, Token.T_ID);
        return new IdNode(t.getValue(), t.getLineNumber());
    }

    /** Methods to get tokens */
    private boolean hasNextToken() throws BPLParserException {
        if (this.cache.size() > 0){
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
        if (this.cache.size() > 0){
            Token t = this.cache.poll();
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
        this.cache.offer(t);
    }

    private void cacheTokens(ArrayList<Token> tokens){
        for (int i = 0; i < tokens.size(); i++){
            this.cache.offer(tokens.get(i));
        }
    }

    private void assertMatchingBrackets(Token left, Token right)
            throws BPLParserException {
        if (left.isType(Token.T_LPAREN)){
            assertTokenType(right, Token.T_RPAREN);
        }
        else {
            assertTokenType(right, Token.T_RBRAC);
        }
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
