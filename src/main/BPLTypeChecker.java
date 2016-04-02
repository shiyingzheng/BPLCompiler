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

    /**
    * The first pass to link all variables with their declarations
    * Exception will be thrown if no declaration is found
    */

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
        this.typeCompound(tree.getChild(3));
        this.checkCompound(tree.getChild(3));
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

    private void typeCompound(BPLParseTreeNode tree)
            throws BPLTypeCheckerException {
        int numLocalDecs = this.typeLocalDecs(tree.getChild(0));
        this.typeStatementList(tree.getChild(1));
        while (numLocalDecs > 0) {
            numLocalDecs--;
            this.localDeclarations.removeFirst();
        }
    }

    private void checkCompound(BPLParseTreeNode tree) {
        //TODO
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

    private void typeStatementList(BPLParseTreeNode tree)
            throws BPLTypeCheckerException{
        if (tree.isEmpty()) {
            return;
        }
        this.typeStatement(tree.getChild(0));
        this.typeStatementList(tree.getChild(1));
    }

    private void typeStatement(BPLParseTreeNode tree)
            throws BPLTypeCheckerException{
        if (tree.isType("EXPRESSION_STMT")) {
            this.typeExpressionStmt(tree);
        }
        else if (tree.isType("COMPOUND_STATEMENT")) {
            this.typeCompound(tree);
        }
        else if (tree.isType("IF_STATEMENT")){
            this.typeIf(tree);
        }
        else if (tree.isType("WHILE_STATEMENT")){
            this.typeWhile(tree);
        }
        else if (tree.isType("RETURN_STATEMENT")) {
            this.typeReturn(tree);
        }
        else if (tree.isType("WRITE_STATEMENT")) {
            this.typeWrite(tree);
        }
        else if (tree.isType("WRITELN_STATEMENT")) {
            if (DETAILDEBUG) {
                System.out.println(tree);
            }
        }
    }

    private void typeExpressionStmt(BPLParseTreeNode tree)
            throws BPLTypeCheckerException{
        if (tree.isEmpty()) {
            return;
        }
        this.typeExpression(tree.getChild(0));
    }

    private void typeExpression(BPLParseTreeNode tree)
            throws BPLTypeCheckerException{
        if (DETAILDEBUG) {
            System.out.println(tree);
        }
        if (tree.isType("ASSIGNMENT_EXPRESSION")){
            this.typeVar(tree.getChild(0));
            this.typeExpression(tree.getChild(1));
        }
        else {
            this.typeCompExp(tree);
        }
    }

    private String checkExpression(BPLParseTreeNode tree) {
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
        if (tree.isType("F")) {
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
            if (tree.getChild(0).isType("-")) {
                this.typeF(tree.getChild(1));
            }
            else {
                this.typeFactor(tree.getChild(1));
            }
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
            this.typeExpression(tree.getChild(1));
            return;
        }
        BPLParseTreeNode child = tree.getChild(0);
        if (child.isType("EXPRESSION")
            || child.isType("ASSIGNMENT_EXPRESSION")) {
            this.typeExpression(child);
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
        this.typeExpression(tree.getChild(0));
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
            this.typeExpression(tree.getChild(1));
        }
    }

    private void typeIf(BPLParseTreeNode tree) throws BPLTypeCheckerException {
        if (DETAILDEBUG) {
            System.out.println(tree);
        }
        this.typeExpression(tree.getChild(0));
        this.typeStatement(tree.getChild(1));
        if (tree.numChildren() > 2) {
            this.typeStatement(tree.getChild(2));
        }
    }

    private void typeWhile(BPLParseTreeNode tree)
            throws BPLTypeCheckerException {
        if (DETAILDEBUG) {
            System.out.println(tree);
        }
        this.typeExpression(tree.getChild(0));
        this.typeStatement(tree.getChild(1));
    }

    private void typeReturn(BPLParseTreeNode tree)
            throws BPLTypeCheckerException {
        if (DETAILDEBUG) {
            System.out.println(tree);
        }
        if (tree.numChildren() > 0) {
            this.typeExpression(tree.getChild(0));
        }
    }

    private void typeWrite(BPLParseTreeNode tree)
            throws BPLTypeCheckerException {
        if (DETAILDEBUG) {
            System.out.println(tree);
        }
        this.typeExpression(tree.getChild(0));
    }

    /**
    * The second pass to check the types and match them
    * Exception will be thrown if the types do not match
    */

/*    private void check(BPLParseTreeNode tree) {
        this.checkDecList(tree.getChild(0));
    }

    private void checkDecList(BPLParseTreeNode tree) {
        if (tree.isType("DECLARATION_LIST")) {
            for (int i = 0; i < tree.numChildren(); i++) {
                this.checkDecList(tree.getChild(i));
            }
        }
        else {
            this.checkDec(tree);
        }
    }

    private void checkDec(BPLParseTreeNode tree) {

    }
*/
    /** Some helper functions */

    private void linkDeclaration(BPLParseTreeNode tree)
            throws BPLTypeCheckerException{
        String name = tree.getName();
        BPLParseTreeNode node = this.findLocalDec(name);
        if (node == null) {
            node = this.findGlobalDec(name);
        }
        if (node == null) {
            throw new BPLTypeCheckerException("Cannot find reference for "
                + name);
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
        //    throw new BPLTypeCheckerException("Line " + lineNumber + ": " +
        //        "expected type " + t2 + " but get " + t1);
        //}
    }

    private void assertEqualTypes(BPLParseTreeNode t1, BPLParseTreeNode t2) {
        //TODO
    }

    public static void main(String args[]) throws BPLTypeCheckerException {
        String fileName = args[0];
        BPLTypeChecker typeChecker = new BPLTypeChecker(fileName, true, true);
    }
}
