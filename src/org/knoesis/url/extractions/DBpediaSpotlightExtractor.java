package org.knoesis.url.extractions;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.knoesis.url.extractions.*;


/**
 * This entity extractor uses DBpedia Spotlight's Web Service to spot and disambiguate entities.
 * The best solution would be to run your own DBpedia Spotlight instance locally, with a more focused index.
 * There are many advantages: 1) annotation is faster and better with smaller indexes 2) you don't need to rely on a university's network availability. 
 * 
 * @author pablomendes
 * @author Joachim Daiber (based on his client from spotlight.dbpedia.org)
 */
public class DBpediaSpotlightExtractor implements Extractor<Set<String>> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//TODO make these configurable from, perhaps, constructor
    private String API_URL = "http://spotlight.dbpedia.org/rest/annotate";
	private double CONFIDENCE = 0.3;
	private int SUPPORT = 30;
	private String DISAMBIGUATOR = "Document"; //other option is Occurrences
	
    public Logger LOG = Logger.getLogger(this.getClass());
    
    // Create an instance of HttpClient.
    private static HttpClient client = new HttpClient();

    public String request(HttpMethod method) throws AnnotationException {

        String response = null;

        // Provide custom retry handler is necessary
        method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
                new DefaultHttpMethodRetryHandler(3, false));

        try {
            // Execute the method.
            int statusCode = client.executeMethod(method);

            if (statusCode != HttpStatus.SC_OK) {
                LOG.error("Method failed: " + method.getStatusLine());
            }

            // Read the response body.
            byte[] responseBody = method.getResponseBody(); //TODO Going to buffer response body of large or unknown size. Using getResponseBodyAsStream instead is recommended.

            // Deal with the response.
            // Use caution: ensure correct character encoding and is not binary data
            response = new String(responseBody);

        } catch (HttpException e) {
            LOG.error("Fatal protocol violation: " + e.getMessage());
            throw new AnnotationException("Protocol error executing HTTP request.",e);
        } catch (IOException e) {
            LOG.error("Fatal transport error: " + e.getMessage());
            LOG.error(method.getQueryString());
            throw new AnnotationException("Transport error executing HTTP request.",e);
        } finally {
            // Release the connection.
            method.releaseConnection();
        }
        return response;

    }
    
    /**
     * 
     * TODO Removes URLs, usernames and hashtags from the tweet text.
     * @param tweetFullText
     * @return tweet text without URLs, usernames or hashtags
     */
    public String clean(String tweetFullText) {
    	String cleanTweetText = tweetFullText.replaceAll(RegexURLExtractor.urlRegex.pattern(), "");    	
//    	LOG.trace(tweetFullText.replaceAll(RegexURLExtractor.urlRegex.pattern(), ""));
//    	LOG.trace(tweetFullText.replaceAll(TagExtractor.tagRegex.pattern(), ""));
//    	LOG.trace(cleanTweetText);
    	//TODO cleanTweetText = cleanUsers(cleanTweetText);
    	return cleanTweetText;
    }
	
    @Override    
    public Set<String> extract(Object tweetContent) {
    	
    	String text = clean((String)tweetContent);
    	
        LOG.debug("Querying API.");
		String spotlightResponse = null;
		try {
			GetMethod getMethod = new GetMethod(API_URL + "/?" +
					"confidence=" + CONFIDENCE
					+ "&support=" + SUPPORT
					+ "&disambiguator=" + DISAMBIGUATOR
					+ "&text=" + URLEncoder.encode(text, "utf-8"));
			getMethod.addRequestHeader(new Header("Accept", "application/json"));

			spotlightResponse = request(getMethod);
		} catch (UnsupportedEncodingException e) {
			LOG.debug("Could not encode text.", e);
		} catch (AnnotationException e) {
			LOG.debug("Could not annotate text.", e);
		}

		assert spotlightResponse != null;

		JSONObject resultJSON = null;
		JSONArray entities = null;

		try {
			resultJSON = new JSONObject(spotlightResponse);
			entities = resultJSON.getJSONArray("Resources");
		} catch (JSONException e) {
			LOG.debug("Received invalid response from DBpedia Spotlight API.");
		}

		Set<String> resources = new HashSet<String>();
		if (entities==null || entities.length()==0)
			return resources;
		
		for(int i = 0; i < entities.length(); i++) {
			try {
				JSONObject entity = entities.getJSONObject(i);
				resources.add(entity.getString("@URI"));

			} catch (JSONException e) {
                LOG.error("JSON exception "+e);
            }

		}

		return resources;
	}
    

    public static void main(String[] args) throws Exception {


    	DBpediaSpotlightExtractor c = new DBpediaSpotlightExtractor();
    	String text = "Take Action: Stop Obama's Unconstitutional Appointments #tcot #freedomworks http://t.co/xGUa0aWi";
    	System.out.println(text);
    	System.out.println(c.extract(text));


    	//        SpotlightClient c = new SpotlightClient(api_key);
    	//        List<DBpediaResource> response = c.extract(new Text(text));
    	//        PrintWriter out = new PrintWriter(manualEvalDir+"AnnotationText-Spotlight.txt.set");
    	//        System.out.println(response);

    }

	@Override
	public void process(URLModel urlModel) {
		urlModel.setEntities(this.extract(urlModel.getHtmlContent() + " " +  urlModel.getHtmlTitle()));
		
	}



}
