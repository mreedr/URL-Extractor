package org.knoesis.url.extractions;

import java.io.Serializable;
import java.net.URL;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class models a URL found in a tweet. 
 * Mainly comprises of a shortURL and longURLs with the entities found in the content of the html
 * The objective of this class is to find the entities that helps to get the context of the tweet
 * 
 * @author pavan
 *
 */
public class URLModel implements Serializable{
	private int id;
	private String urlTweet;
	private URL shortURL;
	private URL originalURL;
        private URL imageURL;
	private String htmlContent; 
	private Set<String> entities;
	private String htmlTitle;
	private int similarityId;
	private Map<String, Double> bagOfWords;
	private int popularityCount;
	
	public URLModel(URL shortURL) {
		this.shortURL = shortURL;
	}
	public URLModel(URL shortURL, int id, int count) {
		this.shortURL = shortURL;
		this.id = id;
		this.popularityCount = count;
	}
	public URLModel(String urlTweet) {
		this.urlTweet = urlTweet;
	}
	public URL getOriginalURL() {
		return originalURL;
	}
	public void setOriginalURL(URL originalURL) {
		this.originalURL = originalURL;
	}
	public String getHtmlContent() {
		return htmlContent;
	}
	public void setHtmlContent(String htmlContent) {
		this.htmlContent = htmlContent;
	}
	public Set<String> getEntities() {
		return entities;
	}
	public void setEntities(Set<String> entities) {
		this.entities = entities;
	}
	public URL getShortURL() {
		return shortURL;
	}
	public void setShortURL(URL shortURL) {
		this.shortURL = shortURL;
	}
	public URL getImageURL(){
            return imageURL;
        }
        public void setImageURL(URL imageURL){
            this.imageURL = imageURL;
        }
        
	/**
	 * toString() method overriden with returning the originalURL as a string
	 */
	@Override
	public String toString() {
		String result = "";
		if (originalURL == null)
			originalURL = shortURL;
		result+="id: "+id+"\n";
		result+="original URL: "+originalURL.toString() + "\n";
		result+= "Title: " + htmlTitle + "\n";
		//result+= "Content: " +htmlContent + "\n";
		//result+= "Entities: "+entities+"\n";
		result+= "Similarity: "+similarityId+"\n";
                result+= "Image URL: "+imageURL+"\n";
		return result;
		
	}
	public String getHtmlTitle() {
		return htmlTitle;
	}
	public void setHtmlTitle(String htmlTitle) {
		this.htmlTitle = htmlTitle;
	}
	public String getUrlTweet() {
		return urlTweet;
	}
	public void setUrlTweet(String urlTweet) {
		this.urlTweet = urlTweet;
	}
	
	public JSONObject jsonSerialize(boolean heading, boolean html, boolean entity, boolean origurl){
		JSONObject returnResult = new JSONObject();
		JSONObject result = new JSONObject();
		JSONArray entities = new JSONArray();
//		for(String dbEntity: this.entities){
//			entities.put(dbEntity);
//		}
		try {
			result.put("originalURL", this.originalURL);
			result.put("entities", entities);
                        result.put("ImageURL", this.getImageURL());
			if(heading)
				result.put("title", this.htmlTitle);
			if(html)
				result.put("htmlContent", this.htmlContent);
			returnResult.put("result", result);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return returnResult;
	}
	public int getSimilarityId() {
		return similarityId;
	}
	public void setSimilarityId(int similarityId) {
		this.similarityId = similarityId;
	}
	public Map<String, Double> getBagOfWords() {
		return bagOfWords;
	}
	public void setBagOfWords(Map<String, Double> bagOfWords) {
		this.bagOfWords = bagOfWords;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getPopularityCount() {
		return popularityCount;
	}
	public void setPopularityCount(int popularityCount) {
		this.popularityCount = popularityCount;
	}
	
}
