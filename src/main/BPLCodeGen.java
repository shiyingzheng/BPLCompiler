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
        this.generateTextSection();
    }

    private void generateDataSection() {
        this.output(".section   .rodata");
        this.output(".WriteIntString: .string \"%d\"");
        this.output(".WriteStrString: .string \"%s\"");
        this.output(".WritelnString: .string \"\\n\"");
        this.generateDataStrings(this.parseTree);
    }

    private void generateTextSection() {
        this.output(".text");
        this.output(".globl main");
        this.output();
        this.generateDeclarations(this.parseTree);
    }

    private void generateDataStrings(BPLParseTreeNode t) {
        if (t.isType("<string>")) {
            this.output(".String" + this.stringNumber + ": .string \""
                + ((StringValueNode)t).getValue() + "\"");
            this.stringNumber++;
        }
        else {
            for (int i = 0; i < t.numChildren(); i++){
                this.generateDataStrings(t.getChild(i));
            }
        }
    }

    private void generateDeclarations(BPLParseTreeNode t) {
        if (t.isType("PROGRAM")){
            this.generateDeclarations(t.getChild(0));
        }
        else if (t.isType("DECLARATION_LIST")) {
            for (int i = 0; i < t.numChildren(); i++) {
                this.generateDeclarations(t.getChild(i));
            }
        }
        else if (t.isType("FUNCTION")) {
            this.generateFunction(t);
        }
        else {
            this.generateGlobalVar(t);
        }
    }

    private void generateGlobalVar(BPLParseTreeNode t) {
        if (t.isType("ARRAY_VARIABLE_DECLARATION")) {
            this.output(".comm " + ((IdNode)t.getChild(1)).getId() + ", " +
                8*((IntValueNode)t.getChild(2)).getValue() + ", 32");
        }
        else {
            this.output(".comm " + ((IdNode)t.getChild(1)).getId() + ", 8, 32");
        }
        this.output();
    }

    private void generateFunction(BPLParseTreeNode t) {
        this.output(((IdNode)t.getChild(1)).getId() + ": ");
        this.output();
    }

    private void generateStatement() {

    }

    private void generateExpression() {

    }

    private void output() {
        // for now just print to standard out
        System.out.println();
    }

    private void output(String str) {
        // for now just print to standard out
        System.out.println(str);
    }

    public static void main(String args[]) throws BPLTypeCheckerException{
        String fileName = args[0];
        BPLCodeGen gen = new BPLCodeGen(fileName);
    }
}
