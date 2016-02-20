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

    public int getLineNumber(){
        return this.lineNum;
    }

    public BPLParseTreeNode getChild(int i){
        return this.children[i];
    }

    public void setChild(int i, BPLParseTreeNode child){
        this.children[i] = child;
    }

    public String toString(){
        String s = "Line " + this.getLineNumber() + ": " + nodeType + "\n";
        for (int i = 0; i < children.length; i++){
            s += "\t" + children[i].toString();
        }
        return s;
    }
}
