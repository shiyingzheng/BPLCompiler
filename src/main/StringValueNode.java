public class StringValueNode extends BPLParseTreeNode {
    private String value;

    public StringValueNode(String value, int lineNum){
        super("<string>", 0, lineNum);
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

    @Override
    public String toString(){
        return "Line " + this.getLineNumber()
            + ": StringValueNode id = " + this.value;
    }
}
