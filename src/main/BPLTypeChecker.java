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
    //TODO: save all tree types once they're found, use them if available
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
        if (type.equals("INT_SPECIFIER")) {
            type = "INT";
        }
        else if (type.equals("STRING_SPECIFIER")) {
            type = "STRING";
        }
        if (tree.isType("RETURN_STATEMENT")) {
            if (DETAILDEBUG) {
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
        }
        else {
            this.typeCompExp(tree);
        }
        this.checkExpression(tree);
    }

    private String checkExpression(BPLParseTreeNode tree)
            throws BPLTypeCheckerException{
        String treeType = tree.getTreeType();
        if (treeType != null) {
            return treeType;
        }
        if (tree.isType("ASSIGNMENT_EXPRESSION")) {
            treeType = this.checkAssignmentExpression(tree);
        }
        else {
            treeType = this.checkCompExp(tree);
        }
        tree.setTreeType(treeType);
        return treeType;
    }

    private String checkAssignmentExpression(BPLParseTreeNode tree)
            throws BPLTypeCheckerException{
        BPLParseTreeNode var = tree.getChild(0);
        String t1 = this.checkVar(var);
        String t2 = this.checkExpression(tree.getChild(1));
        this.assertType(t1, t2, tree.getLineNumber());
        if (var.isType("VARIABLE")
                && var.getDeclaration().getNodeType().contains("POINTER")) {
            this.checkPtrAddrAssign(var, tree.getChild(1));
        }
        return t1;
    }

    private void checkPtrAddrAssign(BPLParseTreeNode ptr, BPLParseTreeNode e)
            throws BPLTypeCheckerException {
        boolean ref = false;
        while(!e.isType("<id>")) {
            if (e.isType("REF_F")) {
                ref = true;
            }
            e = e.getChild(0);
        }
        if (ref == false) {
            throw new BPLTypeCheckerException(ptr.getLineNumber(),
                "right hand side of pointer address assignment not valid");
        }
        String t1 = ptr.getDeclaration().getChild(0).getNodeType();
        String t2 = e.getDeclaration().getChild(0).getNodeType();
        if (DETAILDEBUG) {
            System.out.println("Line " + ptr.getLineNumber() +
                ": checking pointer address for " + ptr.getName() + " and "
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
        String treeType = tree.getTreeType();
        if (treeType != null) {
            return treeType;
        }
        String t1 = this.checkETF(tree.getChild(0));
        if (tree.numChildren() > 1) {
            String t2 = this.checkETF(tree.getChild(2));
            assertType(t1, t2, tree.getLineNumber());
        }
        tree.setTreeType(t1);
        return t1;
    }

    private void typeETF(BPLParseTreeNode tree) throws BPLTypeCheckerException {
        if (DETAILDEBUG) {
            System.out.println(tree + " typing");
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

    private String checkETF(BPLParseTreeNode tree)
            throws BPLTypeCheckerException{
        if (DETAILDEBUG) {
            System.out.println(tree + " checking");
        }
        String treeType = tree.getTreeType();
        if (treeType != null) {
            return treeType;
        }
        if (tree.isType("F") || tree.isType("REF_F") || tree.isType("DEREF_F")){
            treeType = this.checkF(tree);
            tree.setTreeType(treeType);
            return treeType;
        }
        String t1 = this.checkETF(tree.getChild(0));
        if (tree.numChildren() > 1) {
            String t2 = this.checkETF(tree.getChild(2));
            assertType(t1, t2, tree.getLineNumber());
        }
        tree.setTreeType(t1);
        return t1;
    }

    private void typeF(BPLParseTreeNode tree) throws BPLTypeCheckerException {
        if (tree.numChildren() > 1) {
            this.typeF(tree.getChild(1));
        }
        else {
            this.typeFactor(tree.getChild(0));
        }
    }

    private String checkF(BPLParseTreeNode tree) throws BPLTypeCheckerException{
        String treeType = tree.getTreeType();
        if (treeType != null) {
            return treeType;
        }
        if (tree.numChildren() > 1) {
            treeType = this.checkF(tree.getChild(1));
        }
        else {
            if (tree.isType("REF_F")) {
                treeType = "INT";
                this.checkFactor(tree.getChild(0));
            }
            else if (tree.isType("DEREF_F")) {
                this.checkFactor(tree.getChild(0));
                BPLParseTreeNode f = tree;
                while(!f.isType("<id>")) {
                    f = f.getChild(0);
                }
                treeType = f.getDeclaration().getChild(0).getNodeType();
                if (treeType.equals("INT_SPECIFIER")) {
                    treeType = "INT";
                }
                else if (treeType.equals("STRING_SPECIFIER")) {
                    treeType = "STRING";
                }
                else {
                    throw new BPLTypeCheckerException(tree.getLineNumber(),
                        "Invalid de-reference");
                }
            }
            else {
                treeType = this.checkFactor(tree.getChild(0));
            }
        }
        tree.setTreeType(treeType);
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

    private String checkFactor(BPLParseTreeNode tree)
            throws BPLTypeCheckerException{
        String treeType = null;
        if (treeType != null) {
            return treeType;
        }
        BPLParseTreeNode child = tree.getChild(0);
        if (child.isType("read()")) {
            child.setTreeType("INT");
            treeType = "INT";
        }
        else {
            if (child.isType("<id>")) {

                if (tree.numChildren() > 1){

                }
            }
            else if (child.isType("FUNCTION_CALL")) {
                treeType = this.checkFunctonCall(child);
            }
            else if (child.isType("<num>")) {
                treeType = "INT";
            }
            else if (child.isType("<string>")) {
                treeType = "STRING";
            }
            else { // expression
                treeType = this.checkExpression(child);
            }
        }
        tree.setTreeType(treeType);
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

    private String checkFunctonCall(BPLParseTreeNode tree) {
        //TODO
        return null;
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
        // TODO: allow A = B where A and B are array pointers
        BPLParseTreeNode dec = tree.getDeclaration();
        if (tree.isType("VARIABLE") && dec.getNodeType().contains("POINTER")) {
            return "INT";
        }
        if (tree.isType("POINTER_VARIABLE")) {
            if (!dec.getNodeType().contains("POINTER")) {
                this.varException(tree, dec);
            }
        }
        else if (tree.isType("ARRAY_ELMT_VARIABLE")){
            if (!dec.getNodeType().contains("ARRAY")) {
                this.varException(tree, dec);
            }
        }
        else {
            if (!dec.isType("VARIABLE_DECLARATION")
                    && !dec.isType("VARIABLE_PARAM")) {
                this.varException(tree, dec);
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

    private void varException(BPLParseTreeNode tree, BPLParseTreeNode dec)
            throws BPLTypeCheckerException{
        throw new BPLTypeCheckerException(tree.getLineNumber(),
            "Invalid variable " + tree.getNodeType() + " declared as "
            + dec.getNodeType());
    }

    public static void main(String args[]) throws BPLTypeCheckerException {
        String fileName = args[0];
        BPLTypeChecker typeChecker = new BPLTypeChecker(fileName, true, true);
    }
}
