package performance;

import java.io.File;
import java.io.IOException;
import java.util.List;

import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.CoreSentence;

public interface AnnotationComparator<T> 
{
	public Stats compareFiles() throws IOException;
	void compareFile(List<T> stanfordObjects, List<T> referenceObject) throws IOException;
	List<T> getSentencesFromFile(File file) throws IOException;
	Annotation getCleanSsplitAnnotation(File file) throws IOException;
	Annotation getSsplitAnnotation(File file) throws IOException;
	Annotation getInitAnnotation(File file) throws IOException;
}
