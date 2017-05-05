package crawler.twitter.testes;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;



public class Tw {

	static String AccessToken = "1688957917-mvEZaCmkCFmcASNbPpjmCNbr1XfWH2wMEdCdj6S";
	static String AccessSecret = "S5u2qf7WdA9TZwXLzACkswJeEnAZCv5JbaQFyfTlU";
	static String ConsumerKey = "KnVPCehUNXd4cDc4IxchYw";
	static String ConsumerSecret = "6tYZYHIRn2eWHmF34BD7PAJ4qFRD7efcpsdFgLcyA";

	public static void main(String[] args) throws Exception {
		OAuthConsumer consumer = new CommonsHttpOAuthConsumer(
				ConsumerKey,
				ConsumerSecret);

		consumer.setTokenWithSecret(AccessToken, AccessSecret);
		HttpGet request = new HttpGet("https://api.twitter.com/1.1/users/search.json?q=cocacola");
		consumer.sign(request);

		HttpClient client = new DefaultHttpClient();
		HttpResponse response = client.execute(request);

		int statusCode = response.getStatusLine().getStatusCode();
		System.out.println(statusCode + ":" + response.getStatusLine().getReasonPhrase());
		System.out.println(IOUtils.toString(response.getEntity().getContent()));
	}
}
