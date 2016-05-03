import java.util.LinkedList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.HashSet;


public class BPLTypeChecker {
    private boolean DEBUG;
    private boolean DETAILDEBUG;

    private BPLParser parser;
    private BPLParseTreeNode tree;
    private HashMap<String, BPLParseTreeNode> globalSymbolTable;
    private LinkedList<BPLParseTreeNode> localDeclarations;

    public BPLTypeChecker(String fileName, boolean debug, boolean detailDebug)
            throws BPLTypeCheckerException {
        try {
            parser = new BPLParser(fileName);
            this.DEBUG = debug;
            this.DETAILDEBUG = detailDebug;
            this.tree = parser.getParseTree();
            this.globalSymbolTable = new HashMap<String, BPLParseTreeNode>();
            this.localDeclarations = new LinkedList<BPLParseTreeNode>();
            this.typeCheck(this.tree);
        }
        catch (BPLParserException e) {
            String lineNum = e.getMessage().split(":")[0];
            throw new BPLTypeCheckerException(lineNum + ": Parsing error");
        }

        if (DETAILDEBUG) {
            System.out.println("\nGlobal symbol table:");
            System.out.println(this.globalSymbolTable);
            System.out.println("Local decs list (should be empty):");
            System.out.println(this.localDeclarations);
        }
    }

    public BPLParseTreeNode getTree() {
        return this.tree;
    }

    private void typeCheck(BPLParseTreeNode tree) throws BPLTypeCheckerException{
        if (tree.isNodeType("PROGRAM")){
            this.typeCheckDecList(tree.getChild(0));
        }
    }

    private void typeCheckDecList(BPLParseTreeNode tree)
            throws BPLTypeCheckerException{
        if (tree.isNodeType("DECLARATION_LIST")) {
            for (int i = 0; i < tree.numChildren(); i++) {
                this.typeCheckDecList(tree.getChild(i));
            }
        }
        else {
            this.typeCheckDec(tree);
        }
    }

    private void typeCheckDec(BPLParseTreeNode tree)
            throws BPLTypeCheckerException{
        String name = tree.getName();
        if (this.globalSymbolTable.containsKey(name)) {
            throw new BPLTypeCheckerException(tree.getLineNumber(),
                "Cannot redefine global variable");
        }
        this.globalSymbolTable.put(tree.getName(), tree);
        if (DETAILDEBUG){
            System.out.print(tree);
            System.out.println(" " + name);
        }
        if (tree.isNodeType("FUNCTION")) {
            this.typeCheckFunction(tree);
        }
    }

    private void typeCheckFunction(BPLParseTreeNode tree)
            throws BPLTypeCheckerException{
        int numParams = this.typeParams(tree.getChild(2));
        this.typeCheckCompound(tree.getChild(3));
        while (numParams > 0) {
            numParams--;
            this.localDeclarations.removeFirst();
        }
        this.checkReturnType(tree, tree.getChild(0).getNodeType());
    }

    private int typeParams(BPLParseTreeNode tree) {
        if (tree.isEmpty()){
            return 0;
        }
        int numParams = 1;
        this.localDeclarations.addFirst(tree.getChild(0));
        if (DETAILDEBUG) {
            System.out.print(tree.getChild(0));
            System.out.println(" " + tree.getChild(0).getName());
        }
        numParams += this.typeParams(tree.getChild(1));
        return numParams;
    }

    private void typeCheckCompound(BPLParseTreeNode tree)
            throws BPLTypeCheckerException {
        int numLocalDecs = this.typeLocalDecs(tree.getChild(0));
        this.typeCheckStatementList(tree.getChild(1));
        while (numLocalDecs > 0) {
            numLocalDecs--;
            this.localDeclarations.removeFirst();
        }
    }

    private void checkReturnType(BPLParseTreeNode tree, String type)
            throws BPLTypeCheckerException{
        if (type.equals("INT_SPECIFIER")) {
            type = "INT";
        }
        else if (type.equals("STRING_SPECIFIER")) {
            type = "STRING";
        }
        if (tree.isNodeType("RETURN_STATEMENT")) {
            if (DEBUG) {
                System.out.println("Checking return type "
                    + tree);
            }
            if (type.equals("VOID_SPECIFIER")){
                if (tree.numChildren() > 0) {
                    throw new BPLTypeCheckerException(tree.getLineNumber(),
                        "Function should not return any value");
                }
            }
            if (tree.numChildren() > 0) {
                BPLParseTreeNode child = tree.getChild(0);
                this.assertType(this.checkExpression(child),
                    type, child.getLineNumber());
            }
        }
        for (int i = 0; i < tree.numChildren(); i++){
            this.checkReturnType(tree.getChild(i), type);
        }
    }

