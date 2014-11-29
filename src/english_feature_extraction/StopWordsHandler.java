package english_feature_extraction;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

public class StopWordsHandler {

		/*private static String stopWordsList[] ={"[", "]"," ","(",")","@","be","a","an","the","of","in","on",
			                                    "for","and","to","I","/","\"","",",","-",":","may","which",
			                                    "a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p",
			                                    "q","r","s","t","u","v","w","x","y","z","me","any","that","there",
			                                    "this","would","at","if","with","you","your","as","'","have",
			                                    "?",">=","who","!",">","np","can","could","anyone","1","2",
			                                    "3","4","5","6","7","8","9","0","'s"};//常用停用词*/
	private static String stopWordsList[];
		
		
		public static void setstopWordsList() throws IOException{
			File file = new File("E:\\java_Eclipse\\FeatureExtraction\\英文停用词表.txt");
			InputStreamReader isReader =
        			new InputStreamReader(new FileInputStream("E:\\java_Eclipse\\FeatureExtraction\\英文停用词表.txt"),"GBK");
            BufferedReader reader = new BufferedReader(isReader);
            String aline;
            StringBuilder sb = new StringBuilder();
            while ((aline = reader.readLine()) != null)
            {
                sb.append(aline + " ");
            }
            isReader.close();
            reader.close(); 
            stopWordsList =sb.toString().split(" ");
		}
		
		
		
		
		
	    public static boolean IsStopWord(String word)
	    {
	        for(int i=0;i<stopWordsList.length;++i)
	        {
	            if(word.equalsIgnoreCase(stopWordsList[i]))
	                return true;
	        }
	        return false;
	    }
}
