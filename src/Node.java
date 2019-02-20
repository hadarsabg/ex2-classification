import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hadar on 11/01/2019.
 * implementation of tree node with string data
 */
public class Node {

    private Map<String, Node> children = new HashMap<>();
    private Node parent = null;
    private String data = null;

    /**
     * constructor
     *
     * @param data string
     */
    public Node(String data) {
        this.data = data;
    }

    /**
     * returnt the map of the children node
     *
     * @return map of children
     */
    public Map<String, Node> getChildren() {
        return children;
    }

    /**
     * set the node parent
     *
     * @param newParent parent node
     */
    public void setParent(Node newParent) {
        this.parent = newParent;
    }

    /**
     * add child to the node children map
     *
     * @param branch the value on branch that connects the node to the child
     * @param child  child node
     */
    public void addChild(String branch, Node child) {
        child.setParent(this);
        this.children.put(branch, child);
    }

    /**
     * return node data
     *
     * @return data string
     */
    public String getData() {
        return this.data;
    }

    /**
     * check if node has children
     *
     * @return true if leaf, else -false
     */
    public boolean isLeaf() {
        return this.children.isEmpty();
    }

    /**
     * check if node's parent is null
     *
     * @return true if root, else -false
     */
    public boolean isRoot() {
        return this.parent==null;
    }

}