    private int typeLocalDecs(BPLParseTreeNode tree) {
        if (tree.isEmpty()){
            return 0;
        }
        HashSet<String> newDecs = new HashSet<String>();
        int numLocalDecs = 1;
        this.localDeclarations.addFirst(tree.getChild(0));
        if (DETAILDEBUG) {
            BPLParseTreeNode child = tree.getChild(0);
            System.out.println(child + " " + child.getName());
        }
        if (tree.numChildren() > 1) {
            numLocalDecs += this.typeLocalDecs(tree.getChild(1));
        }
        return numLocalDecs;
    }

    private void typeCheckStatementList(BPLParseTreeNode tree)
            throws BPLTypeCheckerException{
        if (tree.isEmpty()) {
            return;
        }
        this.typeCheckStatement(tree.getChild(0));
        this.typeCheckStatementList(tree.getChild(1));
    }

    private void typeCheckStatement(BPLParseTreeNode tree)
            throws BPLTypeCheckerException{
        if (tree.isNodeType("EXPRESSION_STMT")) {
            this.typeCheckExpressionStmt(tree);
        }
        else if (tree.isNodeType("COMPOUND_STATEMENT")) {
            this.typeCheckCompound(tree);
        }
        else if (tree.isNodeType("IF_STATEMENT")){
            this.typeCheckIf(tree);
        }
        else if (tree.isNodeType("WHILE_STATEMENT")){
            this.typeCheckWhile(tree);
        }
        else if (tree.isNodeType("RETURN_STATEMENT")) {
            this.typeCheckReturn(tree);
        }
        else if (tree.isNodeType("WRITE_STATEMENT")) {
            this.typeCheckWrite(tree);
        }
        else if (tree.isNodeType("WRITELN_STATEMENT")) {
            if (DETAILDEBUG) {
                System.out.println(tree);
            }
        }
    }

    private void typeCheckExpressionStmt(BPLParseTreeNode tree)
            throws BPLTypeCheckerException{
        if (tree.isEmpty()) {
            return;
        }
        this.typeCheckExpression(tree.getChild(0));
    }

    private void typeCheckExpression(BPLParseTreeNode tree)
            throws BPLTypeCheckerException{
        if (DETAILDEBUG) {
            System.out.println(tree);
        }
        if (tree.isNodeType("ASSIGNMENT_EXPRESSION")){
            this.typeVar(tree.getChild(0));
            this.typeCheckExpression(tree.getChild(1));
        }
        else {
            this.typeCompExp(tree);
        }
        this.checkExpression(tree);
    }

    private String checkExpression(BPLParseTreeNode tree)
            throws BPLTypeCheckerException{
        String expType = "";
        if (tree.isNodeType("ASSIGNMENT_EXPRESSION")) {
            expType = this.checkAssignmentExpression(tree);
        }
        else {
            expType = this.checkCompExp(tree);
        }
        tree.setExpType(expType);
        return expType;
    }

    private String checkAssignmentExpression(BPLParseTreeNode tree)
            throws BPLTypeCheckerException{
        BPLParseTreeNode var = tree.getChild(0);
        String t1 = this.checkVar(var);
        String t2 = this.checkExpression(tree.getChild(1));
        this.assertType(t1, t2, tree.getLineNumber());
        if (var.isNodeType("VARIABLE")
                && var.getDeclaration().getNodeType().contains("POINTER")) {
            this.checkPtrAddrAssign(var, tree.getChild(1));
        }
        return t1;
    }

