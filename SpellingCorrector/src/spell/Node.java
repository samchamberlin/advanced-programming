package spell;

public class Node implements INode {

    private Node[] children;
    private int value;

    public Node(){
        children = new Node[26];
        value = 0;
    }

    public Node[] getChildren(){
        return children;
    }

    @Override
    public int getValue() {
        return value;
    }

    public void incrementValue(){
        value++;
    }
}
