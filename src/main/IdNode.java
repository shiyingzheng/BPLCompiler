public class IdNode extends BPLParseTreeNode {
    private String id;
    private int depth;
    private int position;

    public IdNode(String id, int lineNum){
        super("<id>", 0, lineNum);
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    public int getDepth() {
        return this.depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public int getPosition() {
        return this.position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    @Override
    public String toString(){
        return "Line " + this.getLineNumber()
            + ": Identifier id = " + this.id;
    }
}