    private void checkPtrAddrAssign(BPLParseTreeNode ptr, BPLParseTreeNode e)
            throws BPLTypeCheckerException {
        boolean ref = false;
        while(!e.isNodeType("<id>")) {
            if (e.isNodeType("REF_F")) {
                ref = true;
            }
            if (e.numChildren() > 1) {
                ref = false;
            }
            if (e.isNodeType("<string>") || e.isNodeType("<num>")) {
                throw new BPLTypeCheckerException(ptr.getLineNumber(),
                    "right hand side of pointer address assignment not valid");
            }
            e = e.getChild(0);
        }
        if (ref == false) {
            if (!e.getDeclaration().getNodeType().contains("POINTER")){
                throw new BPLTypeCheckerException(ptr.getLineNumber(),
                    "right hand side of pointer address assignment not valid");
            }
        }
        String t1 = ptr.getDeclaration().getChild(0).getNodeType();
        String t2 = e.getDeclaration().getChild(0).getNodeType();
        if (DEBUG) {
            System.out.println("Line " + ptr.getLineNumber() +
                ": matching pointer for " + ptr.getName() + " and "
                + e.getName());
        }
        if (!t1.equals(t2)){
            throw new BPLTypeCheckerException(ptr.getLineNumber(),
                "Invalid pointer address assignment");
        }
    }

    private void typeCompExp(BPLParseTreeNode tree)
            throws BPLTypeCheckerException {
        this.typeETF(tree.getChild(0));
        if (tree.numChildren() > 1) {
            this.typeETF(tree.getChild(2));
        }
    }

    private String checkCompExp(BPLParseTreeNode tree)
            throws BPLTypeCheckerException{
        String treeType = "";
        String t1 = this.checkETF(tree.getChild(0));
        if (tree.numChildren() > 1) {
            String t2 = this.checkETF(tree.getChild(2));
            assertType(t1, "INT", tree.getLineNumber());
            assertType(t2, "INT", tree.getLineNumber());
        }
        return t1;
    }

    private void typeETF(BPLParseTreeNode tree) throws BPLTypeCheckerException {
        if (DETAILDEBUG) {
            System.out.println(tree + " typing");
        }
        if (tree.isNodeType("F") || tree.isNodeType("REF_F")
                || tree.isNodeType("DEREF_F")){
            this.typeF(tree);
            return;
        }
        this.typeETF(tree.getChild(0));
        if (tree.numChildren() > 1) {
            this.typeETF(tree.getChild(2));
        }
    }

    private String checkETF(BPLParseTreeNode tree)
            throws BPLTypeCheckerException{
        if (DETAILDEBUG) {
            System.out.println(tree + " checking");
        }
        String treeType = "";
        if (tree.isNodeType("F") || tree.isNodeType("REF_F")
                || tree.isNodeType("DEREF_F")){
            treeType = this.checkF(tree);
            return treeType;
        }
        String t1 = this.checkETF(tree.getChild(0));
        if (tree.numChildren() > 1) {
            String t2 = this.checkETF(tree.getChild(2));
            assertType(t1, t2, tree.getLineNumber());
        }
        return t1;
    }

    private void typeF(BPLParseTreeNode tree) throws BPLTypeCheckerException {
        if (!tree.getChild(0).isNodeType("-")) {
            this.typeFactor(tree.getChild(0));
        }
        if (tree.numChildren() > 1) {
            this.typeF(tree.getChild(1));
        }
    }

    private String checkF(BPLParseTreeNode tree) throws BPLTypeCheckerException{
        String treeType = "";
        if (tree.numChildren() > 1) {
            treeType = this.checkF(tree.getChild(1));
        }
        else {
            if (tree.isNodeType("REF_F")) {
                treeType = "PTR " + this.checkFactor(tree.getChild(0));
            }
            else if (tree.isNodeType("DEREF_F")) {
                this.checkFactor(tree.getChild(0));
                BPLParseTreeNode f = tree;
                while(!f.isNodeType("<id>")) {
                    f = f.getChild(0);
                }
                treeType = f.getDeclaration().getChild(0).getNodeType();
                if (!treeType.contains("INT") && !treeType.contains("STRING")) {
                    throw new BPLTypeCheckerException(tree.getLineNumber(),
                        "Invalid de-reference");
                }
            }
            else {
                treeType = this.checkFactor(tree.getChild(0));
            }
        }
        return treeType;
    }

