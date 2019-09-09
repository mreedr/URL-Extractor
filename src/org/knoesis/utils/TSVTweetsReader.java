package org.knoesis.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import org.knoesis.url.extractions.APIURLResolver;
import org.knoesis.url.extractions.RegexURLExtractor;
import org.knoesis.url.extractions.URLDataStore;


/**
 * Class parses a TSV file <id, tweet> and processes 
 * the URLs mentioned accordingly.
 * 
 * @author pavan
 *
 */
public class TSVTweetsReader {

	private static String tweet;
	
	public void parseTweets(File file) throws IOException{
		Scanner readFile = new Scanner(file);
		while(readFile.hasNext()){
			String tweetContent = readFile.nextLine();
			if(tweetContent.matches("^[0-9]{18}.*")){
				if(tweet == null){
					tweet = tweetContent;
					continue;
				}
				extractURLWriteToFile(tweet);
				//writeSentimentTriplesToFile(atweet);
				tweet = tweetContent;
			}
			else{
				String[] content = tweetContent.split("\t");
				for(String s: content){
					if ((s != null) || (!s.equals(" ")))
						tweet += s +"\t";
					else
						tweet += " ";
				}
			}
		}
		//This writes the whole tweet to triples
		extractURLWriteToFile(tweet);
		//This writes only the sentiment triples 
		//writeSentimentTriplesToFile(atweet);
		readFile.close();
	}

	
	private void extractURLWriteToFile(String tweetString) {
		System.out.println(tweetString);
		RegexURLExtractor extractURL = new RegexURLExtractor();
		APIURLResolver resolver = new APIURLResolver();
		String[] content = tweetString.split("\t");
		URLDataStore dataStore = new URLDataStore();
		URL shortURL = extractURL.extract(content[1]);
		try{
		String originalUrl = resolver.extract(shortURL.toString());
		dataStore.insertURL(originalUrl, content[0]);
		}catch(NullPointerException e){
			//do nothing
		}
	}

	public static void main(String[] args) throws IOException, ClassNotFoundException {
		/*Scanner readFile = new Scanner(new File(fileName));
		while(readFile.hasNext()){
			String tweetContent = readFile.nextLine();
			if(tweetContent.matches("^[0-9]{10}.*")){
				if(tweet == null){
					tweet = tweetContent;
					continue;
				}

				String[] content = tweet.split("\t");
				tweetForAnnotation.setId(Long.parseLong(content[0]));
				tweetForAnnotation.setUser(content[1]);
				tweetForAnnotation.setText(content[3]);
				AnnotatedTweet aTweet = processor.process(tweetForAnnotation);
				List<Triple> triples = SparqlUpdateSerializer.tweetToTriples(aTweet);
				for(Triple t:triples)
					out.append(t.toString());
				System.out.println("\n");
				tweet = tweetContent;
			}
			else{
				String[] content = tweetContent.split("\t");
				for(String s: content){
					if ((s != null) || (!s.equals(" ")))
						tweet += s +"\t";
					else
						tweet += " ";
				}
			}
		}
		readFile.close();
		out.close();*/
		/**
		 * The above is a working copy of the code which has been 
		 * split into other functions
		 */
		/*
		 * The below code takes in the filenames and processes all the tweets 
		 *  and converts it into triples.
		 *  
		 *  
		 *  1. File for the output.
		 *  2. Entities TernaryIntervalSearchTree
		 *  3. Folder where the files are there to be processed
		 *  4. Entities file for the hash Map.
		 */
		TSVTweetsReader tsv = new TSVTweetsReader();
		File file = new File("test_olympics_urls");
		tsv.parseTweets(file);
		
	}

}
