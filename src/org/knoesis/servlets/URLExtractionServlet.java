package org.knoesis.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URLDecoder;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.knoesis.url.extractions.URLDataStore;

import org.knoesis.url.extractions.URLInformationAssembler;
import org.knoesis.url.extractions.URLModel;

/**
 * Servlet implementation class URLExtractionServlet
 * 
 * @author pavan
 * 
 * The objective of this class is to return the information of a particular URL
 * 
 * Parameters that has to be sent
 * 1. Tweet text of the URL
 * 
 * Returns a JSON with
 * 1. Original URL
 * 2. Entities from the content of the URL (Body + Title) using dbpedia spotlight
 * 
 * TODO: I have extended the function to put it boolean values to also get the content but its just about getting the 
 * 		 parameters from the user
 */
@WebServlet("/URLExtractionServlet")
public class URLExtractionServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * Default constructor. 
	 */
	public URLExtractionServlet() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		processTweetText(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		processTweetText(request, response);
	}

	public static void processTweetText(HttpServletRequest request, HttpServletResponse response){
		String tweet = request.getParameter("tweet");
		boolean title = false;
		boolean html = false;
		boolean entity = true;
		boolean origURL = true;
		try {
			String urlDecodedTweet = URLDecoder.decode(tweet, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		URLModel urlModel = new URLModel(tweet);
		URLInformationAssembler assembler = new URLInformationAssembler();
		assembler.process(urlModel);
		try {
			PrintWriter out = response.getWriter();
			out.println(urlModel.jsonSerialize(title, html, entity, origURL));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		String tweet = "Five of our @PlanetaryRsrcs engineers designed and helped build the @MarsCuriosity rover. Here's their next mission: http://ow.ly/cXYSJ";  
		URLModel urlModel = new URLModel(tweet);
		URLInformationAssembler assembler = new URLInformationAssembler();
		assembler.process(urlModel);
		System.out.println(urlModel.jsonSerialize(true, false, true, true));
                
                URLDataStore ds = new URLDataStore();
        try {
            ds.getRawTweets();
            

        } catch (SQLException ex) {
            Logger.getLogger(URLExtractionServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
	}

}
