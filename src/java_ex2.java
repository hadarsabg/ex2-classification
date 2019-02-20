import javax.print.DocFlavor;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.SimpleTimeZone;

/**
 * Created by hadar on 07/01/2019.
 */
public class java_ex2 {
    public static void main(String[] args) {
        //create parser object and read the file
        FileParser fileParser = new FileParser("train.txt");
        List<String> features = fileParser.getFeatursNames();
        int numOfFeatures = features.size();
        List<List<String>> trainDataVectors = fileParser.getExamplesVectors();
        fileParser = new FileParser("test.txt");
        List<String> testFeaturs = fileParser.getFeatursNames();
        List<List<String>> testDataVectors = fileParser.getExamplesVectors();
        //check what are the 2 possible classifications
        List<String> labels=new ArrayList<>(2);
        labels.add(trainDataVectors.get(0).get(numOfFeatures-1));
        for (int i = 0; i <trainDataVectors.size() ; i++) {
            String currLabel=trainDataVectors.get(i).get(numOfFeatures-1);
            if(!currLabel.equals(labels.get(0))){
                labels.add(currLabel);
                break;
            }
        }
        //knn
        KNN knn = new KNN(features, trainDataVectors, testDataVectors,labels);
        List<String> classificationKNN = knn.classifyAllTestData();
        //naiveBase
        NaiveBase naiveBase = new NaiveBase(features, trainDataVectors, testDataVectors,labels);
        naiveBase.calculateAllProbabilities();
        List<String> classificationNaiveBase = naiveBase.classifyAllTestData();
        //DTL
        List<String> attributes = new ArrayList<String>();
        for (int i = 0; i < features.size() - 1; i++) {
            attributes.add(features.get(i));
        }
        DTL dtl = new DTL(features, trainDataVectors, testDataVectors,labels);
        Node def = new Node(dtl.mostFrequentClass(trainDataVectors));
        Tree tree = dtl.buildTree(trainDataVectors, attributes, def);
        tree.printTreeToFile();
        List<String> classificationDTL = dtl.classifyAllTestData(tree);
        //write result to file
        writeResult(classificationDTL, classificationKNN, classificationNaiveBase, testDataVectors);

    }

    public static String accuracy(List<List<String>> testDataVectors, List<String> classification) {
        int correctPredictions = 0;
        int lastIndex = testDataVectors.get(0).size() - 1;
        for (int i = 0; i < classification.size(); i++) {
            if (classification.get(i).equals(testDataVectors.get(i).get(lastIndex))) {
                correctPredictions++;
            }
        }
        return String.format("%.2f", (double) correctPredictions / testDataVectors.size());
    }

    public static void writeResult(List<String> DTL,
                                   List<String> KNN,
                                   List<String> naiveBase,
                                   List<List<String>> test) {
        PrintWriter writer = null;
        try {
            writer = new PrintWriter("output.txt");
            writer.println("Num" + "\t" + "DT" + "\t" + "KNN" + "\t" + "naiveBase");
            int examplesCount = DTL.size();
            for (int i = 0; i < examplesCount; i++) {
                writer.println((i + 1) + "\t" + DTL.get(i) + "\t" + KNN.get(i) + "\t" + naiveBase.get(i));
            }
            String accDTL = accuracy(test, DTL);
            String accKNN = accuracy(test, KNN);
            String accNaive = accuracy(test, naiveBase);
            writer.println("\t" + accDTL + "\t" + accKNN + "\t" + accNaive);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }
}

