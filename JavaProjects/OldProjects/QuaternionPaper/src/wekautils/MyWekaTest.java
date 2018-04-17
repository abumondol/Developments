package wekautils;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.Random;
import quaternionpaper.MyConstants;
import weka.classifiers.Classifier;

import weka.classifiers.Evaluation;
import weka.classifiers.functions.SMO;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;

public class MyWekaTest {

    public Instances instancesTrain = null, instancesTest, instances;
    String fileName = null;
    BufferedReader reader;
    StringReader strReader;
    //public RandomForest forest;
    public Classifier classifier;
    public Evaluation eval;
    public int factor = 2;

    public void setData(String arrfData) throws Exception {
        if (instances != null) {
            instances.delete();
        }

        strReader = new StringReader(arrfData);
        instances = new Instances(strReader);
        //UtilFunctions.close(strReader);
        strReader.close();
    }

    public void setData(String arrfFileTrain, String arrfFileTest) throws Exception {
        if (instancesTrain != null) {
            instancesTrain.delete();
        }
        strReader = new StringReader(arrfFileTrain);
        instancesTrain = new Instances(strReader);
        strReader.close();


        if (instancesTest != null) {
            instancesTest.delete();
        }
        strReader = new StringReader(arrfFileTest);
        instancesTest = new Instances(strReader);
        strReader.close();

    }

    public void test(String arrfFileTrain, String arrfFileTest) throws Exception {
        setData(arrfFileTrain, arrfFileTest);
        instancesTrain.setClassIndex(instancesTrain.numAttributes() - 1);
        instancesTest.setClassIndex(instancesTest.numAttributes() - 1);

        classifier = new RandomForest();
        //classifier = new J48();
        classifier.buildClassifier(instancesTrain);
        //classifier.setNumIterations(100);        
        
        eval = new Evaluation(instancesTrain);
        eval.evaluateModel(classifier, instancesTest);        
    }

    public void crossVal(String arrfData) throws Exception {
        setData(arrfData);
        instances.setClassIndex(instances.numAttributes() - 1);

        //classifier = new J48();
        classifier = new RandomForest();
        //forest.setNumIterations(100);
        //forest.setNumFeatures(instances.numAttributes() - 1);
        classifier.buildClassifier(instances); // build classifier
        
        eval = new Evaluation(instances);
        eval.evaluateModel(classifier, instances, 10, new Random(1));   
    }
    
    public double[][] testWithProbDist(String arrfFileTrain, String arrfFileTest, int classifier_type) throws Exception {
        setData(arrfFileTrain, arrfFileTest);
        instancesTrain.setClassIndex(instancesTrain.numAttributes() - 1);
        instancesTest.setClassIndex(instancesTest.numAttributes() - 1);

        if(classifier_type == MyConstants.RANDOM_FOREST)
            classifier = new RandomForest();
        else
            classifier = new RandomForest();
            //classifier = new Logistic();
                
        classifier.buildClassifier(instancesTrain);
        
        int testSize = instancesTest.numInstances();
        double[][] res = new double[testSize][];
        for(int i=0;i<testSize;i++){
            res[i] = classifier.distributionForInstance(instancesTest.instance(i));
        }
        
        return res;        
    }
    
}

