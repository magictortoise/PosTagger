package com.gumgum.tagging;

import edu.stanford.nlp.tagger.maxent.*;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.*;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class PageTagger {

	MaxentTagger tagger =  new MaxentTagger("english-left3words-distsim.tagger");
	
	public String tagText(String input) {
		if (input == null || input.length() == 0) {
			return "";
		}		
		return tagger.tagString(input);
	}
	
	public String getText(String link) {
		return getHTMLContent(link);
	}
	
	private String getHTMLContent(String link) {
		String content = "";
		Reader r;
		if (link == null || link.length() == 0) {
			return content;
		}
		try {
			URL url = new URL(link);
			URLConnection conn = url.openConnection();
			Pattern p = Pattern.compile("text/html;\\s+charset=([^\\s]+)\\s*");
			Matcher m = p.matcher(conn.getContentType());
			/* If Content-Type doesn't match this pre-conception, choose default one. */
			String charset = m.matches() ? m.group(1) : "utf-8";
			r = new InputStreamReader(conn.getInputStream(), charset);
			BufferedReader br = new BufferedReader(r);
			StringBuilder sb = new StringBuilder();
			String line;
			while ( (line=br.readLine()) != null) {
				System.out.println(Jsoup.parse(line).text());
			      sb.append(Jsoup.parse(line).text());
			    }
			//content = Jsoup.parse(sb.toString()).text();
			content = sb.toString();
			r.close();
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			r = null;
		}
		return content;
	}
	
	private String parseHTMLcontent(String content) {
	    // Replace anything between script and remove some white space
		String scriptregex = "<(script|style)[^>]*>[^<]*</(script|style)>";
	    Pattern p1 = Pattern.compile(scriptregex,Pattern.CASE_INSENSITIVE);
	    Matcher m1 = p1.matcher(content);
	    int count = 0;
	    while (m1.find()) {
	      count++;
	    }
	    System.out.println("Removed " + count + " script & style tags");
	    // Replace any matches with nothing
	    content = m1.replaceAll("");
	    
	    // A Regex to match anything in between <>
	    // Reads as: Match a "<"
	    // Match one or more characters that are not ">"
	    // Match "<";
	    String tagregex = "<[^>]*>";
	    Pattern p2 = Pattern.compile(tagregex);
	    Matcher m2 = p2.matcher(content);
	    count = 0;
	    // Just counting all the tags first
	    while (m2.find()) {
	      count++;
	    }

	    // Replace any matches with nothing
	    content = m2.replaceAll("");
	    System.out.println("Removed " + count + " other tags.");
	    
	    String multiplenewlines = "(\\n{1,2})(\\s*\\n)+"; 
	    // Replace with the original one or two new lines
	    content = content.replaceAll(multiplenewlines,"$1");
	    
	    return content;

	}
	
	private String parseHTMLcontent2(String content) {
		return Jsoup.parse(content).text(); 
		
	}
	
	public static void main(String[] args) {

		//String link = "http://gumgum.com/";
		//String link = "http://www.popcrunch.com/jimmy-kimmel-engaged/";
		String link = "http://gumgum-public.s3.amazonaws.com/numbers.html";
		//String link = "http://www.windingroad.com/articles/reviews/quick-drive-2012-bmw-z4-sdrive28i/";

		PageTagger taggerTest = new PageTagger();
		String cleanContent = taggerTest.getText(link);
		String taggerContent = taggerTest.tagText(cleanContent);
		System.out.println(cleanContent);
		System.out.println(taggerContent);

	}

}
