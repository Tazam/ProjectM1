package test.performance;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.util.CoreMap;
import performance.ssplit.SsplitUtils;

public class AnnotationHelper 
{
	// Pour m'aider à annoter manuellement, sera supprimée plus tard
	public static void textAnnotationHelper(File file) throws IOException
	{
		Annotation annotation = SsplitUtils.getCleanAnnotation(file);
		List<CoreMap> sentences = annotation.get(SentencesAnnotation.class);
		List<Integer> phrases = new ArrayList<>();
		List<Integer> debuts = new ArrayList<>();
		List<Integer> fins = new ArrayList<>();
		List<String> textes = new ArrayList<>();
		Scanner reader = new Scanner(System.in);
			System.out.println("entité : ");
			String entity = reader.nextLine();
			while(true)
			{
				System.out.println("phrase : ");
				int p = reader.nextInt();
				if(p == -1)
				{
					break;
				}
				System.out.println("start : ");
				int d = reader.nextInt();
				System.out.println("end : ");
				int f = reader.nextInt();
					
				List<CoreLabel> span = sentences.get(p).get(TokensAnnotation.class).subList(d, f);
				String t = "";
				for(int i = 0; i < span.size(); i ++)
				{
					if(i == span.size() -1)
						t += span.get(i).originalText();
					else
						t += span.get(i).originalText() + " ";
				}
					
				phrases.add(p);
				debuts.add(d);
				fins.add(f);
				textes.add(t);
				}
			printMentions(phrases, debuts, fins, textes, entity);
	}

	public static void helperPrint(File file) throws IOException
	{
		Annotation annotation = SsplitUtils.getCleanAnnotation(file);
		List<CoreMap> sentences = annotation.get(SentencesAnnotation.class);
		for(int i = 0; i < sentences.size(); i ++)
		{
			System.out.println(i + "==> " + sentences.get(i).get(TokensAnnotation.class));
		}
	}
	
	private static void printMention(int p, int d, int f, String t)
	{
		System.out.println("		<mention>");
		System.out.println("			<sent>" + p + "</sent>");
		System.out.println("			<startIndex>" + d + "</startIndex>");
		System.out.println("			<endIndex>" + f + "</endIndex>");
		System.out.println("			<text>" + t + "</text>");
		System.out.println("		</mention>");
	}
	
	private static void printMentions(List<Integer> phrases, List<Integer> debuts, List<Integer> fins,
			List<String> textes, String entity) 
	{
		System.out.println("	<entity name =\"" + entity +"\">");
		for(int i = 0; i < phrases.size(); i ++)
			printMention(phrases.get(i), debuts.get(i), fins.get(i), textes.get(i));
		System.out.println("	</entity>");
		
	}
}
