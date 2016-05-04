import java.util.HashMap;


public class BPLCodeGen {
    private BPLParseTreeNode parseTree;
    private HashMap<String, String> stringTable;
    private int stringNumber;

    public BPLCodeGen(String fileName) throws BPLTypeCheckerException {
        BPLTypeChecker typeChecker = new BPLTypeChecker(fileName, false, false);
        this.stringTable = new HashMap<String, String>();
        this.stringNumber = 0;
        this.parseTree = typeChecker.getTree();
        this.generateCode();
    }

    private void generateCode() {
        this.generateDataSection();
        this.assignDepth(this.parseTree, 0, 0);
        this.generateTextSection();
    }

    private void generateDataSection() {
        this.output(".section   .rodata");
        this.output(".WriteIntString: .string \"%d\"");
        this.output(".WriteStrString: .string \"%s\"");
        this.output(".WritelnString: .string \"\\n\"");
        this.output(".ReadIntString: .string \"%d\"");
        this.output(".WriteHiBobString: .string \"Hi Bob!\""); // important
        this.generateDataStrings(this.parseTree);
    }

    private void generateTextSection() {
        this.output(".text");
        this.output(".globl main");
        this.generateDeclarations(this.parseTree);
    }

    private void generateDataStrings(BPLParseTreeNode t) {
        if (t.isNodeType("<string>")) {
            String value = ((StringValueNode)t).getValue();
            this.output(".String" + this.stringNumber + ": .string \""
                +  value + "\"");
            this.stringTable.put(value, "$.String" + this.stringNumber);
            this.stringNumber++;
        }
        else {
            for (int i = 0; i < t.numChildren(); i++){
                this.generateDataStrings(t.getChild(i));
            }
        }
    }

    private Integer assignDepth(BPLParseTreeNode t, int depth, int pos) {
        if (t.isNodeType("PARAM_LIST")) {
            this.assignDepthParamList(t, 0);
            return null;
        }
        if (t.getNodeType().contains("VARIABLE_DECLARATION")) {
            this.assignDepthVar(t, depth, pos);
            return pos+1;
        }

        if (t.isNodeType("FUNCTION")) {
            pos = 0;
        }
        else if (t.isNodeType("COMPOUND_STATEMENT")) {
            if (depth == 0) {
                depth = 2;
            }
            else{
                depth++;
            }
        }

        for (int i = 0; i < t.numChildren(); i++){
            Integer p = this.assignDepth(t.getChild(i), depth, pos);
            if (p != null) {
                pos = p;
            }
        }

        if (t.isNodeType("FUNCTION") || t.isNodeType("COMPOUND_STATEMENT")) {
            return null;
        }
        return pos;
    }

    private void assignDepthParamList(BPLParseTreeNode t, int pos) {
        BPLParseTreeNode param = t.getChild(0);
        BPLParseTreeNode rest = t.getChild(1);
        this.assignDepthVar(param, 1, pos);
        if (!rest.isEmpty()) {
            this.assignDepthParamList(rest, pos++);
        }
    }

    private void assignDepthVar(BPLParseTreeNode t, int depth, int pos) {
        ((IdNode)t.getChild(1)).setDepth(depth);
        ((IdNode)t.getChild(1)).setPosition(pos);
        //System.out.println(((IdNode)t.getChild(1)).getId() + " " + depth + " " + pos);
    }

    private void generateDeclarations(BPLParseTreeNode t) {
        if (t.isNodeType("PROGRAM")){
            this.generateDeclarations(t.getChild(0));
        }
        else if (t.isNodeType("DECLARATION_LIST")) {
            for (int i = 0; i < t.numChildren(); i++) {
                this.generateDeclarations(t.getChild(i));
            }
        }
        else if (t.isNodeType("FUNCTION")) {
            this.generateFunction(t);
        }
        else {
            this.generateGlobalVar(t);
        }
    }

    private void generateGlobalVar(BPLParseTreeNode t) {
        if (t.isNodeType("ARRAY_VARIABLE_DECLARATION")) {
            this.output(".comm " + ((IdNode)t.getChild(1)).getId() + ", " +
                8*((IntValueNode)t.getChild(2)).getValue() + ", 32");
        }
        else {
            this.output(".comm " + ((IdNode)t.getChild(1)).getId() + ", 8, 32");
        }
    }

    private void generateFunction(BPLParseTreeNode t) {
        this.output();
        this.output(((IdNode)t.getChild(1)).getId() + ": ");
        // do stuff with params
        this.generateCompound(t.getChild(3));
        this.output("ret");
    }

    private void generateCompound(BPLParseTreeNode t) {
        // do stuff with local decs
        this.generateStmtList(t.getChild(1));
    }

    private void generateStmtList(BPLParseTreeNode t) {
        if (t.isNodeType("STATEMENT_LIST")) {
            for (int i = 0; i < t.numChildren(); i++) {
                this.generateStmtList(t.getChild(i));
            }
        }
        else {
            this.generateStmt(t);
        }
    }

