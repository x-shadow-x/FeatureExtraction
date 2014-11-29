package english_feature_extraction;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Set;

public class Program {
	public static void main(String[]args) throws IOException{
		
		//FeatureValue[] featureValues = useDF();String filePathString = "E:\\java_Eclipse\\FeatureExtraction\\DF结果.arff";
		//FeatureValue[] featureValues = useCHI();String filePathString = "E:\\java_Eclipse\\FeatureExtraction\\CHI结果.arff";
		//FeatureValue[] featureValues = useCHI2();String filePathString = "E:\\java_Eclipse\\FeatureExtraction\\CHI2结果.arff";
		//FeatureValue[] featureValues = useMI();String filePathString = "E:\\java_Eclipse\\FeatureExtraction\\MI结果.arff";
	    FeatureValue[] featureValues = useIG();String filePathString = "E:\\java_Eclipse\\FeatureExtraction\\IG结果.arff";
		
		for(int i = 0; i < featureValues.length; i++){
			System.out.println(featureValues[i].getfeatureValueHashMap());
		}
		
		writeToTxt(featureValues,filePathString);
	}
	
	public static FeatureValue[] useDF() throws IOException{
		DF df = new DF();
		FeatureValue[] featureValues = df.getFeatureValue();
		return featureValues;
	}
	
	public static FeatureValue[] useCHI() throws IOException{
		CHI chi = new CHI();
		FeatureValue[] featureValues = chi.getFeatureValue();
		return featureValues;
	}
	
	public static FeatureValue[] useCHI2() throws IOException{
		CHI2 chi2 = new CHI2();
		FeatureValue[] featureValues = chi2.getFeatureValue();
		return featureValues;
	}
	
	public static FeatureValue[] useMI() throws IOException{
		MI mi = new MI();
		FeatureValue[] featureValues = mi.getFeatureValue();
		return featureValues;
	}
	
	public static FeatureValue[] useIG() throws IOException{
		IG ig = new IG();
		FeatureValue[] featureValues = ig.getFeatureValue();
		return featureValues;
	}
	
	
	
	
	public static void writeToTxt(FeatureValue[] featureValues,String filePath) throws IOException{
		Set<String> set = featureValues[0].getfeatureValueHashMap().keySet();
		String[] featureStrings = new String[set.size()];//将vector集合类转化成字符串数组以便后续操作
        set.toArray(featureStrings);
        
		File file = new File(filePath);
		file.createNewFile();
		OutputStreamWriter outs = new OutputStreamWriter(new FileOutputStream(file));
		
		outs.write("@relation result" + "\r\n");
		for(int i = 0; i < set.size(); i++){
			outs.write("@attribute a" + i + " real" + "\r\n");
		}
		outs.write("@attribute class {'合法邮件','垃圾邮件'}" + "\r\n@data\r\n");
		for(int i = 0; i < featureValues.length; i++){
			String str ="";
			for(int j = 0; j < featureStrings.length; j++){
				str = str + featureValues[i].getfeatureValueHashMap().get(featureStrings[j]) + ",";
			}
			str = str + featureValues[i].getdocumentClassString() + "\r\n";
			outs.write(str);
		}
        outs.close();
	}

}
