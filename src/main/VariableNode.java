public class VariableNode extends BPLParseTreeNode {
    private String id;

    public VariableNode(String id, int lineNum){
        super(null, 0, lineNum);
        this.id = id;
    }

    @Override
    public String toString(){
        return "Line " + this.getLineNumber()
            + ": VariableNode id = " + this.id;
    }
}
