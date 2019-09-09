package org.knoesis.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.knoesis.url.extractions.Extractor;
import org.knoesis.url.extractions.URLModel;
import org.knoesis.utils.Tokenizer;
/**
 * The objective of this class is to generate a bag of words give a list of 
 * annotated tweets. The bag of words contain the tfidf of each word
 * except stopwords and urls.
 * 
 * This class is expected to be called after the following two extractors, because
 * it uses the output of these to generate the bag of words.
 * 
 * 1. URLExtractor
 * 2. TagExtractor 
 * 
 * @author pavan
 *
 */
public class TermFrequencyGenerator implements Extractor<Map<String, Double>> {

	@Override
	public Map<String, Double> extract(Object tweet) {
		URLModel url = (URLModel) tweet;
		Map<String, Double> termFrequency = new HashMap<String, Double>();

		// TODO: I am not sure whether stemming is required 
		// FIXME: Tokenizer -- This also cleans urls again overhead work 
		String[] tokens = Tokenizer.tokenize(url.getHtmlContent()); 
		for (int i=0; i<tokens.length; i++){
			// If tokens are lesser than 3 chars then ignore
			if (tokens[i].length()<3)
				continue;
			if (termFrequency.containsKey(tokens[i]))
				termFrequency.put(tokens[i], termFrequency.get(tokens[i])+1);
			else
				termFrequency.put(tokens[i], 1.0d);
		}

		return termFrequency;
	}

	@Override
	public void process(URLModel urlModel) {
		urlModel.setBagOfWords(this.extract(urlModel));

	}

}
