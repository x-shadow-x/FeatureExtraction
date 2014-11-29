package english_feature_extraction;

import java.util.HashMap;

public class DocumentWord {
	private HashMap<String,Double> allWordHashMap;//存放这篇文章的各个分词
	private String className;               //存放类标号
	private int numOfAllWord;
	
	public DocumentWord(){
		allWordHashMap = new HashMap<String,Double>();
		className = "";
	}
	
	public void setallWordHashMap(HashMap<String,Double> map){
		this.allWordHashMap = new HashMap<String,Double>(map);
	}
	
	public HashMap<String,Double> getallWordHashMap(){
		return this.allWordHashMap;
	}
	
	public void setclassNameString(String className){
		this.className = className;
	}
	
	public String getclassNameString(){
		return this.className;
	}
	
	public void setnumOfAllWord(int num){
		this.numOfAllWord = num;
	}
	
	public int getnumOfAllWord(){
		return this.numOfAllWord;
	}
}