    private void typeFactor(BPLParseTreeNode tree)
            throws BPLTypeCheckerException {
        if (DETAILDEBUG) {
            System.out.println(tree);
        }
        if (tree.numChildren() > 1) {
            this.linkDeclaration(tree.getChild(0));
            this.typeCheckExpression(tree.getChild(1));
            return;
        }
        BPLParseTreeNode child = tree.getChild(0);
        if (child.isNodeType("COMP_EXPRESSION")
            || child.isNodeType("ASSIGNMENT_EXPRESSION")) {
            this.typeCheckExpression(child);
        }
        else if (child.isNodeType("FUNCTION_CALL")) {
            this.typeFunctionCall(child);
        }
        else if (child.isNodeType("<id>")) {
            this.linkDeclaration(child);
        }
    }

    private String checkFactor(BPLParseTreeNode tree)
            throws BPLTypeCheckerException{
        String treeType = "";
        BPLParseTreeNode child = tree.getChild(0);
        if (child.isNodeType("read()")) {
            treeType = "INT";
        }
        else {
            if (child.isNodeType("<id>")) {
                if (child.getDeclaration().getNodeType().contains("POINTER")) {
                    treeType = "PTR ";
                }
                if (child.getDeclaration().getNodeType().contains("ARRAY")
                        && tree.numChildren() <= 1) {
                    treeType = "ARR ";
                }
                treeType += child.getDeclaration().getChild(0).getNodeType();
                if (tree.numChildren() > 1){
                    String t = this.checkExpression(tree.getChild(1));
                    assertType("INT", t, tree.getLineNumber());
                }
            }
            else if (child.isNodeType("FUNCTION_CALL")) {
                treeType = this.checkFunctionCall(child);
            }
            else if (child.isNodeType("<num>")) {
                treeType = "INT";
            }
            else if (child.isNodeType("<string>")) {
                treeType = "STRING";
            }
            else {
                treeType = this.checkExpression(child);
            }
        }
        return treeType;
    }

    private void typeFunctionCall(BPLParseTreeNode tree)
            throws BPLTypeCheckerException {
        if (DETAILDEBUG) {
            System.out.println(tree);
        }
        this.linkDeclaration(tree.getChild(0));
        this.typeArgs(tree.getChild(1));
    }

    private String checkFunctionCall(BPLParseTreeNode tree)
            throws BPLTypeCheckerException{
        String treeType = "";
        assertType(tree.getChild(0).getDeclaration().getNodeType(), "FUNCTION",
            tree.getLineNumber());
        treeType = tree.getChild(0).getDeclaration().getChild(0).getNodeType();
        BPLParseTreeNode params = tree.getChild(0).getDeclaration().getChild(2);
        this.checkArgList(tree.getChild(1), params);
        return treeType;
    }

    private void typeArgs(BPLParseTreeNode tree)
            throws BPLTypeCheckerException {
        if (DETAILDEBUG) {
            System.out.println(tree);
        }
        if (!tree.isEmpty()) {
            this.typeArgList(tree);
        }
    }

    private void typeArgList(BPLParseTreeNode tree)
            throws BPLTypeCheckerException {
        if (tree.isEmpty()) {
            return;
        }
        this.typeCheckExpression(tree.getChild(0));
        this.typeArgList(tree.getChild(1));
    }

    private void checkArgList(BPLParseTreeNode args, BPLParseTreeNode params)
            throws BPLTypeCheckerException{
        if (args.isEmpty() != params.isEmpty()) {
            throw new BPLTypeCheckerException(args.getLineNumber(),
                "Mismatching arguments in function call");
        }
        if (args.isEmpty()){
            return;
        }

        String t1 = this.checkExpression(args.getChild(0));
        String t2 = params.getChild(0).getChild(0).getNodeType();

        if (params.getChild(0).isNodeType("POINTER_PARAM")){
            t2 = "PTR " + t2;
        }
        else if (params.getChild(0).isNodeType("ARRAY_VARIABLE_PARAM")){
            t2 = "ARR " + t2;
        }
        assertType(t1, t2, args.getLineNumber());
        this.checkArgList(args.getChild(1), params.getChild(1));
    }

    private void typeVar(BPLParseTreeNode tree) throws BPLTypeCheckerException {
        if (DETAILDEBUG) {
            System.out.println(tree);
        }
        this.linkDeclaration(tree);
        if (tree.isNodeType("ARRAY_VARIABLE")) {
            this.typeCheckExpression(tree.getChild(1));
        }
    }

