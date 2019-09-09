package org.knoesis.url.extractions;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.knoesis.utils.TermFrequencyGenerator;
/**
 * This class should be called instead of just the URL Extractor to collect all the 
 * information about the URLs. This comprises of the following 
 * 
 * 1. Regex URL Extraction
 * 2. URL Resolution
 * 3. Title Extraction
 * 4. HTML Content Extraction
 * 5. Entities from the HTML Content and the title
 * 
 * 
 * @author pavan
 *
 * TODO: Almost all the above classes make atleast one HTTP request which is computationally expensive.
 * 		 Try to reduce the number of HTTP requests done.
 *
 */
public class URLInformationAssembler implements Extractor<Boolean> {
	private static List<Extractor> urlContentExtractors;
	/**
	 * This receives all the extractions to be done
	 * @param urlContentExtractors
	 */
	public URLInformationAssembler(List<Extractor> urlContentExtractors) {
		this.urlContentExtractors = urlContentExtractors;
	}

	public URLInformationAssembler(){
		urlContentExtractors = new ArrayList<Extractor>();
                urlContentExtractors.add(new RegexURLExtractor());
		urlContentExtractors.add(new APIURLResolver());
		urlContentExtractors.add(new HtmlTitleExtractor());
		urlContentExtractors.add(new BoilerplateHTMLExtractor());
                urlContentExtractors.add(new ImageURLExtractor());
		//urlContentExtractors.add(new DBpediaSpotlightExtractor());
		urlContentExtractors.add(new TermFrequencyGenerator());
	}

	/**
	 * The extract funtion here is useless because this class call the other classes for 
	 * extracting the information
	 * 
	 */
	@Override
	public Boolean extract(Object text) {
		// TODO Auto-generated method stub
		return null;
	}


	public static void main(String[] args) {
		//Make a tweet and test it
	}

	@Override
	public void process(URLModel urlModel) {
		try{
			for(Extractor e: urlContentExtractors){
				e.process(urlModel);
			}
		}catch(NullPointerException e){
			//skipping if the url cant be processed.
                        Logger.getLogger(URLInformationAssembler.class.getName()).log(Level.WARNING, "Couldn't process URL" + urlModel.getShortURL(), e);
			urlModel = null;
		}
	}

}
