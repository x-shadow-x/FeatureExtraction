package english_feature_extraction;

import java.util.HashMap;

public class FeatureValue {
	private HashMap<String, Double> featureValueHashMap ;
	private String documentClassString ;
	
	public FeatureValue(){
		documentClassString = "";
		featureValueHashMap = new HashMap<String,Double>();
	}
	
	public HashMap<String, Double> getfeatureValueHashMap(){
		return featureValueHashMap;
	}
	public void setfeatureValueHashMap(String feature,Double value){
		featureValueHashMap.put(feature, value);
	}
	
	public void setdocumentClassString(String classindex){
		this.documentClassString = classindex;
	}
	
	public String getdocumentClassString(){
		return documentClassString;
	}
}
