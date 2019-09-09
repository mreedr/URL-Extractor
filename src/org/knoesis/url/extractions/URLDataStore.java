package org.knoesis.url.extractions;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.knoesis.utils.CosineSimilarityCalculator;




/**
 * This class connects to the database and queries the necessary 
 * information to the database
 * 
 * TODO: This is a messy code -- make sure to add appropriate comments
 * 	
 * @author pavan
 *
 */
public class URLDataStore {
	private static Connection con = null;
	String serverName = "";
	private Set<String> blockedHosts = new HashSet<String>();
	public URLDataStore() {
		//Set hosts
		blockedHosts.add("twitter.com");
		blockedHosts.add("facebook.com");
		blockedHosts.add("instagram.com");
		blockedHosts.add("itunes.apple.com");
		Properties connectionProperties = new Properties();
		connectionProperties.put("user", "twitris");
		connectionProperties.put("password", "pranyd09!");
		try {
			con = DriverManager.getConnection("jdbc:mysql://130.108.5.96:3306/twitris_healthcare",
					connectionProperties);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Inserting the url in the database
	 * @param url
	 * @return
	 */
	public void insertURL(String url, String tweetId){
		String insertQuery = "Insert ignore into articles (url) values ('"+url+"');";
		String selectQuery = "Select id from articles where url='"+url+"'";

		Statement stmt;
		try {
			int id = 0;
			stmt = con.createStatement();
			//if its not a duplicate
			if(stmt.execute(insertQuery, Statement.RETURN_GENERATED_KEYS)){
				ResultSet genKeys = stmt.getGeneratedKeys();
				id = genKeys.getInt(0);
				System.out.println("id1: "+id);
			}
			else{
				ResultSet result = stmt.executeQuery(selectQuery);
				while(result.next()){
					id = result.getInt("id");
					System.out.println("id2: "+id);
				}

			}
			String insertTweetArticle = "Insert ignore into articles_tweets (article_id, twitter_ID) values ("+id+", '"+tweetId+"');";
			stmt.execute(insertTweetArticle);
		}
		catch (SQLException e) {
			e.printStackTrace();
		}	
	}

	/**
	 * Inserting the details of the URL into the table with 
	 * similarity to other highly ranked URLs
	 */
	public void insertURLDetails(String event){
		List<URLModel> urlModels = new ArrayList<URLModel>();
		URLInformationAssembler urlInfoAssembler = new URLInformationAssembler();
		//TODO: Check this sql for mistakes -- Also we need to put up and interval in 
		// the where clause
		String selectURLs = "select a.id as id, a.url as url, count(at.article_id) as count from articles as a, articles_tweets as at where at.article_id = a.id GROUP BY at.article_id ORDER BY count DESC LIMIT 20;";
		Statement stmt;
		try{
			stmt = con.createStatement();
			ResultSet results = stmt.executeQuery(selectURLs);
			String url=null;
			int i=0;
			while(results.next()){
				// Adding models to maintain the priority based on ranking
				String urlString = results.getString("url");
				int id = results.getInt("id");
				int count = results.getInt("count");
				System.out.println(id + ":" +urlString);
				try{
					URL urlNet = new URL(urlString);
					System.out.println(urlNet.getHost());
					if(blockedHosts.contains(urlNet.getHost())){
						continue;
					}
					urlModels.add(i, new URLModel(urlNet, id, count));
				}catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					continue;
				}
				urlInfoAssembler.process(urlModels.get(i));
				i++;
			}
			System.out.println("getting the final urls");
			urlModels = getFinalUrls(urlModels);
			for(URLModel urlModel: urlModels){
				// Add every URL into the database 
				String insertURLDetails = "Insert into articles_ranked (article_id, event_id, " +
						"share_count, title, image_url, text) values (?, ?, ?, ?, ?, ?)";
				System.out.println(insertURLDetails);
				PreparedStatement pStmt = con.prepareStatement(insertURLDetails);
				pStmt.setInt(1, urlModel.getId());
				pStmt.setString(2, "olympics2012");
				pStmt.setInt(3, urlModel.getPopularityCount());
				pStmt.setString(4, urlModel.getHtmlTitle());
				pStmt.setString(5, urlModel.getImageURL().toString());
				pStmt.setString(6, urlModel.getHtmlContent());
				pStmt.executeUpdate();
				System.out.println(urlModel.toString());
			}
		}catch (SQLException e){
			e.printStackTrace();

		}
	}

	private List<URLModel> getFinalUrls(List<URLModel> urlModels) {
		// Threshhold for cosine similarity of the documents;

		double threshHold = 0.85d;

		for(int i=urlModels.size()-1; i>=0;i--){
			for(int j=0; j<i; j++){
				try{
					if(CosineSimilarityCalculator.calculate(urlModels.get(j).getBagOfWords(), urlModels.get(i).getBagOfWords())>=threshHold){
						urlModels.get(i).setSimilarityId(urlModels.get(j).getId());
						break;
					}
				}catch(NullPointerException e){

				}

			}
		}
		return urlModels;
	}

        public ArrayList<String> getRawTweets() throws SQLException{
            ArrayList<String> tweetList = new ArrayList<String>();
            ArrayList<String> idList = new ArrayList<String>();
            Statement stmt = con.createStatement();
            String query = "SELECT tweet FROM twitterdata LIMIT 300";
            String idQuery = "SELECT id FROM twitterdata LIMIT 300";
            
            ResultSet result = stmt.executeQuery(query);
            while(result.next()){
                tweetList.add(result.getString("tweet"));
            }
            
            ArrayList<URL> urlFromTweets = new ArrayList<URL>();
            RegexURLExtractor extractor = new RegexURLExtractor();
            
            for(String s: tweetList){
                urlFromTweets.add(extractor.extract(s));
            }
            for(URL url: urlFromTweets){ //get all of the long urls
                url = 
            }
            
            
            result = stmt.executeQuery(idQuery);
            while(result.next()){
                idList.add(result.getString("id"));
            }
            
            for(int i=0; i<idList.size(); i++){
                insertURL(urlFromTweets.get(i).toString(),idList.get(i));
            }
            
            return tweetList;
            
        }
        
	public static void main(String[] args) {
		URLDataStore dataStore = new URLDataStore();
		//dataStore.insertURL("http://news.carbon-future.co.uk/archives/42856", "224267191476953089");
		dataStore.insertURLDetails("olympics2012");
	}


}
