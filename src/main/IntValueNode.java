public class IntValueNode extends BPLParseTreeNode {
    private int value;

    public IntValueNode(int value, int lineNum){
        super(null, 0, lineNum);
        this.value = value;
    }

    @Override
    public String toString(){
        return "Line " + this.getLineNumber()
            + ": VariableNode id = " + this.value;
    }
}
