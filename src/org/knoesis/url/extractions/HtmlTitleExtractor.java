package org.knoesis.url.extractions;

import java.util.HashSet;
import java.util.Set;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.OrFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.util.NodeIterator;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import org.knoesis.url.extractions.URLModel;
/**
 * This class is used for extracting required contents from the html page fromt the url.
 * The objective is to use this class to get the contents and use it anotate with dbpedia spotlight 
 * for corresponding analysis. 
 * @author pavan
 *
 */
public class HtmlTitleExtractor implements Extractor<String> {

	/**
	 * This method in the extractor expects two inputs in the List 
	 * 
	 * 1. URL String of the webpage to be parsed
	 * 2. The content(Element data) to be retrieved.
	 */
	@Override
	public String extract(Object text) {
		// TODO Auto-generated method stub
		String url = (String)text;
		String output = "";
		try {
			Parser parser = new Parser(url);
			NodeFilter tagFilter = new TagNameFilter("title");
			NodeFilter bodyFilter = new TagNameFilter("body");
			NodeFilter[] nodeFilters = {tagFilter};
			NodeFilter orFilter = new OrFilter(nodeFilters);
			NodeList nodeList = parser.extractAllNodesThatMatch(orFilter);

			// Not the right way to do it but for not its a hack. 
			//Need to see how Filters can be added to ge the body and the title

			Node[] nodeArray = nodeList.toNodeArray();
			for(int i=0; i<nodeArray.length; i++){
				Node node =  nodeArray[i];
				output = node.toPlainTextString();
			}		
		} catch (ParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return output;
	}


	public static void main(String args[]){
		Extractor titleExtractor = new HtmlTitleExtractor();
		String input = "http://knoesis.org/researchers/pavan";
		String textFromHtml = (String) titleExtractor.extract(input);
		Extractor dbpediaExtractor = new DBpediaSpotlightExtractor();
		Set<String> entities = (Set<String>) dbpediaExtractor.extract(textFromHtml);
		for(String entity: entities)
				System.out.println(entity);
		}


	@Override
	public void process(URLModel urlModel) {
		urlModel.setHtmlTitle(this.extract(urlModel.getOriginalURL().toString()));
	}

}
