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
            pos = this.assignDepthVar(t, depth, pos);
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
            this.assignDepthParamList(rest, ++pos);
        }
    }

    private Integer assignDepthVar(BPLParseTreeNode t, int depth, int pos) {
        IdNode id = (IdNode)t.getChild(1);
        id.setDepth(depth);
        if (t.isNodeType("ARRAY_VARIABLE_DECLARATION")){
            pos += ((IntValueNode)t.getChild(2)).getValue() - 1;
        }
        id.setPosition(pos);
        return pos;
    }

    private Integer findMaxPosition(BPLParseTreeNode t) {
        if (t.getNodeType().contains("VARIABLE_DECLARATION")) {
            return ((IdNode)t.getChild(1)).getPosition();
        }
        else if (t.numChildren() == 0) {
            return null;
        }
        Integer max = null;
        for (int i = 0; i < t.numChildren(); i++) {
            Integer pos = this.findMaxPosition(t.getChild(i));
            if (max == null) {
                max = pos;
            }
            else if (pos != null && pos > max) {
                max = pos;
            }
        }
        return max;
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
        Integer maxPos = this.findMaxPosition(t.getChild(3));
        if (maxPos != null) {
            this.output("subq $" + 8 * (maxPos + 1) + ", %rsp",
                "Allocate space for local variables");
        }
        this.generateCompound(t.getChild(3));
        if (maxPos != null) {
            this.output("addq $" + 8 * (maxPos + 1) + ", %rsp",
                "Deallocate space for local variables");
        }
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
        BPLParseTreeNode exp = t.getChild(1);
        BPLParseTreeNode var = t.getChild(0);
        BPLParseTreeNode dec = var.getDeclaration();
        IdNode id = (IdNode)dec.getChild(1);
        String name = id.getId();
        this.generateExpression(exp);
        if (dec.getNodeType().contains("ARRAY")) {
            this.output("push %rax");
            this.generateExpression(var.getChild(1));
            this.output("push %rax");
            this.generateArrayAddress(dec);
            this.output("movq %rax, %rdx");
            this.output("pop %rax");
            this.output("imul $8, %eax");
            this.output("addq %rax, %rdx");
            this.output("pop %rax");
            this.output("movq %rax, 0(%rdx)", "assign to array elmt");
        }
        else if (var.isNodeType("VARIABLE")){
            if (id.getDepth() >= 1) { // local decs and params
                int offset;
                if (id.getDepth() == 1) {
                    offset = 16 + 8 * id.getPosition();
                }
                else {
                    offset = -8 - 8 * id.getPosition();
                }
                this.output("movq %rax, " + offset + "(%rbx)",
                    "assign to variable " + name);
            }
            else { // globals
                this.output("movq %rax, " + name,
                    "assign to global variable " + name);
            }
        }
        else {
            if (id.getDepth() >= 1) {
                int offset;
                if (id.getDepth() == 1) {
                    offset = 16 + 8 * id.getPosition();
                }
                else {
                    offset = -8 - 8 * id.getPosition();
                }
                this.output("movq " + offset + "(%rbx), %rdx");
                this.output("movq %rax, 0(%rdx)",
                    "assign to pointer variable " + name);
            }
            else {
                this.output("movq $"+ name + ", %rdx",
                    "assign to global pointer " + name);
                this.output("movq %rax, 0(%rdx)",
                    "assign to global pointer variable " + name);
            }
        }
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
            op = "jle";
        }
        else if (op.equals("<")) {
            op = "jl";
        }
        else if (op.equals("==")) {
            op = "je";
        }
        else if (op.equals("!=")) {
            op = "jne";
        }
        else if (op.equals(">=")) {
            op = "jge";
        }
        else if (op.equals(">")) {
            op = "jg";
        }
        String label1 = this.getLabel();
        String label2 = this.getLabel();
        this.output("cmpl %eax, 0(%rsp)");
        this.output(op + " " + label1);
        this.output("movl $0, %eax");
        this.output("jmp " + label2);
        this.output(label1 + ":");
        this.output("movl $1, %eax");
        this.output(label2 + ":");
        this.output("addq $8, %rsp");
    }

    private void generateE(BPLParseTreeNode t) {
        if (t.isNodeType("E")) {
            this.generateT(t.getChild(2));
            this.output("push %rax", "addition/subtraction here");
            this.generateE(t.getChild(0));
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
                this.generateT(t.getChild(0));
                this.output("imul 0(%rsp) , %eax");
                this.output("addq $8, %rsp");
            }
            else {
                this.output("movl %eax, %ebp", "division here");
                this.generateT(t.getChild(0));
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
            this.generateF(t.getChild(0));
            this.output("neg %eax", "negation");
        }
        else if (t.isNodeType("REF_F")) { // &ptr
            BPLParseTreeNode f = t.getChild(0);
            BPLParseTreeNode var = f.getChild(0);
            BPLParseTreeNode dec = var.getDeclaration();
            if (f.numChildren() > 1) {
                generateArrayElmtFactorAddress(f);
            }
            else {
                IdNode id = (IdNode)dec.getChild(1);
                String name = id.getId();
                if (id.getDepth() >= 2) { // local decs
                    int offset = -8 - 8 * id.getPosition();
                    this.output("movq %rbx, %rax",
                        "pointer reference local var " + name);
                    this.output("addq $" + offset + ", %rax");
                }
                else if (id.getDepth() == 1) { // params
                    int offset = 16 + 8 * id.getPosition();
                    this.output("movq %rbx, %rax",
                        "pointer reference param " + name);
                    this.output("addq $" + offset + ", %rax");
                }
                else { // globals
                    this.output("movq $" + name + ", %rax",
                        "pointer reference global variable " + name);
                }
            }
        }
        else if (t.isNodeType("DEREF_F")) { // *ptr
            this.generateFactor(t.getChild(0));
            this.output("movq 0(%rax), %rax", "pointer dereference");
        }
    }

    private void generateFactor(BPLParseTreeNode t) {
        BPLParseTreeNode exp = t.getChild(0);
        if (t.numChildren() > 1) {
            this.generateArrayElmtFactorAddress(t);
            this.output("movq 0(%rax), %rax");
        }
        else if (exp.isNodeType("<num>")){
            int i = ((IntValueNode)exp).getValue();
            this.output("movq $" + i + ", %rax", "evaluate number");
        }
        else if (exp.isNodeType("<string>")) {
            String n = this.stringTable.get(((StringValueNode)exp).getValue());
            this.output("movq " + n + ", %rax", "evaluate string");
        }
        else if (exp.isNodeType("read()")) {
            this.generateRead(exp);
        }
        else if (exp.isNodeType("<id>")) {
            BPLParseTreeNode dec = exp.getDeclaration();
            IdNode id = (IdNode)dec.getChild(1);
            String name = id.getId();
            if (dec.getNodeType().contains("ARRAY")) {
                this.generateArrayFactor(dec);
            }
            else if (id.getDepth() >= 2) { // local decs
                int offset = -8 - 8 * id.getPosition();
                this.output("movq " + offset + "(%rbx), %rax",
                    "local variable " + name);
            }
            else if (id.getDepth() == 1) { // params
                int offset = 16 + 8 * id.getPosition();
                this.output("movq " + offset + "(%rbx), %rax",
                    "param " + name);
            }
            else { // globals
                this.output("movq " + name + ", %rax",
                    "global variable " + name);
            }
        }
        else if (exp.isNodeType("COMP_EXPRESSION")) {
            this.generateCompExp(exp);
        }
        else if (exp.isNodeType("ASSIGNMENT_EXPRESSION")) {
            this.generateAssignExp(exp);
        }
        else if (exp.isNodeType("FUNCTION_CALL")) {
            this.generateFunCall(exp);
        }
    }

    private void generateArrayFactor(BPLParseTreeNode t) {
        // this is for <id> that is an array. t is a dec of array
        this.generateArrayAddress(t);
    }

    /* gets the address of the start of the array, where t is the dec of array
     * if need to access an element, add the index*8 to this address
     */
    private void generateArrayAddress(BPLParseTreeNode t) {
        IdNode id = (IdNode)t.getChild(1);
        String name = id.getId();
        int depth = id.getDepth();

        if (depth >= 2) {
            int offset = -8 - 8 * id.getPosition();
            this.output("movq %rbx, %rax", "array factor");
            this.output("addq $" + offset + ", %rax");
        }
        else if (depth == 1) {
            int offset = 16 + 8 * id.getPosition();
            this.output("movq " + offset + "(%rbx), %rax");
        }
        else {
            this.output("movq $" + name + ", %rax");
        }
    }

    private void generateArrayElmtFactorAddress(BPLParseTreeNode t) {
        BPLParseTreeNode var = t.getChild(0);
        BPLParseTreeNode dec = var.getDeclaration();
        IdNode id = (IdNode)dec.getChild(1);
        String name = id.getId();
        int depth = id.getDepth();
        this.generateExpression(t.getChild(1));
        this.output("push %rax", "get array " + name + " element");
        this.generateArrayAddress(dec);
        this.output("movq %rax, %rdx");
        this.output("pop %rax");
        this.output("imul $8, %eax");
        this.output("addq %rdx, %rax");
    }

    private void generateFunCall(BPLParseTreeNode t) {
        int numArgs = this.pushArgs(t.getChild(1));
        this.output("push %rbx", "Push frame pointer");
        this.output("call " + t.getChild(0).getName(), "Call function");
        this.output("pop %rbx", "Retrieve frame pointer");
        this.output("addq $" + numArgs * 8 + ", %rsp", "remove args");
    }

    private int pushArgs(BPLParseTreeNode t) {
        if (t.isEmpty()) {
            return 0;
        }
        return pushArgList(t);
    }

    private int pushArgList(BPLParseTreeNode t) {
        int rest = 0;
        if (!(t.getChild(1).isEmpty())) {
            rest = this.pushArgList(t.getChild(1));
        }
        BPLParseTreeNode exp = t.getChild(0);
        this.generateExpression(exp);
        if (exp.isExpType("INT")) {
            this.output("push %rax", "int argument");
        }
        else if (exp.isExpType("STRING")) {
            this.output("push %rax", "string argument");
        }
        else if (exp.getExpType().contains("ARR")) { // array pointer
            while(!exp.isNodeType("<id>")){
                exp = exp.getChild(0);
            }
            if (((IdNode)exp).getDepth() == 1) {
                this.output("movq 0(%rax), %rax");
            }
            this.output("push %rax", "array argument");
        }
        else {
            this.output("push %rax", "pointer argument");
        }
        return rest + 1;
    }

    private void generateIf(BPLParseTreeNode t) {
        this.generateExpression(t.getChild(0));
        this.output("cmpl $0, %eax", "if statement");
        String label = this.getLabel();
        this.output("je " + label);
        this.generateStmt(t.getChild(1));
        this.output(label + ":");
        if (t.numChildren() > 2) {
            this.generateStmt(t.getChild(2));
        }
    }

    private void generateWhile(BPLParseTreeNode t){
        String label1 = this.getLabel();
        String label2 = this.getLabel();
        this.output(label1 + ":");
        this.generateExpression(t.getChild(0));
        this.output("cmpl $0, %eax", "while statement");
        this.output("je " + label2);
        this.generateStmt(t.getChild(1));
        this.output("jmp " + label1);
        this.output(label2 + ":");
    }

    private void generateReturn(BPLParseTreeNode t) {
        this.output("movq %rbx, %rsp", "return statement");
        if (t.numChildren() > 0) {
            this.generateExpression(t.getChild(0));
        }
        this.output("ret");
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
            this.output("movq %rax, %rsi", "write int");
            this.output("movq $.WriteIntString, %rdi");
        }
        else if (exp.isExpType("STRING")) {
            this.output("movq %rax, %rsi", "write string");
            this.output("movq $.WriteStrString, %rdi");
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

    private String getLabel() {
        int num = this.labelNumber++;
        return ".Label" + num;
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
