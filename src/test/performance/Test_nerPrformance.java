/**
 * 
 */
package test.performance;

import java.io.IOException;

import performance.ner.NERUtils;

/**
 * @author Schmidt Gaëtan
 *
 */
public class Test_nerPrformance {

	/**
	 * @param args
	 * @author Schmidt Gaëtan
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		
		try {
			NERUtils.test();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
