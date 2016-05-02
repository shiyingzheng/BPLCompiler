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
            BPLParseTreeNode child = tree.getChild(i);
            s += this.toStringHelper(child, depth + 1);
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
            list.setChild(1, this.declarationList());
        }
        else {
            list = new BPLParseTreeNode("DECLARATION_LIST",
                1, declaration.getLineNumber());
        }
        list.setChild(0, declaration);
        return list;
    }

    private BPLParseTreeNode declaration() throws BPLParserException {
        Token t = this.getNextToken();
        Token s = this.getNextToken();
        Token p = this.getNextToken();
        this.cacheToken(t);
        this.cacheToken(s);
        this.cacheToken(p);

        if (p.isType(Token.T_LPAREN)) {
            return this.funDec();
        }
        return this.varDec();
    }

    private BPLParseTreeNode varDec() throws BPLParserException {
        BPLParseTreeNode specifier = this.typeSpecifier();
        Token t = this.getNextToken();
        int lineNum = t.getLineNumber();

        BPLParseTreeNode varDec = null;
        IdNode id = null;

        if (t.isType(Token.T_MULT)){
            id = this.makeID(this.getNextToken());
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
                this.assertTokenType(this.getNextToken(), Token.T_RBRAC);

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
        this.assertTokenType(this.getNextToken(), Token.T_SEMI);
        varDec.setChild(0, specifier);
        varDec.setChild(1, id);
        return varDec;
    }

    private BPLParseTreeNode localDec() throws BPLParserException {
        Token t = this.peakNextToken();
        if (!t.isTypeSpecifier()){
            return this.makeEmpty(t);
        }

        BPLParseTreeNode dec = new BPLParseTreeNode("LOCAL_DEC", 2,
            t.getLineNumber());
        dec.setChild(0, this.varDec());
        dec.setChild(1, this.localDec());
        return dec;
    }

    private BPLParseTreeNode params() throws BPLParserException {
        Token t = this.getNextToken();
        if (t.isType(Token.T_VOID)){
            return this.makeEmpty(t);
        }
        this.cacheToken(t);
        return this.paramList();
    }

    private BPLParseTreeNode paramList() throws BPLParserException {
        BPLParseTreeNode param = this.param();
        Token t = this.getNextToken();
        BPLParseTreeNode list = new BPLParseTreeNode("PARAM_LIST",
            2, param.getLineNumber());
        list.setChild(0, param);
        if (t.isType(Token.T_RPAREN)){
            this.cacheToken(t);
            list.setChild(1, this.makeEmpty(t));
        }
        else if (t.isType(Token.T_COMMA)) {
            list.setChild(1, this.paramList());
        }
        else {
            throw new BPLParserException(t.getLineNumber(),
                "Illegal function params");
        }
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
                param = new BPLParseTreeNode("ARRAY_VARIABLE_PARAM",
                    2, lineNum);
            }
            else {
                this.cacheToken(t);
                param = new BPLParseTreeNode("VARIABLE_PARAM",
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
        return new BPLParseTreeNode(nodeType, 0, t.getLineNumber());
    }

    private BPLParseTreeNode funDec() throws BPLParserException {
        Token t = this.getNextToken();
        BPLParseTreeNode specifier = null;
        if (t.isType(Token.T_VOID)) {
            specifier = new BPLParseTreeNode("VOID_SPECIFIER", 0,
                t.getLineNumber());
        }
        else{
            this.cacheToken(t);
            specifier = this.typeSpecifier();
        }
        IdNode id = this.makeID(this.getNextToken());
        assertTokenType(this.getNextToken(), Token.T_LPAREN);
        BPLParseTreeNode params = this.params();
        assertTokenType(this.getNextToken(), Token.T_RPAREN);
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
        Token t = this.peakNextToken();
        if (t.isType(Token.T_RCURL)) {
            return this.makeEmpty(t);
        }

        BPLParseTreeNode curlist = new BPLParseTreeNode("STATEMENT_LIST",
            2, t.getLineNumber());
        curlist.setChild(0, this.statement());
        curlist.setChild(1, this.statementList());
        return curlist;
    }

    private BPLParseTreeNode statement() throws BPLParserException {
        Token t = peakNextToken();
        if (t.isType(Token.T_LCURL)) {
            return this.compoundStatement();
        }
        if (t.isType(Token.T_IF)){
            return this.ifStatement();
        }
        if (t.isType(Token.T_WHILE)){
            return this.whileStatement();
        }
        if (t.isType(Token.T_RETURN)){
            return this.returnStatement();
        }
        if (t.isType(Token.T_WRITE) || t.isType(Token.T_WRITELN)){
            return this.writeStatement();
        }
        return this.expressionStatement();
    }

    private BPLParseTreeNode compoundStatement() throws BPLParserException {
        Token t = this.getNextToken();
        this.assertTokenType(t, Token.T_LCURL);
        BPLParseTreeNode localDec = this.localDec();
        BPLParseTreeNode stmtList = this.statementList();
        this.assertTokenType(this.getNextToken(), Token.T_RCURL);

        BPLParseTreeNode comp = new BPLParseTreeNode("COMPOUND_STATEMENT",
            2, t.getLineNumber());
        comp.setChild(0, localDec);
        comp.setChild(1, stmtList);
        return comp;
    }

    private BPLParseTreeNode expressionStatement() throws BPLParserException {
        Token t = this.getNextToken();
        if (t.isType(Token.T_SEMI)){
            return this.makeEmpty(t);
        }

        this.cacheToken(t);
        BPLParseTreeNode expStmt = new BPLParseTreeNode("EXPRESSION_STMT",
            1, t.getLineNumber());
        expStmt.setChild(0, this.expression());
        this.assertTokenType(this.getNextToken(), Token.T_SEMI);
        return expStmt;
    }

    private BPLParseTreeNode ifStatement() throws BPLParserException {
        Token t = this.getNextToken();
        this.assertTokenType(t, Token.T_IF);
        int lineNum = t.getLineNumber();

        this.assertTokenType(this.getNextToken(), Token.T_LPAREN);
        BPLParseTreeNode exp = this.expression();
        this.assertTokenType(this.getNextToken(), Token.T_RPAREN);

        BPLParseTreeNode stmt = this.statement();

        BPLParseTreeNode ifStmt = null;
        t = this.getNextToken();
        if (t.isType(Token.T_ELSE)){
            BPLParseTreeNode elseStmt = this.statement();
            ifStmt = new BPLParseTreeNode("IF_STATEMENT", 3, lineNum);
            ifStmt.setChild(2, elseStmt);
        }
        else {
            this.cacheToken(t);
            ifStmt = new BPLParseTreeNode("IF_STATEMENT", 2, lineNum);
        }
        ifStmt.setChild(0, exp);
        ifStmt.setChild(1, stmt);
        return ifStmt;
    }

    private BPLParseTreeNode whileStatement() throws BPLParserException {
        Token t = this.getNextToken();
        this.assertTokenType(t, Token.T_WHILE);
        int lineNum = t.getLineNumber();

        this.assertTokenType(this.getNextToken(), Token.T_LPAREN);
        BPLParseTreeNode exp = this.expression();
        this.assertTokenType(this.getNextToken(), Token.T_RPAREN);
        BPLParseTreeNode stmt = this.statement();

        BPLParseTreeNode whileStmt = new BPLParseTreeNode("WHILE_STATEMENT",
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
            return new BPLParseTreeNode("RETURN_STATEMENT", 0, lineNum);
        }

        this.cacheToken(t);
        BPLParseTreeNode exp = this.expression();
        this.assertTokenType(this.getNextToken(), Token.T_SEMI);

        BPLParseTreeNode ret = new BPLParseTreeNode("RETURN_STATEMENT",
            1, lineNum);
        ret.setChild(0, exp);
        return ret;
    }

    private BPLParseTreeNode writeStatement() throws BPLParserException {
        Token t = this.getNextToken();
        int lineNum = t.getLineNumber();
        if (t.isType(Token.T_WRITELN)){
            this.assertTokenType(this.getNextToken(), Token.T_LPAREN);
            this.assertTokenType(this.getNextToken(), Token.T_RPAREN);
            this.assertTokenType(this.getNextToken(), Token.T_SEMI);
            return new BPLParseTreeNode("WRITELN_STATEMENT", 0, lineNum);
        }

        this.assertTokenType(this.getNextToken(), Token.T_LPAREN);
        BPLParseTreeNode exp = this.expression();
        this.assertTokenType(this.getNextToken(), Token.T_RPAREN);
        this.assertTokenType(this.getNextToken(), Token.T_SEMI);

        BPLParseTreeNode write = new BPLParseTreeNode("WRITE_STATEMENT",
            1, lineNum);
        write.setChild(0, exp);
        return write;
    }

    private BPLParseTreeNode expression() throws BPLParserException {
        ArrayList<Token> tokens = new ArrayList<Token>();
        LinkedList<Token> stack = new LinkedList<Token>();
        Token t = this.getNextToken();

        while (!(t.isType(Token.T_SEMI)
                || ((t.isType(Token.T_COMMA)
                    || t.isType(Token.T_RBRAC)
                    || t.isType(Token.T_RPAREN)) && stack.isEmpty()))) {
            if (t.isType(Token.T_EQ) && stack.isEmpty()){
                this.cacheTokens(tokens);
                BPLParseTreeNode var = this.var();
                BPLParseTreeNode exp = new BPLParseTreeNode(
                    "ASSIGNMENT_EXPRESSION", 2, var.getLineNumber());
                exp.setChild(0, var);
                exp.setChild(1, this.expression());
                return exp;
            }
            else if (t.isType(Token.T_LBRAC) || t.isType(Token.T_LPAREN)){
                stack.push(t);
            }
            else if (t.isType(Token.T_RBRAC) || t.isType(Token.T_RPAREN)){
                this.assertMatchingBrackets(stack.pop(), t);
            }
            tokens.add(t);
            t = this.getNextToken();
        }

        if (!stack.isEmpty()){
            throw new BPLParserException(t.getLineNumber(),
                "Brackets not matching");
        }

        this.cacheTokens(tokens);
        this.cacheToken(t);
        return this.compExpression();
    }

    private BPLParseTreeNode var() throws BPLParserException {
        Token t = this.getNextToken();
        int lineNum = t.getLineNumber();
        BPLParseTreeNode var = null;
        IdNode id = null;

        if (t.isType(Token.T_MULT)){
            id = this.makeID(this.getNextToken());
            var = new BPLParseTreeNode("POINTER_VARIABLE", 1, lineNum);
        }
        else {
            id = this.makeID(t);
            t = this.getNextToken();
            if (t.isType(Token.T_LBRAC)){
                BPLParseTreeNode num = this.expression();
                this.assertTokenType(this.getNextToken(), Token.T_RBRAC);
                var = new BPLParseTreeNode("ARRAY_ELMT_VARIABLE", 2, lineNum);
                var.setChild(1, num);
            }
            else {
                this.cacheToken(t);
                var = new BPLParseTreeNode("VARIABLE", 1, lineNum);
            }
        }
        var.setChild(0, id);
        return var;
    }

    private BPLParseTreeNode compExpression() throws BPLParserException {
        BPLParseTreeNode e1 = this.e();
        Token t = peakNextToken();

        BPLParseTreeNode compExp = null;
        if (t.isComparator()){
            compExp = new BPLParseTreeNode("COMP_EXPRESSION",
                3, e1.getLineNumber());
            compExp.setChild(1, this.relop());
            compExp.setChild(2, this.e());
        }
        else {
            compExp = new BPLParseTreeNode("COMP_EXPRESSION",
                1, e1.getLineNumber());
        }
        compExp.setChild(0, e1);
        return compExp;
    }

    private BPLParseTreeNode relop() throws BPLParserException {
        Token t = this.getNextToken();
        BPLParseTreeNode rel = null;
        if (t.isType(Token.T_LESSEQ)){
            rel = new BPLParseTreeNode("<=", 0, t.getLineNumber());
        }
        else if (t.isType(Token.T_LESS)){
            rel = new BPLParseTreeNode("<", 0, t.getLineNumber());
        }
        else if (t.isType(Token.T_EQEQ)){
            rel = new BPLParseTreeNode("==", 0, t.getLineNumber());
        }
        else if (t.isType(Token.T_NE)){
            rel = new BPLParseTreeNode("!=", 0, t.getLineNumber());
        }
        else if (t.isType(Token.T_GREQ)){
            rel = new BPLParseTreeNode(">=", 0, t.getLineNumber());
        }
        else if (t.isType(Token.T_GR)){
            rel = new BPLParseTreeNode(">", 0, t.getLineNumber());
        }
        return rel;
    }

    private BPLParseTreeNode e() throws BPLParserException {
        BPLParseTreeNode e = this.t();
        Token t = this.peakNextToken();
        while (t.isAddOp()){
            BPLParseTreeNode addop = this.addop();
            BPLParseTreeNode t2 = this.t();
            BPLParseTreeNode e1 = new BPLParseTreeNode("E", 3,
                e.getLineNumber());
            e1.setChild(0, e);
            e1.setChild(1, addop);
            e1.setChild(2, t2);
            e = e1;
            t = this.peakNextToken();
        }
        return e;
    }

    private BPLParseTreeNode addop() throws BPLParserException {
        Token t = this.getNextToken();
        if (t.isType(Token.T_PLUS)){
            return new BPLParseTreeNode("+", 0, t.getLineNumber());
        }
        return new BPLParseTreeNode("-", 0, t.getLineNumber());
    }

    private BPLParseTreeNode t() throws BPLParserException {
        BPLParseTreeNode t1 = this.f();
        Token t = this.peakNextToken();
        while (t.isMulOp()){
            BPLParseTreeNode mulop = this.mulop();
            BPLParseTreeNode f2 = this.f();
            BPLParseTreeNode t2 = new BPLParseTreeNode("T", 3,
                t1.getLineNumber());
            t2.setChild(0, t1);
            t2.setChild(1, mulop);
            t2.setChild(2, f2);
            t1 = t2;
            t = this.peakNextToken();
        }
        return t1;
    }

    private BPLParseTreeNode mulop() throws BPLParserException {
        Token t = this.getNextToken();
        if (t.isType(Token.T_MULT)){
            return new BPLParseTreeNode("*", 0, t.getLineNumber());
        }
        else if (t.isType(Token.T_DIV)){
            return new BPLParseTreeNode("/", 0, t.getLineNumber());
        }
        return new BPLParseTreeNode("%", 0, t.getLineNumber());
    }

    private BPLParseTreeNode f() throws BPLParserException {
        Token t = this.getNextToken();
        int lineNum = t.getLineNumber();

        if (t.isType(Token.T_MINUS)){
            BPLParseTreeNode f = new BPLParseTreeNode("F", 2, lineNum);
            f.setChild(0, new BPLParseTreeNode("-", 0, lineNum));
            f.setChild(1, this.f());
            return f;
        }
        else if (t.isType(Token.T_AMP)) {
            BPLParseTreeNode f = new BPLParseTreeNode("REF_F", 1, lineNum);
            f.setChild(0, this.factor());
            return f;
        }
        else if (t.isType(Token.T_MULT)) {
            BPLParseTreeNode f = new BPLParseTreeNode("DEREF_F", 1, lineNum);
            f.setChild(0, this.factor());
            return f;
        }
        this.cacheToken(t);
        BPLParseTreeNode f = new BPLParseTreeNode("F", 1, lineNum);
        f.setChild(0, this.factor());
        return f;
    }

    private BPLParseTreeNode factor() throws BPLParserException {
        Token t = this.getNextToken();
        int lineNum = t.getLineNumber();
        BPLParseTreeNode factor = null;

        if (t.isType(Token.T_LPAREN)){
            factor = new BPLParseTreeNode("FACTOR", 1, lineNum);
            factor.setChild(0, this.expression());
            assertTokenType(this.getNextToken(), Token.T_RPAREN);
        }
        else if (t.isType(Token.T_READ)){
            assertTokenType(this.getNextToken(), Token.T_LPAREN);
            assertTokenType(this.getNextToken(), Token.T_RPAREN);
            factor = new BPLParseTreeNode("FACTOR", 1, lineNum);
            factor.setChild(0, new BPLParseTreeNode("read()", 0, lineNum));
        }
        else if (t.isType(Token.T_ID)){
            Token t1 = this.getNextToken();
            if (t1.isType(Token.T_LPAREN)){
                this.cacheToken(t);
                this.cacheToken(t1);
                factor = new BPLParseTreeNode("FACTOR", 1, lineNum);
                factor.setChild(0, this.funCall());
            }
            else if (t1.isType(Token.T_LBRAC)) {
                IdNode id = this.makeID(t);
                BPLParseTreeNode exp = this.expression();
                assertTokenType(this.getNextToken(), Token.T_RBRAC);
                factor = new BPLParseTreeNode("FACTOR", 2, lineNum);
                factor.setChild(0, id);
                factor.setChild(1, exp);
            }
            else {
                this.cacheToken(t1);
                factor = new BPLParseTreeNode("FACTOR", 1, lineNum);
                factor.setChild(0, this.makeID(t));
            }
        }
        else if (t.isType(Token.T_NUM)) {
            factor = new BPLParseTreeNode("FACTOR", 1, lineNum);
            factor.setChild(0, new IntValueNode(t.getValue(), lineNum));
        }
        else if (t.isType(Token.T_STRING)) {
            factor = new BPLParseTreeNode("FACTOR", 1, lineNum);
            factor.setChild(0, new StringValueNode(t.getValue(), lineNum));
        }
        else{
            throw new BPLParserException(lineNum,
                "Unsupported grammar rules for " + t.getValue());
        }
        return factor;
    }

    private BPLParseTreeNode funCall() throws BPLParserException {
        IdNode id = this.makeID(this.getNextToken());
        assertTokenType(this.getNextToken(), Token.T_LPAREN);
        BPLParseTreeNode args = this.args();
        BPLParseTreeNode func = new BPLParseTreeNode("FUNCTION_CALL", 2,
            id.getLineNumber());
        func.setChild(0, id);
        func.setChild(1, args);
        return func;
    }

    private BPLParseTreeNode args() throws BPLParserException {
        Token t = this.getNextToken();
        if (t.isType(Token.T_RPAREN)){
            return this.makeEmpty(t);
        }
        this.cacheToken(t);
        BPLParseTreeNode argsList = this.argList();
        assertTokenType(this.getNextToken(), Token.T_RPAREN);
        return argsList;
    }

    private BPLParseTreeNode argList() throws BPLParserException {
        BPLParseTreeNode exp = this.expression();
        Token t = this.getNextToken();

        BPLParseTreeNode list = new BPLParseTreeNode("ARG_LIST",
            2, exp.getLineNumber());
        list.setChild(0, exp);
        if (t.isType(Token.T_RPAREN)){
            this.cacheToken(t);
            list.setChild(1, this.makeEmpty(t));
        }
        else if (t.isType(Token.T_COMMA)) {
            list.setChild(1, this.argList());
        }
        else {
            throw new BPLParserException(t.getLineNumber(),
                "Illegal function params");
        }
        return list;
    }

    private IdNode makeID(Token t) throws BPLParserException {
        this.assertTokenType(t, Token.T_ID);
        return new IdNode(t.getValue(), t.getLineNumber());
    }

    private BPLParseTreeNode makeEmpty(Token t) throws BPLParserException {
        return new BPLParseTreeNode("<empty>", 0, t.getLineNumber());
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
            return this.cache.poll();
        }
        try {
            return this.scanner.getNextToken();
        }
        catch(Exception e) {
            String lineNum = e.getMessage().split(":")[0];
            throw new BPLParserException(lineNum + ": Unexpected EOF");
        }
    }

    private void cacheToken(Token t) throws BPLParserException {
        int i = 0;
        for (i = 0; i < this.cache.size(); i++){
            int pos1 = this.cache.get(i).getPosition();
            int pos2 = t.getPosition();
            if (pos1 > pos2){
                break;
            }
            else if (pos1 == pos2){
                return;
            }
        }
        this.cache.add(i, t);
    }

    private Token peakNextToken() throws BPLParserException {
        Token t = this.getNextToken();
        this.cacheToken(t);
        return t;
    }

    private void cacheTokens(ArrayList<Token> tokens)
            throws BPLParserException{
        for (int i = 0; i < tokens.size(); i++){
            this.cacheToken(tokens.get(i));
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
            return "INT";
        }
        if (t.isType(Token.T_KWSTRING)){
            return "STRING";
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
        System.out.print(parser);
    }
}
