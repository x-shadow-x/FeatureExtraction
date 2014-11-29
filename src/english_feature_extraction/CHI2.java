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

public class CHI2 {
	private String _documentPathString = "E:\\java_Eclipse\\FeatureExtraction\\文档数据2";            //文件路径
	private String[] _documentClassNameString;          //存储所有文档的文件名
	private File _documenFile;                     //存放待处理文章的文件
	private DocumentWord[] _documentTextHashMap;   //将每篇文章一整篇当成此变量的一个元素,以便在计算各个词的文档频率
	private HashMap<String, Double> _featureCHI2;    //记录所有词的词频，以便用以确定将词频排名靠前的词语提出来作为特征
	private HashSet<String> _allWord;              //用来存储所有文章的所有词语（不包括重复）以便用来做索引
	private FeatureValue[] _featureValue;          //用来存储各篇文档的特征词的文档频率
	private String[] _featureStrings;              //用来存储提取出来的特征词
	private final int _numOfFeature = 200;
	private int _numOfdocument;
	private final int M = 20;
	
	public CHI2() throws IOException{
		_numOfdocument = 0;
		_documenFile = new File(_documentPathString);
		if (!_documenFile.isDirectory())           //检查这个文件对象是否为文件夹,因为我们是把待处理的文章保存到这个文件夹下的,故要作此判断
        {
            throw new IllegalArgumentException("特征选择文档搜索失败！ [" +_documentPathString + "]");
        }
		this._documentClassNameString = _documenFile.list();//若保存待处理文章的文件夹搜索成功,则将其中存放的所有文章的文件名存储到_documentNameString中
		
		for(int i = 0; i < _documentClassNameString.length; i++){
			File tempFile = new File(_documentPathString + File.separator + _documentClassNameString[i]);
			_numOfdocument = _numOfdocument + tempFile.list().length;
		}//统计语料库的文档数目
		
		_documentTextHashMap = new DocumentWord[_numOfdocument];
        _featureStrings = new String[_numOfFeature];
        _featureValue = new FeatureValue[_numOfdocument];
        _featureCHI2 = new HashMap<String,Double>();
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
		
        //下面开始计算每个词的卡方值
        for(int i = 0; i < tempwordStrings.length; i++){
        	double maxCHI = 0.0;
        	
        	for(int j = 0; j < _documentClassNameString.length; j++){//计算这个词语对这个类的卡方值同时和当前最大的值进行比较判断是否替换
        		double arf = 0.0; double berta = 0.0;
        		double A = 0; double B = 0; double C = 0; double D = 0;
        		for(int k =0; k < _documentTextHashMap.length; k++){
        			
        			if(_documentTextHashMap[k].getclassNameString().equals(_documentClassNameString[j]))
        				if(_documentTextHashMap[k].getallWordHashMap().containsKey(tempwordStrings[i]))
        					arf = arf + _documentTextHashMap[k].getallWordHashMap().get(tempwordStrings[i]);
        		}//改进的卡方统计方法计算文档内频度adf和类内正确率berta
        		
        		
        		
        		
        		
        		for(int k = 0; k < _documentTextHashMap.length; k++){
        			if(_documentTextHashMap[k].getclassNameString().equals(_documentClassNameString[j]))
        				if(_documentTextHashMap[k].getallWordHashMap().containsKey(tempwordStrings[i]))//这篇文章属于指定类别且包含给定词语
        					A = A + 1;
        				else //这篇文章属于指定类别但不包含给定词
							C = C + 1;
        			else
        				if(_documentTextHashMap[k].getallWordHashMap().containsKey(tempwordStrings[i]))//这篇文章不属于指定类别且包含给定词语
        					B = B + 1;
        				else //这篇文章既不属于指定类别又不包含给定词
        					D = D + 1;
		
        		}//for循环结束后计算好了相应的A、B、C、D的值，下面代入公式计算给定词tempwordStrings[i]到指定类_documentClassNameString[j]的卡方值
        		
        		berta = A/(A + B);
        		
        		berta = Math.pow(M, (2 * berta - 1));
        		
        		
        		double tempCHI = ((A*D-B*C)*(A*D-B*C) * arf * berta)/((A+B)*(C+D));
        		if(tempCHI > maxCHI)
        			maxCHI = tempCHI;
        	}//for循环结束后得到词语tempwordStrings[i]到某各类的最大的卡方值，将其存储到_featureCHI中以便后面进行特征提取时做依据
        	
        	_featureCHI2.put(tempwordStrings[i], maxCHI);
        	
        }//for循环结束后_featureCHI保存了所有词的卡方值，下面进行特征提取
        
        
        List<Map.Entry<String, Double>> list_Data = new ArrayList<Map.Entry<String, Double>>(_featureCHI2.entrySet()); 
	    
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
	    
	    
	    
	    
	    
	  //取出词频排在前20的词语作为特征词
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