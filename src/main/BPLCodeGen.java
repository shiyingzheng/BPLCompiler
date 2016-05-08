import java.util.HashMap;


public class BPLCodeGen {
    private BPLParseTreeNode parseTree;
    private HashMap<String, String> stringTable;
    private int stringNumber;
    private int labelNumber;

    public BPLCodeGen(String fileName) throws BPLTypeCheckerException {
        BPLTypeChecker typeChecker = new BPLTypeChecker(fileName, false, false);
        this.stringTable = new HashMap<String, String>();
        this.stringNumber = 0;
        this.labelNumber = 0;
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
        IdNode id = (IdNode)t.getChild(1);
        id.setDepth(depth);
        if (t.isNodeType("ARRAY_VARIABLE_DECLARATION")){
            pos += ((IntValueNode)t.getChild(2)).getValue() - 1;
        }
        id.setPosition(pos);
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
            this.output(".comm " + t.getChild(1).getName() + ", " +
                8*((IntValueNode)t.getChild(2)).getValue() + ", 32");
        }
        else {
            this.output(".comm " + t.getChild(1).getName() + ", 8, 32");
        }
    }

    private void generateFunction(BPLParseTreeNode t) {
        this.output();
        this.output(t.getChild(1).getName() + ":");
        this.output("movq %rsp, %rbx");
        // allocate space for max position local decs
        this.generateCompound(t.getChild(3));
        // deallocate space for local decs
        this.output("ret");
    }

    private void generateCompound(BPLParseTreeNode t) {
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
        this.output("movl $12, %eax", "placeholder for assignment eval");
    }

    private void generateCompExp(BPLParseTreeNode t) {
        this.generateE(t.getChild(0));
        if (t.numChildren() == 1) {
            return;
        }
        this.output("push %rax", "comparison");
        this.generateE(t.getChild(2));
        String op = t.getChild(1).getNodeType();
        if (op.equals("<=")) {
            op = "jl";
        }
        else if (op.equals("<")) {
            op = "jle";
        }
        else if (op.equals("==")) {
            op = "je";
        }
        else if (op.equals("!=")) {
            op = "jne";
        }
        else if (op.equals(">=")) {
            op = "jg";
        }
        else if (op.equals(">")) {
            op = "jge";
        }
        int label = this.getLabelNumber();
        this.output("cmpl %eax, 0(%rsp)");
        this.output(op + " Label" + label);
        this.output("movl $0, %eax");
        this.output("jmp Label" + this.getLabelNumber());
        this.output("Label" + label + ":");
        this.output("movl $1, %eax");
        this.output("Label" + (label+1) + ":");
        this.output("addq $8, %rsp");
    }

    private void generateE(BPLParseTreeNode t) {
        if (t.isNodeType("E")) {
            this.generateT(t.getChild(2));
            this.output("push %rax", "addition/subtraction here");
            this.generateT(t.getChild(0));
            String op = t.getChild(1).getNodeType();
            if (op.equals("+")) {
                op = "addq";
            }
            else {
                op = "subq";
            }
            this.output(op + " 0(%rsp), %rax");
            this.output("addq $8, %rsp");
        }
        else {
            this.generateT(t);
        }
    }

    private void generateT(BPLParseTreeNode t) {
        if (t.isNodeType("T")) {
            String op = t.getChild(1).getNodeType();
            this.generateF(t.getChild(2));
            if (op.equals("*")) {
                this.output("push %rax", "multiplication here");
                this.generateF(t.getChild(0));
                this.output("imul 0(%rsp) , %eax");
                this.output("addq $8, %rsp");
            }
            else {
                this.output("movl %eax, %ebp", "division here");
                this.generateF(t.getChild(0));
                this.output("cltq");
                this.output("cqto");
                this.output("idivl %ebp");
                if (op.equals("%")) {
                    this.output("movl %edx, %eax");
                }
            }
        }
        else {
            this.generateF(t);
        }
    }

    private void generateF(BPLParseTreeNode t) {
        if (t.isNodeType("F")) {
            this.generateFactor(t.getChild(0));
        }
        else if (t.isNodeType("NEG_F")) {

        }
        else if (t.isNodeType("REF_F")) {

        }
        else if (t.isNodeType("DEREF_F")) {

        }
    }

    private void generateFactor(BPLParseTreeNode t) {
        BPLParseTreeNode exp = t.getChild(0);
        if (exp.isNodeType("<num>")){
            int i = ((IntValueNode)exp).getValue();
            this.output("movl $" + i + ", %eax");
        }
        else if (exp.isNodeType("<string>")) {
            String n = this.stringTable.get(((StringValueNode)exp).getValue());
            this.output("movq " + n + ", %rax");
        }
        else if (exp.isNodeType("read()")) {
            this.generateRead(exp);
        }
        else if (exp.isNodeType("<id>")) {

        }
        else if (exp.isNodeType("COMP_EXPRESSION")) {
            this.generateCompExp(exp);
        }
        else if (exp.isNodeType("ASSIGNMENT_EXPRESSION")) {

        }
        else if (exp.isNodeType("FUNCTION_CALL")) {
            this.generateFunCall(exp);
        }
    }

    private void generateFunCall(BPLParseTreeNode t) {
        //push arguments onto stack
        this.output("push %rbx", "Push frame pointer");
        this.output("call " + t.getChild(1).getName(), "Call function");
        this.output("pop %rbx", "Retrieve frame pointer");
        //remove arguments from stack
    }

    private void generateArgs(BPLParseTreeNode t) {

    }

    private void generateIf(BPLParseTreeNode t) {
        this.generateExpression(t.getChild(0));
        this.output("cmpl $0, %eax", "if statement");
        int label = this.getLabelNumber();
        this.output("je Label" + label);
        this.generateStmt(t.getChild(1));
        this.output("Label" + label + ":");
        if (t.numChildren() > 2) {
            this.generateStmt(t.getChild(2));
        }
    }

    private void generateWhile(BPLParseTreeNode t){
        //TODO test when variables are implemented
        this.output("Label" + this.getLabelNumber() + ":");
        this.generateExpression(t.getChild(0));
        this.output("cmpl $0, %eax", "while statement");
        int label = this.getLabelNumber();
        this.output("je Label" + label);
        this.generateStmt(t.getChild(1));
        this.output("jmp Label" + (label - 1));
        this.output("Label" + label + ":");
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
            this.output("movl %eax, %esi", "write int");
            this.output("movq $.WriteIntString, %rdi");
        }
        else if (exp.isExpType("STRING")) {
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
        this.output("subq $40, %rsp", "read input");
        this.output("movq %rsp, %rsi");
        this.output("addq $24, %rsi");
        this.output("movq $.ReadIntString, %rdi");
        this.output("movl $0, %eax");
        this.output("push %rbx");
        this.output("call scanf");
        this.output("pop %rbx");
        this.output("movq 24(%rsp), %rax");
        this.output("addq $40, %rsp");
    }

    private int getStringNumber() {
        return this.stringNumber++;
    }

    private int getLabelNumber() {
        return this.labelNumber++;
    }

    private void output() {
        System.out.println();
    }

    private void output(String str) {
        if (!(str.charAt(0) == '.' || str.contains(":"))) {
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