    private void generateStmt(BPLParseTreeNode t) {
        if (t.isNodeType("EXPRESSION_STMT")) {
            this.generateExpressionStmt(t);
        }
        else if (t.isNodeType("COMPOUND_STATEMENT")) {
            this.generateCompound(t);
        }
        else if (t.isNodeType("IF_STATEMENT")){
            this.generateIf(t);
        }
        else if (t.isNodeType("WHILE_STATEMENT")){
            this.generateWhile(t);
        }
        else if (t.isNodeType("RETURN_STATEMENT")) {
            this.generateReturn(t);
        }
        else if (t.isNodeType("WRITE_STATEMENT")) {
            this.generateWrite(t);
        }
        else if (t.isNodeType("WRITELN_STATEMENT")) {
            this.generateWriteln(t);
        }
    }

    private void generateExpressionStmt(BPLParseTreeNode t) {
        this.generateExpression(t.getChild(0));
    }

    private void generateExpression(BPLParseTreeNode t) {
        if (t.isNodeType("ASSIGNMENT_EXPRESSION")) {
            this.generateAssignExp(t);
        }
        else {
            this.generateCompExp(t);
        }
    }

    private void generateAssignExp(BPLParseTreeNode t) {
        //TODO
        this.output("movl $12, %eax", "placeholder for expression eval");
    }

    private void generateCompExp(BPLParseTreeNode t) {
        this.generateE(t.getChild(0));
        if (t.numChildren() > 1) {
            //TODO
        }
    }

    private void generateE(BPLParseTreeNode t) {
        if (t.isNodeType("E")) {
            this.generateT(t.getChild(0));
            // TODO
        }
        else {
            this.generateT(t);
        }
    }

    private void generateT(BPLParseTreeNode t) {
        if (t.isNodeType("T")) {
            this.generateF(t.getChild(0));
            // TODO
        }
        else {
            this.generateF(t);
        }
    }

    private void generateF(BPLParseTreeNode t) {
        if (t.isNodeType("F")) {
            this.generateFactor(t.getChild(0));
        }
        else {
            //TODO
        }
    }

    private void generateFactor(BPLParseTreeNode t) {
        if (t.isExpType("INT")){
            this.output("movl $12, %eax", "placeholder for expression eval");
        }
        else if (t.isExpType("STRING")) {
            BPLParseTreeNode exp = t.getChild(0);
            //System.out.println(exp);
            while (!exp.isNodeType("<string>")) {
                exp = exp.getChild(0);
            }
            String n = this.stringTable.get(((StringValueNode)exp).getValue());
            this.output("movq " + n + ", %rax");
        }
        else if (t.isExpType("read()")) {
            this.generateReturn(t);
        }
    }

    private void generateFunCall(BPLParseTreeNode t) {

    }

    private void generateArgs(BPLParseTreeNode t) {

    }

    private void generateIf(BPLParseTreeNode t) {

    }

    private void generateWhile(BPLParseTreeNode t){

    }

    private void generateReturn(BPLParseTreeNode t) {

    }

    private void generateWriteln(BPLParseTreeNode t) {
        this.output("movl $0, %eax", "writeln");
        this.output("movq $.WritelnString, %rdi");
        this.output("call printf");
    }

    private void generateWrite(BPLParseTreeNode t) {
        BPLParseTreeNode exp = t.getChild(0);
        this.generateExpression(exp);
        if (exp.isExpType("INT")) {
            this.output();
            this.output("movl %eax, %esi", "write int");
            this.output("movq $.WriteIntString, %rdi");
        }
        else if (exp.isExpType("STRING")) {
            this.output();
            this.output("movq %rax, %rsi", "write string");
            this.output("movq $.WriteStrString, %rdi");
        }
        else {
            //TODO, arr, ptr
        }
        this.output("movl $0, %eax");
        this.output("call printf");
    }

    private void generateRead(BPLParseTreeNode t) {
        //TODO:test
        this.output("sub $40, %rsp", "read input");
        this.output("movq %rsp, %rax");
        this.output("sub $24, %rax");
        this.output("movq $rax, $rsi");
        this.output("movq $.ReadIntString, %rdi");
        this.output("call scanf");
        this.output("movl 24(%rsp) %eax");
        this.output("add $40, %rsp");
    }

    private void output() {
        System.out.println();
    }

    private void output(String str) {
        if (!(str.charAt(0) == '.'
                || (str.length() > 1 && str.charAt(str.length()-2) == ':'))) {
            str = "\t" + str;
        }
        System.out.println(str);
    }

    private void output(String str, String comment) {
        this.output(str + " \t# " + comment);
    }

    public static void main(String args[]) throws BPLTypeCheckerException{
        String fileName = args[0];
        BPLCodeGen gen = new BPLCodeGen(fileName);
    }
}
