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

        BPLParseTreeNode dec = new BPLParseTreeNode("DECLARATION", 1,
                t.getLineNumber());
        if (p.isType(Token.T_LPAREN)) {
            dec.setChild(0, this.funDec());
        }
        else {
            dec.setChild(0, this.varDec());
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

        varDec.setChild(0, specifier);
        varDec.setChild(1, id);
        this.assertTokenType(this.getNextToken(), Token.T_SEMI);

        return varDec;
    }

    private BPLParseTreeNode localDec() throws BPLParserException {
        Token t = this.getNextToken();
        this.cacheToken(t);
        if (!t.isTypeSpecifier()){
            return new BPLParseTreeNode("<empty>", 0, t.getLineNumber());
        }

        BPLParseTreeNode dec = new BPLParseTreeNode("LOCAL_DEC", 2,
            t.getLineNumber());
        dec.setChild(0, this.varDec());
        dec.setChild(1, this.localDec());
        return dec;
    }

    private BPLParseTreeNode params() throws BPLParserException {
        Token t = this.getNextToken();
        BPLParseTreeNode p = null;
        if (t.isType(Token.T_VOID)){
            p = new BPLParseTreeNode("<void>", 0, t.getLineNumber());
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
        BPLParseTreeNode param = this.param();
        Token t = this.getNextToken();
        BPLParseTreeNode list = null;
        if (t.isType(Token.T_RPAREN)){
            this.cacheToken(t);
            list = new BPLParseTreeNode("PARAM_LIST", 1, param.getLineNumber());
        }
        else if (t.isType(Token.T_COMMA)) {
            list = new BPLParseTreeNode("PARAM_LIST", 2, param.getLineNumber());
            list.setChild(1, this.paramList());
        }
        else {
            throw new BPLParserException(t.getLineNumber(),
                "Illegal function params");
        }

        list.setChild(0, param);
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
        assertTokenType(this.getNextToken(), Token.T_LPAREN);
        BPLParseTreeNode params = this.params();

        Token t = this.getNextToken();
        assertTokenType(t, Token.T_RPAREN);

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
            return new BPLParseTreeNode("<empty expression statement>", 0,
                t.getLineNumber());
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

        while (!(t.isType(Token.T_SEMI)
                || (t.isType(Token.T_COMMA)) && stack.isEmpty())) {
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
            t = this.getNextToken();
            id = this.makeID(t);
            var = new BPLParseTreeNode("POINTER_VARIABLE", 1,
                id.getLineNumber());
        }
        else {
            id = this.makeID(t);
            t = this.getNextToken();
            if (t.isType(Token.T_LBRAC)){
                BPLParseTreeNode num = this.expression();
                t = this.getNextToken();
                this.assertTokenType(t, Token.T_RBRAC);
                var = new BPLParseTreeNode("ARRAY_VARIABLE",
                    2, lineNum);
                var.setChild(1, num);
            }
            else {
                this.cacheToken(t);
                var = new BPLParseTreeNode("VARIABLE", 1, id.getLineNumber());
            }
        }

        var.setChild(0, id);

        return var;
    }

    private BPLParseTreeNode compExpression() throws BPLParserException {
        BPLParseTreeNode e1 = this.e();
        Token t = this.getNextToken();

        BPLParseTreeNode compExp = null;
        this.cacheToken(t);
        if (t.isComparator()){
            BPLParseTreeNode rel = this.relop();
            BPLParseTreeNode e2 = this.e();
            compExp = new BPLParseTreeNode("COMP_EXPRESSION",
                3, e1.getLineNumber());
            compExp.setChild(1, rel);
            compExp.setChild(2, e2);
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
        BPLParseTreeNode t1 = this.t();
        BPLParseTreeNode e = null;
        Token t = this.getNextToken();
        this.cacheToken(t);

        if (t.isType(Token.T_PLUS) || t.isType(Token.T_MINUS)){
            BPLParseTreeNode addop = this.addop();
            BPLParseTreeNode e1 = this.e();
            e = new BPLParseTreeNode("E", 3, t1.getLineNumber());
            e.setChild(1, addop);
            e.setChild(2, e1);
        }
        else {
            e = new BPLParseTreeNode("E", 1, t1.getLineNumber());
        }
        e.setChild(0, t1);
        return e;
    }

    private BPLParseTreeNode addop() throws BPLParserException {
        Token t = this.getNextToken();
        if (t.isType(Token.T_PLUS)){
            return new BPLParseTreeNode("+", 0, t.getLineNumber());
        }
        else if (t.isType(Token.T_MINUS)){
            return new BPLParseTreeNode("-", 0, t.getLineNumber());
        }
        throw new BPLParserException(t.getLineNumber(), "Invalid addop");
    }

    private BPLParseTreeNode t() throws BPLParserException {
        BPLParseTreeNode f1 = this.f();
        BPLParseTreeNode t1 = null;
        Token t = this.getNextToken();
        this.cacheToken(t);

        if (t.isType(Token.T_MULT) || t.isType(Token.T_DIV)){
            BPLParseTreeNode mulop = this.mulop();
            BPLParseTreeNode t2 = this.t();
            t1 = new BPLParseTreeNode("T", 3, f1.getLineNumber());
            t1.setChild(1, mulop);
            t1.setChild(2, t2);
        }
        else {
            t1 = new BPLParseTreeNode("T", 1, f1.getLineNumber());
        }
        t1.setChild(0, f1);
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
        else if (t.isType(Token.T_PERCENT)){
            return new BPLParseTreeNode("%", 0, t.getLineNumber());
        }
        throw new BPLParserException(t.getLineNumber(), "Invalid mulop");
    }

    private BPLParseTreeNode f() throws BPLParserException {
        Token t = this.getNextToken();
        if (t.isType(Token.T_MINUS)){
            BPLParseTreeNode f1 = this.f();
            BPLParseTreeNode f = new BPLParseTreeNode("F", 2,
                t.getLineNumber());
            f.setChild(0, new BPLParseTreeNode("-", 0, t.getLineNumber()));
            f.setChild(1, f1);
            return f;
        }
        else if (t.isType(Token.T_AMP)) {
            BPLParseTreeNode factor = this.factor();
            BPLParseTreeNode f = new BPLParseTreeNode("F", 2,
                t.getLineNumber());
            f.setChild(0, new BPLParseTreeNode("&", 0, t.getLineNumber()));
            f.setChild(1, factor);
            return f;
        }
        else if (t.isType(Token.T_MULT)) {
            BPLParseTreeNode factor = this.factor();
            BPLParseTreeNode f = new BPLParseTreeNode("F", 2,
                t.getLineNumber());
            f.setChild(0, new BPLParseTreeNode("*", 0, t.getLineNumber()));
            f.setChild(1, factor);
            return f;
        }

        this.cacheToken(t);
        BPLParseTreeNode factor = this.factor();
        BPLParseTreeNode f = new BPLParseTreeNode("F", 1,
        t.getLineNumber());
        f.setChild(0, factor);
        return f;
    }

    private BPLParseTreeNode factor() throws BPLParserException {
        Token t = this.getNextToken();
        BPLParseTreeNode factor = null;

        if (t.isType(Token.T_LPAREN)){
            BPLParseTreeNode exp = this.expression();
            factor = new BPLParseTreeNode("FACTOR", 1, t.getLineNumber());
            factor.setChild(0, exp);
            t = this.getNextToken();
            assertTokenType(t, Token.T_RPAREN);
        }
        else if (t.isType(Token.T_READ)){
            t = this.getNextToken();
            assertTokenType(t, Token.T_LPAREN);
            t = this.getNextToken();
            assertTokenType(t, Token.T_RPAREN);
            factor = new BPLParseTreeNode("FACTOR", 1, t.getLineNumber());
            factor.setChild(0, new BPLParseTreeNode("read()", 0,
                t.getLineNumber()));
        }
        else if (t.isType(Token.T_ID)){
            Token t1 = this.getNextToken();
            if (t1.isType(Token.T_LPAREN)){
                this.cacheToken(t);
                this.cacheToken(t1);
                BPLParseTreeNode func = this.funCall();
                factor = new BPLParseTreeNode("FACTOR", 1, t.getLineNumber());
                factor.setChild(0, func);
            }
            else if (t1.isType(Token.T_LBRAC)) {
                IdNode id = this.makeID(t);
                BPLParseTreeNode exp = this.expression();
                assertTokenType(this.getNextToken(), Token.T_RBRAC);
                factor = new BPLParseTreeNode("FACTOR", 2, t.getLineNumber());
                factor.setChild(0, id);
                factor.setChild(1, exp);
            }
            else {
                IdNode id = this.makeID(t);
                this.cacheToken(t1);
                factor = new BPLParseTreeNode("FACTOR", 1, t.getLineNumber());
                factor.setChild(0, id);
            }
        }
        else if (t.isType(Token.T_NUM)) {
            factor = new BPLParseTreeNode("FACTOR", 1, t.getLineNumber());
            factor.setChild(0, new IntValueNode(t.getValue(),
                t.getLineNumber()));
        }
        else if (t.isType(Token.T_STRING)) {
            factor = new BPLParseTreeNode("FACTOR", 1, t.getLineNumber());
            factor.setChild(0, new StringValueNode(t.getValue(),
                t.getLineNumber()));
        }
        else{
            throw new BPLParserException(t.getLineNumber(),
                "Unsupported grammar rules for " + t);
        }

        return factor;
    }

    private BPLParseTreeNode funCall() throws BPLParserException {
        Token t = this.getNextToken();
        IdNode id = this.makeID(t);
        t = this.getNextToken();
        assertTokenType(t, Token.T_LPAREN);
        BPLParseTreeNode args = this.args();
        BPLParseTreeNode func = new BPLParseTreeNode("FUNCTION_CALL", 2,
            t.getLineNumber());
        func.setChild(0, id);
        func.setChild(1, args);
        return func;
    }

    private BPLParseTreeNode args() throws BPLParserException {
        Token t = this.getNextToken();
        if (t.isType(Token.T_RPAREN)){
            return new BPLParseTreeNode("<empty>", 0, t.getLineNumber());
        }
        this.cacheToken(t);
        BPLParseTreeNode argList = this.argList();
        BPLParseTreeNode args = new BPLParseTreeNode("ARGS", 1,
            t.getLineNumber());
        args.setChild(0, argList);

        t = this.getNextToken();
        assertTokenType(t, Token.T_RPAREN);

        return args;
    }

    private BPLParseTreeNode argList() throws BPLParserException {
        BPLParseTreeNode exp = this.expression();
        Token t = this.getNextToken();
        BPLParseTreeNode list = null;
        if (t.isType(Token.T_RPAREN)){
            this.cacheToken(t);
            list = new BPLParseTreeNode("ARG_LIST", 1, exp.getLineNumber());
        }
        else if (t.isType(Token.T_COMMA)) {
            BPLParseTreeNode rest = this.argList();
            list = new BPLParseTreeNode("ARG_LIST", 2, exp.getLineNumber());
            list.setChild(1, rest);
        }
        else {
            throw new BPLParserException(t.getLineNumber(),
                "Illegal function params");
        }

        list.setChild(0, exp);
        return list;
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
