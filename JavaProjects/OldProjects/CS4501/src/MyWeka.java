

import java.io.StringReader;
import java.util.Random;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.SMO;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;

public class MyWeka {

	public static Evaluation classify(String arrfData, int clf) throws Exception {
		StringReader strReader = new StringReader(arrfData);
		Instances instances = new Instances(strReader);
		strReader.close();
		instances.setClassIndex(instances.numAttributes() - 1);
		
		Classifier classifier;
		if(clf==1)
			classifier = new J48(); // Decision Tree classifier
		else if(clf==2)			
			classifier = new RandomForest();
		else if(clf == 3)
			classifier = new SMO();  //This is a SVM classifier
		else 
			return null;
		
		classifier.buildClassifier(instances); // build classifier
		
		Evaluation eval = new Evaluation(instances);
		eval.crossValidateModel(classifier, instances, 10, new Random(1), null);
		
		return eval;
	}

}
