public class IdNode extends BPLParseTreeNode {
    private String id;

    public IdNode(String id, int lineNum){
        super("<id>", 0, lineNum);
        this.id = id;
    }

    @Override
    public String toString(){
        return "Line " + this.getLineNumber()
            + ": Identifier id = " + this.id;
    }
}
