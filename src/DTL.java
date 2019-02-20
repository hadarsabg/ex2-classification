import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hadar on 11/01/2019.
 * this class builds a decision tree and classify  given test set using ID3 algorithm
 */
public class DTL {
    private List<String> features;
    private List<List<String>> trainDataVectors;
    private List<List<String>> testDataVectors;
    private int numOfFeatures;
    private Map<String, List<String>> featuresOptionalValues;
    private List<Map<String, Double>> yesProbabilities;
    private List<Map<String, Double>> noProbabilities;
    private List<String> labels;

    /**
     * constructor
     *
     * @param features         names of attributes
     * @param trainDataVectors classified train data set
     * @param testDataVectors  classified test data set
     */
    public DTL(List<String> features, List<List<String>> trainDataVectors, List<List<String>> testDataVectors,List<String> labels) {
        this.features = features;
        this.testDataVectors = testDataVectors;
        this.trainDataVectors = trainDataVectors;
        this.numOfFeatures = features.size() - 1;
        this.featuresOptionalValues = new LinkedHashMap<String, List<String>>();
        this.yesProbabilities = new ArrayList<Map<String, Double>>();
        this.noProbabilities = new ArrayList<Map<String, Double>>();
        this.setFeaturesOptionalValues();
        this.labels=labels;
    }

    /**
     * scan the data set and save all optional values for each feature
     */
    private void setFeaturesOptionalValues() {
        for (int i = 0; i < this.numOfFeatures; i++) {
            //insert to the map new feature and an empty list
            String currFeature = this.features.get(i);
            this.featuresOptionalValues.put(currFeature, new ArrayList<String>());
            for (List<String> trainVector : this.trainDataVectors) {
                String optVal = trainVector.get(i);
                if (!this.featuresOptionalValues.get(currFeature).contains(optVal)) {
                    this.featuresOptionalValues.get(currFeature).add(optVal);
                }
            }
        }
    }

    /**
     * calculate mode(examples) from algorithm
     * count how many examples are classified as yes and how many as no
     *
     * @param examples train set
     * @return max(yescount, nocount)
     */
    public String mostFrequentClass(List<List<String>> examples) {
        int l0Count = 0;
        int l1Count = 0;
        for (List<String> vector : examples) {
            if (vector.get(numOfFeatures).equals(labels.get(0))) {
                l0Count++;
            } else {
                l1Count++;
            }
        }
        String currentMaxClassification;
        if (l0Count >= l1Count) {
            currentMaxClassification = labels.get(0);
        } else {
            currentMaxClassification = labels.get(1);
        }
        return currentMaxClassification;

    }

    /**
     * build the DTL recursivly using the ID3 algorithm
     *
     * @param examples   list of train examples
     * @param attributes list of attributes names
     * @param def        default classification( previous max classification)
     * @return dtl
     */
    public Tree buildTree(List<List<String>> examples, List<String> attributes, Node def) {
        Tree tree = new Tree();
        int l0Count = 0;
        int l1Count = 0;
        for (List<String> vector : examples) {
            if (vector.get(numOfFeatures).equals(labels.get(0))) {
                l0Count++;
            } else {
                l1Count++;
            }
        }
        String currentMaxClassification;
        if (l0Count >= l1Count) {
            currentMaxClassification = labels.get(0);
        } else {
            currentMaxClassification = labels.get(1);
        }
        //if no examples are left return default classification
        if (examples.isEmpty()) {
            tree.setRoot(def);
            return tree;
            //if all classification are the same
        } else if (l0Count == 0) {
            tree.setRoot(new Node(labels.get(1)));
            return tree;
        } else if (l1Count == 0) {
            tree.setRoot(new Node(labels.get(0)));
            return tree;
            // if no attributes are left
        } else if (attributes.isEmpty()) {
            tree.setRoot(new Node(currentMaxClassification));
            return tree;
        } else {
            String best = chooseAttribute(attributes, examples);
            tree.setRoot(new Node(best));
            for (int i = 0; i < featuresOptionalValues.get(best).size(); i++) {
                String currVal = featuresOptionalValues.get(best).get(i);
                List<List<String>> examplesI = new ArrayList<List<String>>();
                for (List<String> vector : examples) {
                    if (vector.get(features.indexOf(best)).equals(currVal)) {
                        examplesI.add(vector);
                    }
                }
                List<String> modifyAttributes = new ArrayList<String>();
                modifyAttributes.addAll(attributes);
                if (modifyAttributes.contains(best)) {
                    modifyAttributes.remove(best);
                }
                //recursive call to build sub tree from current node
                Tree subTree = buildTree(examplesI, modifyAttributes, new Node(currentMaxClassification));
                //connect the sub tree to the main tree
                tree.getRoot().addChild(featuresOptionalValues.get(best).get(i), subTree.getRoot());
            }
            return tree;
        }
    }

    /**
     * return the attribute with max gain
     *
     * @param attributes feature names
     * @param examples   train set
     * @return best attribute
     */
    private String chooseAttribute(List<String> attributes, List<List<String>> examples) {
        if (attributes.size() == 1) {
            return attributes.get(0);
        }
        double maxGain = -6.0;
        String best = null;
        int countL0 = 0;
        int countL1 = 0;
        for (List<String> vector : examples) {
            if (vector.get(numOfFeatures).equals(labels.get(0))) {
                countL0++;
            } else {
                countL1++;
            }
        }
        double entropyTotal = entropy(countL0, countL1, examples.size());
        for (String attribute : attributes) {
            double gain = entropyTotal;
            for (String value : this.featuresOptionalValues.get(attribute)) {
                int countRelevantExamples = 0;
                countL0 = 0;
                countL1 = 0;
                for (List<String> vector : examples) {
                    if (vector.get(features.indexOf(attribute)).equals(value)) {
                        countRelevantExamples++;
                        if (vector.get(numOfFeatures).equals(labels.get(0))) {
                            countL0++;
                        } else if (vector.get(numOfFeatures).equals(labels.get(1))) {
                            countL1++;
                        }
                    }
                }
                double entropy = entropy(countL0, countL1, countRelevantExamples);
                gain -= ((double) countRelevantExamples / examples.size()) * entropy;
            }
            if (gain > maxGain) {
                maxGain = gain;
                best = attribute;
            }

        }
        return best;
    }

    /**
     * calculate entropy for node
     *
     * @param countL0 number of l0 labeled examples
     * @param countL1  number of l1 labeled examples
     * @param total    total count of examples
     * @return entropy
     */
    private double entropy(int countL0, int countL1, int total) {
        if (countL1 == 0 || countL0 == 0) {
            return 0.0;
        }
        double entropy = (-1) * ((double) countL0 / total) * Math.log((double) countL0 / total)
                - ((double) countL1 / total) * Math.log((double) countL1 / total);
        return entropy;
    }

    /**
     * apply the ID3 algorithm on the test set
     *
     * @param dtl DTL build from data set
     * @return list of predictions for each test example
     */
    public List<String> classifyAllTestData(Tree dtl) {
        List<String> classification = new ArrayList<>(testDataVectors.size());
        for (List<String> testVector : testDataVectors) {
            Node currNode = dtl.getRoot();
            while (!currNode.isLeaf()) {
                String attVal = testVector.get(features.indexOf(currNode.getData()));
                currNode = currNode.getChildren().get(attVal);
            }

            classification.add(currNode.getData());
        }
        return classification;
    }
}
