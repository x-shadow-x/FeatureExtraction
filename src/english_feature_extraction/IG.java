package english_feature_extraction;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class IG {
	private String _documentPathString = "E:\\java_Eclipse\\FeatureExtraction\\文档数据2";            //文件路径
	private String[] _documentClassNameString;          //存储所有文档的文件名
	private File _documenFile;                          //存放待处理文章的文件
	private DocumentWord[] _documentTextHashMap;        //将每篇文章一整篇当成此变量的一个元素,以便在计算各个词的文档频率
	private HashMap<String, Double> _featureIG;         //记录所有词的词频，以便用以确定将词频排名靠前的词语提出来作为特征
	private HashSet<String> _allWord;                   //用来存储所有文章的所有词语（不包括重复）以便用来做索引
	private FeatureValue[] _featureValue;               //用来存储各篇文档的特征词的文档频率
	private String[] _featureStrings;                   //用来存储提取出来的特征词
	private final int _numOfFeature = 300;              //特征词数目
	private HashMap<String, Integer> _numOfClassDocument;                  //记录每个类别下的文章有几篇
	private int _numOfdocument;                         //记录所有文章的总数
	
	
	public IG() throws IOException{
		_numOfdocument = 0;
		_numOfClassDocument = new HashMap<String, Integer>();
		
		_documenFile = new File(_documentPathString);
		if (!_documenFile.isDirectory())           //检查这个文件对象是否为文件夹,因为我们是把待处理的文章保存到这个文件夹下的,故要作此判断
        {
            throw new IllegalArgumentException("特征选择文档搜索失败！ [" +_documentPathString + "]");
        }
		this._documentClassNameString = _documenFile.list();//若保存待处理文章的文件夹搜索成功,则将其中存放的所有文章的文件名存储到_documentNameString中
		
		for(int i = 0; i < _documentClassNameString.length; i++){
			File tempFile = new File(_documentPathString + File.separator + _documentClassNameString[i]);
			_numOfdocument = _numOfdocument + tempFile.list().length;
			_numOfClassDocument.put(_documentClassNameString[i], tempFile.list().length);
		}//统计语料库的文档数目
		
		
		_documentTextHashMap = new DocumentWord[_numOfdocument];
        _featureStrings = new String[_numOfFeature];
        _featureValue = new FeatureValue[_numOfdocument];
        _featureIG = new HashMap<String,Double>();
        _allWord = new HashSet<String>();
        
        int index = 0;
		for(int i = 0; i < _documentClassNameString.length; i++){
			File tempFile = new File(_documentPathString + File.separator + _documentClassNameString[i]);
			String[] textString = tempFile.list();
			for(int j = 0; j < textString.length; j++){
				InputStreamReader isReader =
	        			new InputStreamReader(new FileInputStream(_documenFile.getPath() + File.separator +_documentClassNameString[i] + File.separator + textString[j]),"GBK");
	            BufferedReader reader = new BufferedReader(isReader);
	            String aline;
	            StringBuilder sb = new StringBuilder();
	            while ((aline = reader.readLine()) != null)
	            {
	                sb.append(aline + " ");
	            }
	            isReader.close();
	            reader.close(); 
	            String[] tempStrings1 =sb.toString().split(" ");
	            StopWordsHandler.setstopWordsList();
	            Vector<String> v1 = new Vector<String>();
	            
	            for(int ii = 0; ii < tempStrings1.length; ++ii)
	            {
	                if(StopWordsHandler.IsStopWord(tempStrings1[ii]) == false)
	                {//不是停用词
	                    v1.add(tempStrings1[ii]);
	                }
	            }
	            String[] tempStrings = new String[v1.size()];//将vector集合类转化成字符串数组以便后续操作
	            v1.toArray(tempStrings);
	            
	            _documentTextHashMap[index] = new DocumentWord();
	            
	            //将当前这篇文章的词语存储到Hashset中,达到去重复的目的,同时为下面快速统计词语的文档频率
	            HashMap<String, Double> temp = new HashMap<String,Double>();
	            
	            for(int jj = 0; jj < tempStrings.length; jj++){
	            	if(temp.containsKey(tempStrings[jj])){
	            		double value = temp.get(tempStrings[jj]) + 1;
	            		temp.put(tempStrings[jj], value);
	            	}
	            	else {
	            		temp.put(tempStrings[jj], 1.0);
					}
	            	_allWord.add(tempStrings[jj]);

	            }//统计好这个类别下这篇文章的词频再存储到对应的_documentTextHashMap中去
	            _documentTextHashMap[index].setallWordHashMap(temp);
	            _documentTextHashMap[index].setclassNameString(_documentClassNameString[i]);
	            index++;
	            
			}
			
		}//此for循环结束后_allWord中存储了所有的不重复的词，_documentTextHashMap中存储了各篇文章的词频，可以利用其中的成语变量Hashmap的key值来判断这篇文章是否包含特定词条
		
		String[] tempwordStrings = new String[_allWord.size()];
        _allWord.toArray(tempwordStrings);
		
        //下面开始计算每个词的信息增益
        for(int i = 0; i < tempwordStrings.length; i++){
        	double Pc_i;     //ci类文档在语料库中出现的概率
        	double Pt = 0.0;       //语料库中包含词条t的文档的概率
        	double nPt;      //预料库中不包含词条t的文档的概率
        	double Pc_i_t;   //文档包含词条t时属于ci的条件概率
        	double Pc_i_nt;  //文档不包含词条t时属于ci的条件概率
        	double tempNumOfDocumentContainT = 0.0;
        	double sum1 = 0.0;
        	double sum2 = 0.0;
        	double sum3 = 0.0;
        	
        	//信息增益计算公式中的三项代数式
        	
        	for(int j = 0; j < _documentClassNameString.length; j++){
        		Pc_i = (double)_numOfClassDocument.get(_documentClassNameString[j])/(double)_numOfdocument;
        		sum1 = sum1 + (0-Pc_i*Math.log(Pc_i));
        	}
        	
        	for(int j = 0; j < _documentTextHashMap.length; j++){
        		if(_documentTextHashMap[j].getallWordHashMap().containsKey(tempwordStrings[i])){
        			tempNumOfDocumentContainT = tempNumOfDocumentContainT + 1.0;
        		}//记录下包含词条t的的文档总数
        	}
        	Pt = tempNumOfDocumentContainT/_numOfdocument;
        	nPt = 1-Pt;
        	
        	for(int j = 0; j < _documentClassNameString.length; j ++){
        		double temp1 = 0.0;//用以记录既属于指定类，又包含词条t的文档数
        		double temp2 = 0.0;//用以记录既属于指定类，但不包含词条t的文档数
        		for(int k = 0; k < _documentTextHashMap.length; k++){
        			if(_documentTextHashMap[k].getclassNameString().equals(_documentClassNameString[j])){
        				if(_documentTextHashMap[k].getallWordHashMap().containsKey(tempwordStrings[i])){
        					temp1 = temp1 + 1.0;//既属于指定类，又包含词条t的文档数
        				}
        				else {
							temp2 = temp2 + 1.0;//属于指定类，但不包含词条t的文档数
						}
        			}
        		}
        		
        		Pc_i_t = temp1/tempNumOfDocumentContainT;
        		Pc_i_nt = temp2/(_numOfdocument-tempNumOfDocumentContainT);
        		sum2 = sum2 + Pc_i_t * Math.log(Pc_i_t);
        		sum3 = sum3 + Pc_i_nt * Math.log(Pc_i_nt);
        		
        	}
        	sum2 = sum2 * Pt;
        	sum3 = sum3 * nPt;
        	double result = sum1 + sum2 + sum3;
        	_featureIG.put(tempwordStrings[i], result);

        }//for循环结束_featureIG中存储了所有词的信息增益值，下面进行特征选择
        List<Map.Entry<String, Double>> list_Data = new ArrayList<Map.Entry<String, Double>>(_featureIG.entrySet()); 
        System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
	    Collections.sort(list_Data, new Comparator<Map.Entry<String, Double>>(){
	    	public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2){ 
	        if ((o2.getValue() - o1.getValue())>0)  
	        	return 1;  
	        else if((o2.getValue() - o1.getValue())==0)  
	        	return 0;  
	        else   
	        	return -1;  
	        }  }  );  
	    
	    System.out.println(list_Data);
	    
	    //提取出前n个特征词作为向两属性
	    for(int i = 0; i < _numOfFeature; i++){
	    	_featureStrings[i] = list_Data.get(i).getKey();
	    }
	    
	    //以tf填充向量空间
	    for(int i = 0; i < _documentTextHashMap.length; i++){
	    	
	    	_featureValue[i] = new FeatureValue();
	    	_featureValue[i].setdocumentClassString(_documentTextHashMap[i].getclassNameString());//存储类标号
	    	
	    	for(int j = 0; j < _featureStrings.length; j++){
	    		String featureString = _featureStrings[j];
	    		if(_documentTextHashMap[i].getallWordHashMap().containsKey(_featureStrings[j]))
	    			_featureValue[i].setfeatureValueHashMap(featureString, _documentTextHashMap[i].getallWordHashMap().get(_featureStrings[j]));
	    		else {
	    			_featureValue[i].setfeatureValueHashMap(featureString,0.0);
				}
	    	}
	    }
		
	}
	
	public FeatureValue[] getFeatureValue(){
		return _featureValue;
	}
}

