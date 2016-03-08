public class BPLParseTreeNode {
    private int lineNum;
    private BPLParseTreeNode[] children;
    private String nodeType;

    public BPLParseTreeNode(String nodeType, int numChildren, int lineNum){
        this.nodeType = nodeType;
        if (numChildren > 0){
            this.children = new BPLParseTreeNode[numChildren];
        }
        this.lineNum = lineNum;
    }

    public String getNodeType(){
        return this.nodeType;
    }

    public boolean isType(String type) {
        return this.nodeType.equals(type);
    }

    public int getLineNumber(){
        return this.lineNum;
    }

    public BPLParseTreeNode getChild(int i){
        return this.children[i];
    }

    public void setChild(int i, BPLParseTreeNode child){
        this.children[i] = child;
    }

    public int numChildren(){
        if (this.children == null){
            return 0;
        }
        return this.children.length;
    }

    public boolean isEmpty() {
        return this.nodeType.equals("<empty>");
    }

    public String toString(){
        return "Line " + this.getLineNumber() + ": " + nodeType;
    }
}
