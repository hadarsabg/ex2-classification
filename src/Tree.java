import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Created by hadar on 11/01/2019.
 * implements a tree object.
 */
public class Tree {

    private PrintWriter writer;
    private Node root;
    private List<String> treeToPrint;

    /**
     * constructor
     */
    public Tree() {
        this.treeToPrint = new ArrayList<String>();
    }

    /**
     * set new node as root
     *
     * @param root root node
     */
    public void setRoot(Node root) {
        this.root = root;
    }

    /**
     * return the tree root
     *
     * @return the tree root
     */
    public Node getRoot() {
        return this.root;
    }

    /**
     * creates outPut_tree.txt file and writes the tree data
     */
    public void printTreeToFile() {
        try {
            this.writer = new PrintWriter("output_tree.txt");
            recursiveWrite(root, 0);
            if (treeToPrint.get(treeToPrint.size()-1).equals("\n")){
                treeToPrint.remove(treeToPrint.size()-1);
            }
            for (int i = 0; i < treeToPrint.size(); i++) {
                writer.print(treeToPrint.get(i));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (this.writer != null) {
                writer.close();
            }
        }
    }

    /**
     * print recursively all the tree nodes
     *
     * @param node  node that is the current sub tree root
     * @param depth depth of node for the tabs print
     */
    public void recursiveWrite(Node node, int depth) {
        if (node.isLeaf()) {
            treeToPrint.add(": "+node.getData()+"\n");
        } else {
            SortedSet<String> sortedChildren = new TreeSet<>(node.getChildren().keySet());
            for (String branch : sortedChildren) {
                for (int i = 0; i < depth; i++) {
                    treeToPrint.add("\t");
                }
                if (!node.isRoot()) {
                    treeToPrint.add("|");
                }
                treeToPrint.add(node.getData()+ " = " + branch);
                Node childNode = node.getChildren().get(branch);
                if (!childNode.isLeaf()) {
                    treeToPrint.add("\n");
                }
                recursiveWrite(childNode, depth + 1);
            }
        }
    }


//    public void recursiveWrite(Node node, int depth) {
//        if (node.isLeaf()) {
//            writer.println(": " + node.getData());
//        } else {
//            SortedSet<String> sortedChildren = new TreeSet<>(node.getChildren().keySet());
//            for (String branch : sortedChildren) {
//                for (int i = 0; i < depth; i++) {
//                    writer.print("\t");
//                }
//                if (!node.isRoot()) {
//                    writer.print("|");
//                }
//                writer.print(node.getData() + " = " + branch);
//                Node childNode = node.getChildren().get(branch);
//                 if (!childNode.isLeaf()) {
//                   writer.print("\n");
//                }
//                recursiveWrite(childNode, depth + 1);
//            }
//        }
//    }

}
