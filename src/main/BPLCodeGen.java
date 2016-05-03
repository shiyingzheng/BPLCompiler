import java.util.HashMap;


public class BPLCodeGen {
    private BPLParseTreeNode parseTree;
    private HashMap<String, String> stringTable;
    private int stringNumber;
    private int position;

    public BPLCodeGen(String fileName) throws BPLTypeCheckerException {
        BPLTypeChecker typeChecker = new BPLTypeChecker(fileName, false, false);
        this.stringTable = new HashMap<String, String>();
        this.stringNumber = 0;
        this.position = 0;
        this.parseTree = typeChecker.getTree();
        this.generateCode();
    }

    private void generateCode() {
        this.generateDataSection();
        this.assignDepth(this.parseTree, 0);
        this.generateTextSection();
    }

    private void generateDataSection() {
        this.output(".section   .rodata");
        this.output(".WriteIntString: .string \"%d\"");
        this.output(".WriteStrString: .string \"%s\"");
        this.output(".WritelnString: .string \"\\n\"");
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

    private void assignDepth(BPLParseTreeNode t, int depth) {
        if (t.getNodeType().contains("VARIABLE_DECLARATION")) {
            ((IdNode)t.getChild(1)).setDepth(depth);
            ((IdNode)t.getChild(1)).setPosition(position);
            //System.out.println(((IdNode)t.getChild(1)).getId() + " " + depth + " " + this.position);
            this.position++;
        }
        else {
            if (t.isNodeType("PARAM_LIST")) {
                this.position = 0;
                this.assignDepthParamList(t);
                return;
            }
            else if (t.isNodeType("COMPOUND_STATEMENT")) {
                if (depth == 0) {
                    depth = 1;
                }
                depth++;
                this.position = 0;
            }
            for (int i = 0; i < t.numChildren(); i++){
                this.assignDepth(t.getChild(i), depth);
            }
        }
    }

    private void assignDepthParamList(BPLParseTreeNode t) {
        BPLParseTreeNode param = t.getChild(0);
        BPLParseTreeNode rest = t.getChild(1);
        ((IdNode)param.getChild(1)).setDepth(1);
        ((IdNode)param.getChild(1)).setPosition(position);
        //System.out.println(((IdNode)param.getChild(1)).getId() + " " + 1 + " " + this.position);
        position++;
        if (!rest.isEmpty()) {
            this.assignDepthParamList(rest);
        }
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

    }

    private void generateExpression(BPLParseTreeNode t) {
        // TODO
        if (t.isExpType("INT")){
            this.output("movl $12, %eax", "placeholder for expression eval");
        }
        else {
            // ???
        }
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
        if (exp.isExpType("INT")) {
            this.generateExpression(exp);
            this.output();
            this.output("movl %eax, %esi", "write int");
            this.output("movq $.WriteIntString, %rdi");
        }
        else if (exp.isExpType("STRING")) {
            while (!exp.isNodeType("<string>")) {
                exp = exp.getChild(0);
            }
            String n = this.stringTable.get(((StringValueNode)exp).getValue());
            this.output();
            this.output("movq " + n + ", %rdi", "write string");
        }
        else {
            //TODO, arr, ptr
        }
        this.output("movl $0, %eax");
        this.output("call printf");
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