    private String checkVar(BPLParseTreeNode tree)
            throws BPLTypeCheckerException{
        String treeType = "";
        BPLParseTreeNode dec = tree.getDeclaration();
        if (tree.isNodeType("POINTER_VARIABLE")) {
            if (!dec.getNodeType().contains("POINTER")) {
                this.varException(tree, dec);
            }
        }
        else if (tree.isNodeType("ARRAY_ELMT_VARIABLE")){
            if (!dec.getNodeType().contains("ARRAY")) {
                this.varException(tree, dec);
            }
        }
        else {
            if (dec.getNodeType().contains("POINTER")) {
                treeType = "PTR ";
            }
            else if (dec.getNodeType().contains("ARRAY")) {
                treeType = "ARR ";
            }
            else if (!dec.isNodeType("VARIABLE_DECLARATION")
                    && !dec.isNodeType("VARIABLE_PARAM")) {
                this.varException(tree, dec);
            }
        }
        treeType += dec.getChild(0).getNodeType();
        return treeType;
    }

    private void typeCheckIf(BPLParseTreeNode tree)
            throws BPLTypeCheckerException {
        if (DETAILDEBUG) {
            System.out.println(tree);
        }
        this.typeCheckExpression(tree.getChild(0));
        this.typeCheckStatement(tree.getChild(1));
        if (tree.numChildren() > 2) {
            this.typeCheckStatement(tree.getChild(2));
        }
    }

    private void typeCheckWhile(BPLParseTreeNode tree)
            throws BPLTypeCheckerException {
        if (DETAILDEBUG) {
            System.out.println(tree);
        }
        this.typeCheckExpression(tree.getChild(0));
        this.typeCheckStatement(tree.getChild(1));
    }

    private void typeCheckReturn(BPLParseTreeNode tree)
            throws BPLTypeCheckerException {
        if (DETAILDEBUG) {
            System.out.println(tree);
        }
        if (tree.numChildren() > 0) {
            this.typeCheckExpression(tree.getChild(0));
        }
    }

    private void typeCheckWrite(BPLParseTreeNode tree)
            throws BPLTypeCheckerException {
        if (DETAILDEBUG) {
            System.out.println(tree);
        }
        this.typeCheckExpression(tree.getChild(0));
    }

    /** Some helper functions */

    private void linkDeclaration(BPLParseTreeNode tree)
            throws BPLTypeCheckerException{
        String name = tree.getName();
        BPLParseTreeNode node = this.findLocalDec(name);
        if (node == null) {
            node = this.findGlobalDec(name);
        }
        if (node == null) {
            throw new BPLTypeCheckerException(tree.getLineNumber(),
                "Cannot find reference for " + name);
        }
        tree.setDeclaration(node);
        if (DEBUG) {
            this.printLinkedNodes(tree, node);
        }
    }

    private BPLParseTreeNode findLocalDec(String name) {
        Iterator<BPLParseTreeNode> it = this.localDeclarations.iterator();
        while (it.hasNext()) {
            BPLParseTreeNode n = it.next();
            if (n.getName().equals(name)) {
                return n;
            }
        }
        return null;
    }

    private BPLParseTreeNode findGlobalDec(String name) {
        return this.globalSymbolTable.get(name);
    }

    private void printLinkedNodes(BPLParseTreeNode t1, BPLParseTreeNode t2){
        System.out.println(t1 + " linked to " + t2 + " " + t2.getName());
    }

    private void assertType(String t1, String t2, int lineNumber)
            throws BPLTypeCheckerException{
        if (DEBUG) {
            System.out.println("Line " + lineNumber + ": asserting types "
                + t1 + " and " + t2);
        }
        if (!t1.equals(t2)) {
            throw new BPLTypeCheckerException(lineNumber ,
                "Type error");
        }
    }

    private void varException(BPLParseTreeNode tree, BPLParseTreeNode dec)
            throws BPLTypeCheckerException{
        throw new BPLTypeCheckerException(tree.getLineNumber(),
            "Invalid variable " + tree.getNodeType() + " declared as "
            + dec.getNodeType());
    }

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

    public static void main(String args[]) throws BPLTypeCheckerException {
        String fileName = args[0];
        BPLTypeChecker typeChecker = new BPLTypeChecker(fileName, true, false);
    }
}
