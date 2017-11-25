package de.uni_leipzig.digital_text_forensics.preprocessing;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONObject;

public class DBLPDataAccessor {
	private String basePubUrl = "http://dblp.org/search/publ/api?q=";
	
	private String getRequestUrl(String pubQuery){
		String requestUrl = this.basePubUrl + pubQuery.replace(" ", "\\$") +"&format=json";
		return requestUrl;
	}
	

	/**
	 * @param is 
	 * @return String json
	 */
	private String convertStreamToString(InputStream is) {

		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
		    while ((line = reader.readLine()) != null) {
		        sb.append(line + "\n");
		    }
		} catch (IOException e) {
		    e.printStackTrace();
		} finally {
		    try {
		        is.close();
		    } catch (IOException e) {
		        e.printStackTrace();
		    }
		}
		return sb.toString();
	}
	
	/** call dplb-api to get an article object
	 * @param pubQuery
	 * @return Article
	 */
	public Article getArticleObj(String pubQuery) throws java.net.UnknownHostException {

		String requestUrl = this.getRequestUrl(pubQuery);
		URL url = null;
		try {
			url = new URL(requestUrl);
		} catch (MalformedURLException e) {
		}
		HttpURLConnection con = null;
		try {
			con = (HttpURLConnection) url.openConnection();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		try {
			con.setRequestMethod("GET");
		} catch (ProtocolException e) {
			e.printStackTrace();
		}
		con.setRequestProperty("Accept", "text/javascript, application/javascript, application/ecmascript, application/x-ecmascript, */*; q=0.01");
		con.setRequestProperty("Accept-Language", "de,en-US;q=0.7,en;q=0.3");
		con.setRequestProperty("Accept-Encoding", "gzip, deflate");
		con.setRequestProperty("Host", "dblp.uni-trier.de");
		con.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:56.0) Gecko/20100101 Firefox/56.0");
		con.setRequestProperty("DNT", "1");
		con.setRequestProperty("Connection", "keep-alive");

		InputStream response = null;
		String jsonStrigResponse = null;
		try {
			response = con.getInputStream();
		} catch (IOException e) {
			System.out.println("weird title:\t"+pubQuery);
		} 
		try {
			if (con.getResponseCode() == 200 || con.getResponseCode() ==201){
			jsonStrigResponse = convertStreamToString(response);
			} else{
				return null;
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}

		// @todo more than one article! as result -> list
		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(jsonStrigResponse).getJSONObject("result").getJSONObject("hits").getJSONArray("hit").getJSONObject(0);
		} catch (org.json.JSONException e) {
			return null;
		}
		Article article = new Article();
		article.setTitle((String) jsonObject.getJSONObject("info").get("title"));
		article.setPublicationDate((String) jsonObject.getJSONObject("info").get("year"));
		article.setKey((String) jsonObject.getJSONObject("info").get("key")); // we don't need this.
		article.setScore((String) jsonObject.get("@score"));
		try {
			article.setAuthors(jsonObject.getJSONObject("info").getJSONObject("authors").getJSONArray("author"));
		} catch (org.json.JSONException e) {
			try{
				article.setAuthors((String)jsonObject.getJSONObject("info").getJSONObject("authors").get("author"));
			} catch(org.json.JSONException e2) {
				article.setAuthors("");
			}
		}
		
			// not always given.
		String doi = null;
		try {
			doi = (String) jsonObject.getJSONObject("info").get("doi");
		} catch (org.json.JSONException e){
			doi = "";
		}
		article.setDoi(doi);
		return article;		 
	}
	
}
