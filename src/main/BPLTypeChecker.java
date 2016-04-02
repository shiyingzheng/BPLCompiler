import java.util.LinkedList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;


public class BPLTypeChecker {
    private boolean DEBUG;
    private boolean DETAILDEBUG;

    private BPLParser parser;
    private BPLParseTreeNode tree;
    private HashMap<String, BPLParseTreeNode> globalSymbolTable;
    private LinkedList<BPLParseTreeNode> localDeclarations;

    public BPLTypeChecker(String fileName, boolean debug, boolean detailDebug)
            throws BPLTypeCheckerException{
        try {
            parser = new BPLParser(fileName);
        }
        catch (Exception e) {
            throw new BPLTypeCheckerException("Error constructing parse tree");
        }
        this.DEBUG = debug;
        this.DETAILDEBUG = detailDebug;
        this.tree = parser.getParseTree();
        this.globalSymbolTable = new HashMap<String, BPLParseTreeNode>();
        this.localDeclarations = new LinkedList<BPLParseTreeNode>();
        this.typeCheck(this.tree);
        //this.check(this.tree);
        if (DETAILDEBUG) {
            System.out.println("\nGlobal symbol table:");
            System.out.println(this.globalSymbolTable);
            System.out.println("Local decs list (should be empty):");
            System.out.println(this.localDeclarations);
        }
    }

    private void typeCheck(BPLParseTreeNode tree) throws BPLTypeCheckerException{
        if (tree.isType("PROGRAM")){
            this.typeCheckDecList(tree.getChild(0));
        }
    }

