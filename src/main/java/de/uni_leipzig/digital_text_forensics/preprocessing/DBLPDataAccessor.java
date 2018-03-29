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

/**
* DBLPDataAccessor
* <p> Interface to DBLP-API
*  
* @author Tobias Wenzel
* 
*/
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
	
	/** <p> Calls DBLP-API to get an article object with Information.
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
			//System.out.println("weird title. probably different encoding.:\t"+pubQuery);
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

		JSONArray jsonResponseArray = null;
		try {
			//jsonObject = new JSONObject(jsonStrigResponse).getJSONObject("result").getJSONObject("hits").getJSONArray("hit").getJSONObject(0);
			jsonResponseArray = new JSONObject(jsonStrigResponse).getJSONObject("result").getJSONObject("hits").getJSONArray("hit");
		} catch (org.json.JSONException e) {
			return null;
		}
		Article article = new Article();
		int highestScore = 0, articleScore = 0;
		for (int i=0; i<jsonResponseArray.length(); i++){
			JSONObject jsonArticle = jsonResponseArray.getJSONObject(i);
			articleScore = Integer.parseInt((String) jsonArticle.get("@score"));
			//System.out.println(articleScore);
			if (articleScore > highestScore) {
				highestScore = articleScore;
				article.setTitle((String) jsonArticle.getJSONObject("info").get("title"));
				article.setPublicationDate((String) jsonArticle.getJSONObject("info").get("year"));
				//article.setKey((String) jsonArticle.getJSONObject("info").get("key")); // we don't need this.
				article.setScore(Integer.toString(articleScore));
				try {
					article.setAuthors(jsonArticle.getJSONObject("info").getJSONObject("authors").getJSONArray("author"));
				} catch (org.json.JSONException e) {
					try{
						article.setAuthorsString((String)jsonArticle.getJSONObject("info").getJSONObject("authors").get("author"));
					} catch(org.json.JSONException e2) {
						article.setAuthorsString("");
					}
				}
				// not always given.
				String doi = null;
				try {
					doi = (String) jsonArticle.getJSONObject("info").get("doi");
				} catch (org.json.JSONException e){
					doi = "";
				}
				article.setDoi(doi);
			}
		}
		


		return article;		 
	}
	
}
