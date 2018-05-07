/**
 * 
 */
package implementation;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Schmidt Gaëtan
 *
 */
public class RelationsExtractor implements Task {
	
	List<RelationshipExtractionMethod> methodsList;
	
	RelationsExtractor()
	{
		this.methodsList = new ArrayList<RelationshipExtractionMethod>();
	}
	
	RelationsExtractor(List<RelationshipExtractionMethod> methodsList)
	{
		this.methodsList = methodsList;
	}
	
	void AddMethod(RelationshipExtractionMethod m)
	{
		this.methodsList.add(m);
	}

	/* (non-Javadoc)
	 * @see implementation.Task#MainWork()
	 */
	@Override
	public void MainWork() {

		for (RelationshipExtractionMethod m : this.methodsList)
		{
			m.MainWork();
		}

	}

}
