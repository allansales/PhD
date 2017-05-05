package crawler.twitter.testes;

import java.io.IOException;
import java.util.Map;

import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;


public class Facebook {

	public static void main(String args[]) throws IOException{
		
		Connection.Response res = Jsoup.connect("https://twitter.com/search?q=abcdef&src=typd")
			    .data("default_persistent","0","lgnjs","1385487603","lgnrnd","094002_2fAj","locale","pt_BR","lsd","AVq1m-2n","timezone","180","email", "stocks.analytics@gmail.com", "pass", "analyticsufcg", "u_0_c", "Entrar" )
			    .method(Method.POST)
			    .execute();
		
	    Document doc = res.parse();
	    Map<String, String> cookies = res.cookies();
	   
	    System.out.println(doc);
	}
}
