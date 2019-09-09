package org.knoesis.url.extractions;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;


import org.knoesis.url.extractions.*;

import de.l3s.boilerpipe.BoilerpipeProcessingException;
import de.l3s.boilerpipe.extractors.ArticleExtractor;
import de.l3s.boilerpipe.extractors.DefaultExtractor;
/**
 * This class is used for extracting required contents from the html page fromt the url.
 * The objective is to use this class to get the contents and use it anotate with dbpedia spotlight 
 * for corresponding analysis.
 * 
 * The code uses the boilerplate api which helps to extract just the main content from an html.
 * 
 * @author pavan
 *
 */
public class BoilerplateHTMLExtractor implements Extractor<String> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public String extract(Object text) {
		String articleText = null;
		try {
			articleText=ArticleExtractor.INSTANCE.getText((URL) text);
		} catch (BoilerpipeProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return articleText;
	}
	
	
	
	public static void main(String[] args){
		Extractor e = new BoilerplateHTMLExtractor();
		System.out.println(e.extract("http://bit.ly/twarql"));
		System.out.println(e.extract("www.worldcantwait.org"));
		System.out.println(e.extract("yahoo.com"));
		System.out.println(e.extract("http://www.cnn.com/video/#/video/world/2009/06/13/shubert.uk.iran.election.protests.cnn?"));
		System.out.println(e.extract("electionhttp://www.cnn.com/video/#/video/world/2009/06/13/shubert.uk.iran.election.protests.cnn?"));
		System.out.println(e.extract("http://test.com/myservlet?param=12234."));
	}

	@Override
	public void process(URLModel urlModel) {
		urlModel.setHtmlContent(this.extract(urlModel.getOriginalURL()));
		
	}

}
	