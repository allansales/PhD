package crawler.twitter.testes;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;


public class Twitter4j {

	public static void main(String args[]) throws TwitterException{
		
		 // The factory instance is re-useable and thread safe.
	    Twitter twitter = TwitterFactory.getSingleton();
	    Query query = new Query("LIGTH S.A.");
	    query.setSince("2010-01-01");
	    query.setUntil("2013-12-31");
	    QueryResult result = twitter.search(query);
	    for (Status status : result.getTweets()) {
	        System.out.println(status.getUser().getLocation() + ":" + status.getText());
	    }
	}
}
