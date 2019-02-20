import java.util.ArrayList;
import java.util.List;

/**
 * Created by hadar on 07/01/2019.
 * this class implements the knn  with k=5 algorithm to classify a given test set
 */


public class KNN {
    private int k = 5;
    private List<String> features;
    private List<List<String>> trainDataVectors;
    private List<List<String>> testDataVectors;
    private int nunOfFeatures;
    private List<String> labels;
    /**
     * constructor
     * @param features names of attributes
     * @param trainDataVectors classified train data set
     * @param testDataVectors classified test data set
     */
    public KNN(List<String> features, List<List<String>> trainDataVectors, List<List<String>> testDataVectors, List<String> labels) {
        this.features = features;
        this.testDataVectors = testDataVectors;
        this.trainDataVectors = trainDataVectors;
        this.nunOfFeatures = features.size() - 1;
        this.labels=labels;
    }
    /**
     * apply the KNN algorithm on the test set
     * @return list of predictions for each test example
     */
    public List<String> classifyAllTestData() {
        List<String> classification = new ArrayList<>(testDataVectors.size());
        for (List<String> testVector : testDataVectors) {
            int l0Counter = 0, l1Counter = 0;
            ArrayList<Integer> topKNeighbors = getTopKNeighbors(testVector);
            for (int n : topKNeighbors) {
                if (trainDataVectors.get(n).get(nunOfFeatures).equals(labels.get(0))) {
                    l0Counter++;
                } else {
                    l1Counter++;
                }
            }
            if (l1Counter > l0Counter) {
                classification.add(labels.get(1));
            } else {
                classification.add(labels.get(0));
            }
        }
        return classification;
    }

    /**
     * get a list of top k nearest neighbors indexes in original train set list
     * @param testVector test data
     * @return list of top k nearest neighbors indexes
     */
    public ArrayList<Integer> getTopKNeighbors(List<String> testVector) {
        int dist;
        ArrayList<Integer> topKIndices = new ArrayList<Integer>(this.k);
        int numOfExamples = this.trainDataVectors.size();
        int numOfFeatures = this.features.size() - 1;
        List<Integer> distances = new ArrayList<Integer>(numOfExamples);
        for (int exampleIndex = 0; exampleIndex < numOfExamples; exampleIndex++) {
            dist = 0;
            for (int i = 0; i < numOfFeatures; i++) {
                if (!this.trainDataVectors.get(exampleIndex).get(i).equals(testVector.get(i))) {
                    dist++;
                }
            }
            distances.add(dist);
        }
        int currentIndex = 0;
        for (int j = 0; j < this.k; j++) {
            topKIndices.add(currentIndex);
            for (int index = 0; index < numOfExamples; index++) {
                if (distances.get(index) < distances.get(topKIndices.get(j)) && !topKIndices.contains(index)) {
                    topKIndices.set(j, index);
                }
            }
            currentIndex = topKIndices.get(j) + 1;
        }

        return topKIndices;
    }
}
