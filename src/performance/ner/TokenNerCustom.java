/**
 * 
 */
package performance.ner;

/**
 * @author Schmidt Gaëtan
 *
 */
public class TokenNerCustom {
	
	Integer sentenceNum;
	Integer sentencePos;
	String word;
	String nerTag;
	
	public TokenNerCustom()
	{
		this.sentenceNum = -1;
		this.sentencePos = -1;
		this.word = null;
		this.nerTag = null;
	}
	
	public TokenNerCustom(Integer sentenceNum, Integer sentencePos, String word, String nerTag)
	{
		this.sentenceNum = sentenceNum;
		this.sentencePos = sentencePos;
		this.word = word;
		this.nerTag = nerTag;
	}
	
	public Integer getSentencePos()
	{
		return this.sentencePos;
	}
	
	public String getWord()
	{
		return this.word;
	}
	
	public String getNerTag()
	{
		return this.nerTag;
	}
	
	public Integer getSentenceNum()
	{
		return this.sentenceNum;
	}
	
	public String toString()
	{
		return " { sentenceNum="+this.sentenceNum+" sentencePos="+this.sentencePos+" word="+this.word+" nerTag="+this.nerTag+" } ";
	}
	
	

}
