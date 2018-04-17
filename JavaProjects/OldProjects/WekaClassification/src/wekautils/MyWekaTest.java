package wekautils;

import java.io.BufferedReader;
import java.io.StringReader;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.trees.J48;
import weka.core.Instances;

public class MyWekaTest {

	public Instances instancesTrain = null, instancesTest;
	String fileName = null;
	BufferedReader reader;
	StringReader strReader;
	public Classifier classifier;
	public Evaluation eval;
	public int factor = 2;

	public void setData(String arrfFileTrain, String arrfFileTest) throws Exception {
		if (instancesTrain != null)
			instancesTrain.delete();
		strReader = new StringReader(arrfFileTrain);
		instancesTrain = new Instances(strReader);
                strReader.close();
		
		
		if (instancesTest != null)
			instancesTest.delete();
		strReader = new StringReader(arrfFileTest);
		instancesTest = new Instances(strReader);		
		strReader.close();
		
	}
	

	public void test(String arrfFileTrain, String arrfFileTest) throws Exception {
		setData(arrfFileTrain, arrfFileTest);
		instancesTrain.setClassIndex(instancesTrain.numAttributes() - 1);
		instancesTest.setClassIndex(instancesTest.numAttributes() - 1);

		classifier = new J48();
                //classifier = new RandomForest();
                //classifier = new SMO();
		classifier.buildClassifier(instancesTrain);
		eval = new Evaluation(instancesTrain);		
		eval.evaluateModel(classifier, instancesTest);
	}

}
