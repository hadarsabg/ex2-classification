import java.util.*;

/**
 * Created by hadar on 07/01/2019.
 * this class implements the naive base algorithm to classify a given test set
 */
public class NaiveBase {

    private List<String> features;
    private List<List<String>> trainDataVectors;
    private List<List<String>> testDataVectors;
    private int numOfFeatures;
    private int l0Count;
    private int l1count;
    private double pL1;
    private double pL0;
    private Map<String, List<String>> featuresOptionalValues;
    private List<Map<String, Double>> l0Probabilities;
    private List<Map<String, Double>> l1Probabilities;
    private List<String> labels;

    /**
     * constructor
     *
     * @param features         names of attributes
     * @param trainDataVectors classified train data set
     * @param testDataVectors  classified test data set
     */
    public NaiveBase(List<String> features, List<List<String>> trainDataVectors, List<List<String>> testDataVectors,List<String> labels) {
        this.features = features;
        this.testDataVectors = testDataVectors;
        this.trainDataVectors = trainDataVectors;
        this.numOfFeatures = features.size() - 1;
        this.featuresOptionalValues = new LinkedHashMap<String, List<String>>();
        this.l0Probabilities = new ArrayList<Map<String, Double>>();
        this.l1Probabilities = new ArrayList<Map<String, Double>>();
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
     * calculates all options of Conditional probability and updates 2 lists:
     * yes probability (if example was labeled yes) and no probability (if example was labeled yes)
     */
    public void calculateAllProbabilities() {
        //set the maps for al features values options with count 0
        for (String key : featuresOptionalValues.keySet()) {
            Map<String, Double> mapL0 = new HashMap<String, Double>();
            for (int j = 0; j < featuresOptionalValues.get(key).size(); j++) {
                mapL0.put(featuresOptionalValues.get(key).get(j), 0.0);
            }
            this.l0Probabilities.add(mapL0);
        }
        for (String key : featuresOptionalValues.keySet()) {
            Map<String, Double> mapL1 = new HashMap<String, Double>();
            for (int j = 0; j < featuresOptionalValues.get(key).size(); j++) {
                mapL1.put(featuresOptionalValues.get(key).get(j), 0.0);
            }
            this.l1Probabilities.add(mapL1);
        }
        //count all occurrences of each value in a "yes" labeled train vector
        for (List<String> trainVector : this.trainDataVectors) {
            if (trainVector.get(numOfFeatures).equals(labels.get(0))) {
                this.l0Count++;
                for (int i = 0; i < this.numOfFeatures; i++) {
                    Double currCount = l0Probabilities.get(i).get(trainVector.get(i));
                    l0Probabilities.get(i).put(trainVector.get(i), currCount + 1);
                }
                //count all occurrences of each value in a "no" labeled train vector
            } else {
                this.l1count++;
                for (int i = 0; i < this.numOfFeatures; i++) {
                    Double currCount = l1Probabilities.get(i).get(trainVector.get(i));
                    l1Probabilities.get(i).put(trainVector.get(i), currCount + 1);
                }
            }
        }

        this.pL0 = (double) l0Count / (l0Count + l1count);
        this.pL1 = (double) l1count / (l0Count + l1count);

        //replace each count with probability
        for (Map<String, Double> featureMap : l0Probabilities) {
            for (String value : featureMap.keySet()) {
                double p = (featureMap.get(value) + 1) / (l0Count + featureMap.keySet().size());
                featureMap.put(value, p);
            }
        }
        for (Map<String, Double> featureMap : l1Probabilities) {
            for (String value : featureMap.keySet()) {
                double p = (featureMap.get(value) + 1) / (l1count + featureMap.keySet().size());
                featureMap.put(value, p);
            }
        }
    }

    /**
     * apply the naive base algorithm on the test set
     *
     * @return list of predictions for each test example
     */
    public List<String> classifyAllTestData() {
        List<String> classification = new ArrayList<>(testDataVectors.size());
        double l0, l1;
        for (int i = 0; i < this.testDataVectors.size(); i++) {
            l0 = pL0;
            l1 = pL1;
            for (int j = 0; j < this.numOfFeatures; j++) {
                String value = testDataVectors.get(i).get(j);
                double tempY = l0Probabilities.get(j).get(value);
                if (tempY > 0.0) {
                    l0 *= tempY;
                }
                double tempNo = l1Probabilities.get(j).get(value);
                if (tempNo > 0.0) {
                    l1 *= tempNo;
                }
            }
            //save the maximal probability
            if (l0 == l1) {
                if (l0Count >= l1count) {
                    classification.add(labels.get(0));
                } else {
                    classification.add(labels.get(1));
                }
            } else if (l0 > l1) {
                classification.add(labels.get(0));
            } else {
                classification.add(labels.get(1));
            }
        }
        return classification;
    }

}



