package org.knoesis.url.extractions;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


/**
 * This can be done to a few main news websites that are listed down 
 * 
 * 1. Times of india -- http://timesofindia.indiatimes.com/
 *		DONE
 * 2. The Hindu -- http://www.thehindu.com/
 * 		DONE
 * 3. ndtv -- http://www.ndtv.com/
 * 		DONE
 * 4. deccan herald -- http://www.deccanherald.com/
 * 		COULD NOT BE DONE
 * 5. CNN IBN -- http://ibnlive.in.com/
 * 6. DNA India -- http://www.dnaindia.com
 * 		NEED SIGN IN TO VIEW COMMENTS TOO :D
 * 7. OneIndiaNews -- http://news.oneindia.in 
 *  
 * @author pavan
 *
 */
public class ReviewExtractor implements Extractor<List<String>> {


	public static void main(String[] args) throws IOException {
		//		Document doc = Jsoup.connect("http://timesofindia.indiatimes.com/india/Karnataka-deputy-chief-minister-forced-to-taste-contaminated-water/articleshow/15114743.cms").userAgent("Mozilla").timeout(6000).get();
		//		Elements comments = doc.getAllElements();
		//		for(Element comment: comments){
		//			System.out.println("Comment:" +comment.html());
		//		}
		
	}

	public static List<String> timesOfIndiaParser(String url) throws IOException{
		List<String> commentsList = new ArrayList<String>();
		URL timesUrl = new URL("http://timesofindia.indiatimes.com/entertainment/bollywood/news-interviews/I-need-to-look-fresher-next-time-I-do-a-movie-Saif-Ali-Khan/articleshow/15106110.cms");
		System.out.println(timesUrl.getFile());
		Pattern pattern = Pattern.compile("articleshow/([0-9]*).cms");
		Matcher m = pattern.matcher(timesUrl.getFile());
		//Find the article number from the URL and if there is no article number then quit
		String articleNumber = "";
		while(m.find()){
			articleNumber = m.group(1);		
			System.out.println(articleNumber);
		}
		// PAVAN: I found this url in the times of india article -- This might change over time :D
		Document doc = Jsoup.connect("http://timesofindia.indiatimes.com/new_cmtofart2_v4/" +
				articleNumber+".cms?msid="+articleNumber).userAgent("Mozilla").timeout(6000).get();
		Elements comments = doc.getElementsByClass("cmt");
		// Adding a boolean to alternate between comment and username 
		boolean isComment = false;
		for(Element comment: comments){
			if (!isComment){
				isComment = true;
				continue;
			}
			Elements divs = comment.getElementsByTag("div");
			String commentString = divs.get(0).getElementsByTag("span").get(0).text();
			System.out.println(commentString);
			commentsList.add(commentString);
			isComment = false;
		}
		return commentsList;
	}

	public static List<String> ndtvCommentsParser(String url) throws IOException{
		Document doc = Jsoup.connect(url).userAgent("Mozilla").timeout(6000).get();
		List<String> commentsList = new ArrayList<String>();
		Elements iframes = doc.getElementsByAttributeValue("name", "ndtvSocialCommentsList");
		for (Element iframe : iframes) {
			Document iframeDoc =  Jsoup.connect(iframe.attr("src")).userAgent("Mozilla").timeout(6000).get();
			Elements comments = iframeDoc.getElementsByClass("post_text");
			for(Element comment: comments){
				commentsList.add(comment.text());
			}
		}
		return commentsList;
	}

	public static List<String> theHinduCommentsParser(String url) throws IOException{
		Document doc = Jsoup.connect("http://www.thehindu.com/news/national/article3672666.ece").userAgent("Mozilla").timeout(6000).get();
		List<String> commentsList = new ArrayList<String>();
		Elements comments = doc.getElementsByClass("comment");
		for(Element comment: comments){
			try{
				commentsList.add(comment.getElementsByTag("p").get(0).text());
			} catch(IndexOutOfBoundsException e){
				//Log this as the div that is the textbox to write as comment and therefore all 
				//the comments are retrieved
			}
		}
		return commentsList;
	}

	@Override
	public List<String> extract(Object text) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void process(URLModel urlModel) {
		// TODO Auto-generated method stub

	}
}
