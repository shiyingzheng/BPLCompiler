import java.util.LinkedList;
import java.util.ArrayList;
import java.util.HashMap;


public class BPLTypeChecker {
    private final boolean DEBUG = true;
    private final boolean DETAILDEBUG = true;

    private BPLParser parser;
    private BPLParseTreeNode tree;
    private HashMap<String, BPLParseTreeNode> globalSymbolTable;
    private LinkedList<BPLParseTreeNode> localDeclarations;

    public BPLTypeChecker(String fileName) throws BPLTypeCheckerException{
        try {
            parser = new BPLParser(fileName);
        }
        catch (Exception e) {
            throw new BPLTypeCheckerException("Error constructing parse tree");
        }
        this.tree = parser.getParseTree();
        this.globalSymbolTable = new HashMap<String, BPLParseTreeNode>();
        this.localDeclarations = new LinkedList<BPLParseTreeNode>();
        this.type(this.tree);
        this.check(this.tree);
        if (DETAILDEBUG) {
            System.out.println("\nGlobal symbol table:");
            System.out.println(this.globalSymbolTable);
            System.out.println("Local decs list (should be empty):");
            System.out.println(this.localDeclarations);
        }
    }

    private void type(BPLParseTreeNode tree) {
        if (tree.isType("PROGRAM")){
            this.typeDecList(tree.getChild(0));
        }
    }

    private void typeDecList(BPLParseTreeNode tree) {
        if (tree.isType("DECLARATION_LIST")) {
            for (int i = 0; i < tree.numChildren(); i++) {
                this.typeDecList(tree.getChild(i));
            }
        }
        else {
            this.typeDec(tree);
        }
    }

    private void typeDec(BPLParseTreeNode tree){
        String name = tree.getName();
        this.globalSymbolTable.put(tree.getName(), tree);
        if (DETAILDEBUG){
            System.out.print(tree);
            System.out.println(" " + name);
        }
        if (tree.isType("FUNCTION")) {
            this.typeFunction(tree);
        }
    }

    private void typeFunction(BPLParseTreeNode tree) {
        int numParams = this.typeParams(tree.getChild(2));
        this.typeCompound(tree.getChild(3));
        while (numParams > 0) {
            numParams--;
            this.localDeclarations.removeFirst();
        }
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

    private void typeCompound(BPLParseTreeNode tree) {
        int numLocalDecs = this.typeLocalDecs(tree.getChild(0));
        this.typeStatementList(tree.getChild(1));
        while (numLocalDecs > 0) {
            numLocalDecs--;
            this.localDeclarations.removeFirst();
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

    private void typeStatementList(BPLParseTreeNode tree) {
        if (tree.isEmpty()) {
            return;
        }
        this.typeStatement(tree.getChild(0));
        this.typeStatementList(tree.getChild(1));
    }

    private void typeStatement(BPLParseTreeNode tree) {
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

    private void typeExpressionStmt(BPLParseTreeNode tree) {
        if (tree.isEmpty()) {
            return;
        }
        this.typeExpression(tree.getChild(0));
    }

    private void typeExpression(BPLParseTreeNode tree) {
        if (DETAILDEBUG) {
            System.out.println(tree);
        }
        if (tree.isType("ASSIGNMENT_EXPRESSION")){
            this.typeVar(tree.getChild(0));
            this.typeExpression(tree.getChild(1));
        }
        else {

        }
    }

    private void typeVar(BPLParseTreeNode tree) {
        if (DETAILDEBUG) {
            System.out.println(tree);
        }
        //TODO
    }

    private void typeIf(BPLParseTreeNode tree) {
        if (DETAILDEBUG) {
            System.out.println(tree);
        }
        this.typeExpression(tree.getChild(0));
        this.typeStatement(tree.getChild(1));
        if (tree.numChildren() > 2) {
            this.typeStatement(tree.getChild(2));
        }
    }

    private void typeWhile(BPLParseTreeNode tree) {
        if (DETAILDEBUG) {
            System.out.println(tree);
        }
        this.typeExpression(tree.getChild(0));
        this.typeStatement(tree.getChild(1));
    }

    private void typeReturn(BPLParseTreeNode tree) {
        if (DETAILDEBUG) {
            System.out.println(tree);
        }
        if (tree.numChildren() > 0) {
            this.typeExpression(tree.getChild(0));
        }
    }

    private void typeWrite(BPLParseTreeNode tree) {
        if (DETAILDEBUG) {
            System.out.println(tree);
        }
        this.typeExpression(tree.getChild(0));
    }

    private void check(BPLParseTreeNode tree) {
        //TODO
    }

    private void printLinkedNodes(){
        //TODO
    }

    public static void main(String args[]) throws BPLTypeCheckerException {
        String fileName = args[0];
        BPLTypeChecker typeChecker = new BPLTypeChecker(fileName);
    }
}
