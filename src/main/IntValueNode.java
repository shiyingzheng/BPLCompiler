public class IntValueNode extends BPLParseTreeNode {
    private int value;

    public IntValueNode(String value, int lineNum){
        super("<num>", 0, lineNum);
        this.value = Integer.parseInt(value);
    }

    @Override
    public String toString(){
        return "Line " + this.getLineNumber()
            + ": IntValueNode id = " + this.value;
    }
}
