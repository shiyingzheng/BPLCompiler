public class BPLParseTreeNode {
    private int lineNum;
    private BPLParseTreeNode[] children;
    private String nodeType;
    private BPLParseTreeNode declaration;

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

    public String getName() {
        if (this.isType("<id>")){
            return ((IdNode)this).getId();
        }
        for (int i = 0; i < this.children.length; i++) {
            if (this.children[i].isType("<id>")) {
                return ((IdNode)this.children[i]).getId();
            }
        }
        return null;
    }

    public BPLParseTreeNode getDeclaration(){
        return this.declaration;
    }

    public void setDeclaration(BPLParseTreeNode node) {
        this.declaration = node;
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