    private void typeCheckDecList(BPLParseTreeNode tree)
            throws BPLTypeCheckerException{
        if (tree.isType("DECLARATION_LIST")) {
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
        this.globalSymbolTable.put(tree.getName(), tree);
        if (DETAILDEBUG){
            System.out.print(tree);
            System.out.println(" " + name);
        }
        if (tree.isType("FUNCTION")) {
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
        if (tree.numChildren() > 1) {
            numParams += this.typeParams(tree.getChild(1));
        }
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
        if (tree.getNodeType().equals("RETURN_STATEMENT")) {
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
        int numLocalDecs = 1;
        this.localDeclarations.addFirst(tree.getChild(0));
        if (DETAILDEBUG) {
            System.out.print(tree.getChild(0));
            System.out.println(" " + tree.getChild(0).getName());
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
        if (tree.isType("EXPRESSION_STMT")) {
            this.typeCheckExpressionStmt(tree);
        }
        else if (tree.isType("COMPOUND_STATEMENT")) {
            this.typeCheckCompound(tree);
        }
        else if (tree.isType("IF_STATEMENT")){
            this.typeCheckIf(tree);
        }
        else if (tree.isType("WHILE_STATEMENT")){
            this.typeCheckWhile(tree);
        }
        else if (tree.isType("RETURN_STATEMENT")) {
            this.typeCheckReturn(tree);
        }
        else if (tree.isType("WRITE_STATEMENT")) {
            this.typeCheckWrite(tree);
        }
        else if (tree.isType("WRITELN_STATEMENT")) {
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
        if (tree.isType("ASSIGNMENT_EXPRESSION")){
            this.typeVar(tree.getChild(0));
            this.typeCheckExpression(tree.getChild(1));
            String t1 = this.checkVar(tree.getChild(0));
            String t2 = this.checkExpression(tree.getChild(1));
            this.assertType(t1, t2, tree.getLineNumber());
        }
        else {
            this.typeCompExp(tree);
        }
    }

    private String checkExpression(BPLParseTreeNode tree) {
        String treeType = tree.getTreeType();
        if (treeType != null) {
            return treeType;
        }
        //TODO
        //need to check/set treeType
        return null;
    }

    private void typeCompExp(BPLParseTreeNode tree)
            throws BPLTypeCheckerException {
        this.typeETF(tree.getChild(0));
        if (tree.numChildren() > 1) {
            this.typeETF(tree.getChild(2));
        }
    }

    private void typeETF(BPLParseTreeNode tree) throws BPLTypeCheckerException {
        if (DETAILDEBUG) {
            System.out.println(tree);
        }
        if (tree.isType("F") || tree.isType("REF_F") || tree.isType("DEREF_F")){
            this.typeF(tree);
            return;
        }
        this.typeETF(tree.getChild(0));
        if (tree.numChildren() > 1) {
            this.typeETF(tree.getChild(2));
        }
    }

    private void typeF(BPLParseTreeNode tree) throws BPLTypeCheckerException {
        if (tree.numChildren() > 1) {
            this.typeF(tree.getChild(1));
        }
        else {
            this.typeFactor(tree.getChild(0));
        }
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
        if (child.isType("EXPRESSION")
            || child.isType("ASSIGNMENT_EXPRESSION")) {
            this.typeCheckExpression(child);
        }
        else if (child.isType("FUNCTION_CALL")) {
            this.typeFunctionCall(child);
        }
        else if (child.isType("<id>")) {
            this.linkDeclaration(child);
        }
    }

    private void typeFunctionCall(BPLParseTreeNode tree)
            throws BPLTypeCheckerException {
        if (DETAILDEBUG) {
            System.out.println(tree);
        }
        this.linkDeclaration(tree.getChild(0));
        this.typeArgs(tree.getChild(1));
    }

    private void typeArgs(BPLParseTreeNode tree)
            throws BPLTypeCheckerException {
        if (DETAILDEBUG) {
            System.out.println(tree);
        }
        if (!tree.isEmpty()) {
            this.typeArgList(tree.getChild(0));
        }
    }

    private void typeArgList(BPLParseTreeNode tree)
            throws BPLTypeCheckerException {
        this.typeCheckExpression(tree.getChild(0));
        if (tree.numChildren() > 1) {
            this.typeArgList(tree.getChild(1));
        }
    }

    private void typeVar(BPLParseTreeNode tree) throws BPLTypeCheckerException {
        if (DETAILDEBUG) {
            System.out.println(tree);
        }
        this.linkDeclaration(tree);
        if (tree.isType("ARRAY_VARIABLE")) {
            this.typeCheckExpression(tree.getChild(1));
        }
    }

    private String checkVar(BPLParseTreeNode tree)
            throws BPLTypeCheckerException{
        String treeType = tree.getTreeType();
        if (treeType != null) {
            return treeType;
        }
        BPLParseTreeNode dec = tree.getDeclaration();
        if (tree.getNodeType().equals("POINTER_VARIABLE")) {
            if (!dec.getNodeType().contains("POINTER")) {
                throw new BPLTypeCheckerException(tree.getLineNumber(),
                    "Invalid assignment");
            }
        }
        else if (tree.getNodeType().equals("ARRAY_ELMT_VARIABLE")){
            if (!dec.getNodeType().contains("ARRAY")) {
                throw new BPLTypeCheckerException(tree.getLineNumber(),
                    "Invalid assignment");
            }
        }
        else {
            if (!dec.getNodeType().equals("VARIABLE_DECLARATION")
                    && !dec.getNodeType().equals("VARIABLE_PARAM")) {
                throw new BPLTypeCheckerException(tree.getLineNumber(),
                    "Invalid assignment");
            }
        }
        String nodeType = dec.getChild(0).getNodeType();
        if (nodeType.equals("INT_SPECIFIER")) {
            tree.setTreeType("INT");
            return "INT";
        }
        if (nodeType.equals("STRING_SPECIFIER")) {
            tree.setTreeType("STRING");
            return "STRING";
        }
        return null;
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
        //TODO
        if (DETAILDEBUG) {
            System.out.println("Line " + lineNumber + " asserting types "
                + t1 + " and " + t2);
        }
        //if (!t1.equals(t2)) {
        //    throw new BPLTypeCheckerException(lineNumber ,
        //        "expected type " + t2 + " but get " + t1);
        //}
    }

    public static void main(String args[]) throws BPLTypeCheckerException {
        String fileName = args[0];
        BPLTypeChecker typeChecker = new BPLTypeChecker(fileName, true, true);
    }
}
