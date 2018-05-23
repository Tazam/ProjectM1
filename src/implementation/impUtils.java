/**
 * 
 */
package implementation;

import java.util.Map;

import edu.stanford.nlp.coref.data.CorefChain;
import edu.stanford.nlp.coref.data.CorefChain.CorefMention;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.CoreDocument;

/**
 * @author Schmidt Gaëtan
 *
 */
public class impUtils {
	
	protected static CorefChain corefByToken(Map<Integer,CorefChain> corefChains, CoreLabel token)
	{
		CorefChain ret = null;
		
		for (CorefChain corefchain : corefChains.values())
		{
			for (CorefMention mention : corefchain.getMentionsInTextualOrder())
			{
				if (mention.sentNum == token.sentIndex())
				{
					if (mention.startIndex-1 <= token.index()&& token.index() <= mention.endIndex-1)
					{
						return corefchain;
					}
				}
			}
		
		}
		return ret;
	}
	
	/**
	 * 
	 * @param corefchain
	 * @return corefMention : corefMention wich contains a NER entity or null
	 * @author Schmidt Gaëtan
	 */
	protected static CorefMention valideRepresentativeMention(CorefChain corefchain, CoreDocument document)
	{

		if (corefMentionContainsNER(corefchain.getRepresentativeMention(),document))
		{
			return corefchain.getRepresentativeMention();
		}else
		{
			for (CorefMention mention : corefchain.getMentionsInTextualOrder())
			{
				if (corefMentionContainsNER(mention,document))
					return mention;
			}
		}
		return null;
	}
	
	/**
	 * 
	 * @param corefMention
	 * @return true : arg contains a ner entity
	 * @author Schmidt Gaëtan
	 */
	protected static boolean corefMentionContainsNER(CorefMention corefMention, CoreDocument document)
	{
		for (int i = corefMention.startIndex-1; i < corefMention.endIndex-1; i++)
		{
			if ("PERSON".equals(document.sentences().get(corefMention.sentNum-1).tokens().get(i).ner()))
				return true;
		}
		return false;
	}

}
