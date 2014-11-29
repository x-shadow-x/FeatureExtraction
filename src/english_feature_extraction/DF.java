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


public class DF {
	private String _documentPathString = "E:\\java_Eclipse\\FeatureExtraction\\文档数据2";            //文件路径
	private String[] _documentClassNameString;          //存储所有文档的文件名
	private File _documenFile;                     //存放待处理文章的文件
	private DocumentWord[] _documentTextHashMap;   //将每篇文章一整篇当成此变量的一个元素,以便在计算各个词的文档频率
	private HashMap<String, Double> _featureDF;    //记录所有词的词频，以便用以确定将词频排名靠前的词语提出来作为特征
	private HashSet<String> _allWord;              //用来存储所有文章的所有词语（不包括重复）以便用来做索引
	private FeatureValue[] _featureValue;          //用来存储各篇文档的特征词的词频
	private String[] _featureStrings;              //用来存储提取出来的特征词
	private final int _numOfFeature = 300;
	private int _numOfdocument;
	
	public DF() throws IOException{
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
        _featureDF = new HashMap<String,Double>();
        _allWord = new HashSet<String>();
        
        int index = 0;
        for(int i = 0; i < _documentClassNameString.length; i++){
        	File tempFile = new File(_documentPathString + File.separator + _documentClassNameString[i]);
			String[] textString = tempFile.list();
			for(int j = 0; j < textString.length; j++){
				InputStreamReader isReader =
        			new InputStreamReader(new FileInputStream(_documenFile.getPath() + File.separator + _documentClassNameString[i] + File.separator + textString[j]),"GBK");
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
            	//System.out.println(temp);
                	_allWord.add(tempStrings[jj]);
            	
            	
            	//_documentTextHashSet[i].setallWordHashSet(tempStrings[j]);
                }
                _documentTextHashMap[index].setallWordHashMap(temp);
                _documentTextHashMap[index].setclassNameString(_documentClassNameString[i]);
                index++;
			}
        }//for循环结束,_documentTextHashSet[i]的每个元素就是一个DocumentWord类型的变量,其中以Hashset类保存了第i篇文档的所有不重复的词
         //_allWord存储了所有的词语
        
        
        String[] tempwordStrings = new String[_allWord.size()];
        _allWord.toArray(tempwordStrings);
        
        //==========================================================================================================
        System.out.println("########################################################");
        for(int i = 0 ; i < tempwordStrings.length; i++){
        	System.out.println(tempwordStrings[i]);
        }
        //==========================================================================================================
        
        
       
        
        //开始统计各个词语的文档平率
        for(int i = 0; i < tempwordStrings.length; i++){
        	double thiswordnum = 0;
        	for(int j = 0; j < _documentTextHashMap.length; j++){
        		if(_documentTextHashMap[j].getallWordHashMap().containsKey(tempwordStrings[i]))
        		{
        			thiswordnum = thiswordnum + 1;
        		}
        	}
        	
        	_featureDF.put(tempwordStrings[i], thiswordnum);
        	
        }//for循环结束_featureDF中保存了所有的词的文档频率,重写sort方法对_featureDF按value值排序
        
		List<Map.Entry<String, Double>> list_Data = new ArrayList<Map.Entry<String, Double>>(_featureDF.entrySet()); 
		
	    System.out.println(list_Data);///////////////////////////////////////////////////////////////////////////////////////
	    
	    Collections.sort(list_Data, new Comparator<Map.Entry<String, Double>>()  
	        {    
	      public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2)  
	      {  
	        if ((o2.getValue() - o1.getValue())>0)  
	          return 1;  
	        else if((o2.getValue() - o1.getValue())==0)  
	          return 0;  
	        else   
	          return -1;  
	      }  
	        }  
	        );  
        
	    System.out.println(list_Data);
	    //取出词频排在前20的词语作为特征词
	    for(int i = 0; i < _numOfFeature; i++){
	    	_featureStrings[i] = list_Data.get(i).getKey();
	    }
	    
	    
	    
	    System.out.println("000000000000000000000000000000000000000000");
	    for(int i = 0; i < _numOfFeature; i++){
	    	System.out.println(_featureStrings[i]);
	    }
	    System.out.println("000000000000000000000000000000000000000000");
	    
	    for(int i = 0; i < _documentTextHashMap.length; i++){
	    	
	    	_featureValue[i] = new FeatureValue();
	    	_featureValue[i].setdocumentClassString(_documentTextHashMap[i].getclassNameString());//^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
	    	
	    	for(int j = 0; j < _featureStrings.length; j++){
	    		String featureString = _featureStrings[j];
	    		double value = _featureDF.get(featureString);
	    		if(_documentTextHashMap[i].getallWordHashMap().containsKey(_featureStrings[j]))
	    			_featureValue[i].setfeatureValueHashMap(featureString, _documentTextHashMap[i].getallWordHashMap().get(_featureStrings[j]));
	    		else {
	    			_featureValue[i].setfeatureValueHashMap(featureString,0.0);
				}
	    	}
	    	
	    }
	    System.out.println("==============================================================================");
	    System.out.println(list_Data.size());
	    System.out.println(_featureDF.size());
	    System.out.println(tempwordStrings.length);
	    System.out.println(_featureDF);
        
	}
	
	public FeatureValue[] getFeatureValue(){
		return _featureValue;
	}
	
	
}
