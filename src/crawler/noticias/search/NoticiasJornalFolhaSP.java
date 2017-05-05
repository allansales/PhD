package crawler.noticias.search;

import java.io.IOException;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

import crawler.Utiles;
import crawler.db.MongoDB;
import crawler.noticias.Noticia;

public class NoticiasJornalFolhaSP extends Noticia {

	private static final String URL_FOLHASP = "http://search.folha.com.br/search?";
	private static int NUM_PAGINA = 1;
	private DBCollection mongoCollection = null;

	public NoticiasJornalFolhaSP(){}

	public NoticiasJornalFolhaSP(long timestamp, String subFonte,
			String titulo, String subTitulo, String conteudo, 
			String emissor, String url, String repercussao) {

		super(timestamp, subFonte, titulo, subTitulo, conteudo, emissor, url, repercussao);
	}

	public Document obtemPagina(String url){

		Connection.Response res;
		Document paginaInicial = null;

		try {
			res = Jsoup.connect(url)
					.method(Method.GET)
					.execute();
			paginaInicial = res.parse();

			//Alguns links estao vazios e nao podem ser encontrados
		} catch (HttpStatusException e) {

			return new Document("");

		} catch (IOException e) {

			return null;
		} 

		return paginaInicial;
	}

	public Document obtemPaginaIgnoringType(String url){
		Document countPage = null;
		try {
			countPage = Jsoup.connect(url).ignoreContentType(true).get();

		} catch (IOException e) {
			return null;
		}

		return countPage;

	}

	public void insereInformacao(String dataInicial, String dataFinal,
			String consulta) throws IOException {

		mongoCollection = MongoDB.getInstance();
		//gravarArq = new BufferedWriter(new FileWriter("FOLHADESAOPAULO.txt"));

		long unixTimesTampDataInicial = 0; 
		long unixTimesTampDataFinal = 0;

		unixTimesTampDataInicial = Utiles.dataToTimestamp(dataInicial, "0000");
		unixTimesTampDataFinal = Utiles.dataToTimestamp(dataFinal, "2359");

		consulta = "q="+consulta;
		String complemento = "&site=online/paineldoleitor";
		dataInicial = "&sd="+dataInicial;
		dataFinal = "&ed="+dataFinal;
		String paginaInicial = "&sr="+NUM_PAGINA;

		String url = URL_FOLHASP+consulta+complemento+dataInicial+dataFinal+paginaInicial;
		Document pagina = obtemPagina(url);

		while(pagina == null){
			pagina = obtemPagina(url);
		}

		Elements noticiasFolhaSPPagina = pagina.select(".search-results-list li");
		System.out.println("Noticias de 1 - "+noticiasFolhaSPPagina.size());
		NUM_PAGINA += noticiasFolhaSPPagina.size();

		boolean limiteAlcancado =  verificaLimiteInformacao(noticiasFolhaSPPagina, 
				unixTimesTampDataInicial, unixTimesTampDataFinal, consulta);

		while(!limiteAlcancado){
			paginaInicial = "&sr="+NUM_PAGINA;

			pagina = obtemPagina(URL_FOLHASP+consulta+complemento+dataInicial+dataFinal+paginaInicial);

			while(pagina == null){
				pagina = obtemPagina(URL_FOLHASP+consulta+complemento+dataInicial+dataFinal+paginaInicial);
			}

			noticiasFolhaSPPagina = pagina.select(".search-results-list li");
			long valorAntigo = NUM_PAGINA;
			NUM_PAGINA += noticiasFolhaSPPagina.size();
			System.out.println("NOTICIAS DE : "+valorAntigo+" - "+NUM_PAGINA);

			limiteAlcancado =  verificaLimiteInformacao(noticiasFolhaSPPagina, 
					unixTimesTampDataInicial, unixTimesTampDataFinal, consulta);


		}
		//gravarArq.close();

	}

	public boolean verificaLimiteInformacao(Elements noticias, long unixTimesTampDataInicial,
			long unixTimesTampDataFinal, String consulta) throws IOException {

		if(!noticias.isEmpty()){

			if(unixTimesTampDataFinal == Utiles.ZERO){

				for (Element noticia : noticias) {

					Noticia noticiaAtual = criaInformacao(noticia);
					if(noticiaAtual != null){
						long timesTampDia = noticiaAtual.getTimestamp();
						if(timesTampDia >= unixTimesTampDataInicial){
							mongoCollection.insert(converterToMap(noticiaAtual));
						}else{
							return true;
						}
					}

				}

			}else{

				for (Element noticia : noticias) {

					Noticia noticiaAtual = criaInformacao(noticia);

					if(noticiaAtual != null){
						long timesTampDia = noticiaAtual.getTimestamp();

						if((timesTampDia <= unixTimesTampDataFinal) && 
								(timesTampDia >= unixTimesTampDataInicial)){
							mongoCollection.insert(converterToMap(noticiaAtual));

						}else if(timesTampDia < unixTimesTampDataInicial){
							return true;
						}	
					}


				}
				return false;
			}
		}

		return true;
	}

	public DBObject converterToMap(Noticia noticia) {   
		//timestamp, subFonte, titulo, subTitulo, conteudo, emissor, url, repercussao
		DBObject news = new BasicDBObject();  
		news.put("timestamp", noticia.getTimestamp());  
		news.put("subFonte", noticia.getSubFonte());  
		news.put("titulo", noticia.getTitulo());  
		news.put("subTitulo", noticia.getSubTitulo());  
		news.put("conteudo", noticia.getConteudo());  
		news.put("emissor", noticia.getEmissor());  
		news.put("url", noticia.getUrl());  
		news.put("repercussao", noticia.getRepercussao());  

		return news;    
	}

	public Noticia criaInformacao(Element el){

		String url = el.select("a").attr("href");
		if (url == null || url.length() == 0){
			System.out.println(el);

		}else{

			Document doc = obtemPagina(url);
			
			if(doc != null){
				String corpo = doc.select("body").text();
				if(corpo.isEmpty()){
					url = doc.select("meta[http-equiv=Refresh]").attr("content");
					if(url.isEmpty()){
						System.out.println("url vazia...");
						return null;
					}
					url = url.split("=")[1];
					doc = null;
				}
			}
			
			int tentativas = 0;
			
			while((doc == null) && (tentativas <= 10)){
				
				doc = obtemPagina(url);
				
				if(doc != null){
					String corpo = doc.select("body").text();
					if(corpo.isEmpty()){
						url = doc.select("meta[http-equiv=Refresh]").attr("content");
						if(url.isEmpty()){
							System.out.println("url vazia...");
							return null;
						}
						url = url.split("=")[1];
						doc = obtemPagina(url);
					}
				}
				
				tentativas++;	
			}

			if(doc == null){
				return null;
			}

			String data = "";
			String hora = "";
			long timestamp = 0;
			String tempo = doc.select("time").text();

			if(!tempo.isEmpty()){
				
				data = tempo.split(" ")[0];
				hora = tempo.split(" ")[1];
				timestamp = Utiles.dataToTimestamp(data, hora.replace("h", ""));
			
			}else{
				
				tempo = doc.select("#articleDate").text();
				
				if(!tempo.isEmpty()){
					data = tempo.split("-")[0].trim();
					hora = tempo.split("-")[1].trim();
					timestamp = Utiles.dataToTimestamp(data, hora.replace("h", ""));		
				}else{
					System.out.println("Nao houve como capturar o tempo aqui...");
					return null; 
				}
			
			}

			String titulo = doc.select("article header h1").text();
			
			if(titulo.isEmpty()){
				titulo = doc.select("#articleNew h1").text();
			}
			
			//System.out.println("\t -"+titulo);

			if(titulo.contains("\"")){
				return null;
			}

			String emissor = doc.select(".author p").text();
			
			if(emissor.isEmpty()){
				emissor = doc.select("#articleBy p ").text();
			}

			String id = doc.select(".shortcut").attr("data-shortcut");
			id = id.substring(id.lastIndexOf("o")+1,id.length());
 
			System.out.println(doc);
			System.out.println("\t -"+titulo);
			
			Elements comments = doc.select("#article-comments");
			//System.out.println("comments");
			//System.out.println(comments);
			
			//String repercussao = calculaRepercussao(url,id);
			String repercussao = "0";
			String conteudo = doc.select("article .content p").text();
			
			
			if(conteudo.isEmpty()){
				conteudo = doc.select("#articleNew p").text();
			}

			return new NoticiasJornalFolhaSP(timestamp, "FOLHASP",
					titulo, "", conteudo, 
					emissor, url, repercussao);
		}

		return null;

	}

	public String calculaRepercussao(String url, String id){


		final String comentariosPage = "http://comentarios1.folha.com.br/comentarios.jsonp?callback=get_comments&service_name=Folha+Online&category_name=Mercado&external_id="+id+"&type=news&show_replies=false&show_with_alternate=false&link_format=html&order_by=plugin&callback=get_comments";
		int comentarios = getCount(comentariosPage, "total_comments");

		final String tweeterPage = "https://cdn.api.twitter.com/1/urls/count.json?url="+url+"&callback=jQuery11100053468162895262794_1425342668803&_=1425342668804";
		int tweeter = getCount(tweeterPage, "count");
		
		final String tweeterPage_2 = "http://urls.api.twitter.com/1/urls/count.json?callback=jQuery183004098643323709983_1426095974484&url="+url+"&_=1426095975713";
		int tweeter_2 = getCount(tweeterPage_2, "count");
	    
		try {
	    	
			new Thread().sleep(1000);
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			System.exit(0);
			e.printStackTrace();
		}
	    
		final String facebookPage = "https://graph.facebook.com/fql?q=SELECT%20total_count%20FROM%20link_stat%20WHERE%20url=%27"+url+"%27&callback=jQuery183079688022619946_1426192200188&_=1426192201535";
		int facebook = getCount(facebookPage, "total_count");

		final String facebookPage_2 = "http://graph.facebook.com/?callback=jQuery183004098643323709983_1426095974483&id="+url+"&_=1426095975709";
		int facebook_2 = getCount(facebookPage_2, "count");
		
		final String linkedInPage = "https://www.linkedin.com/countserv/count/share?format=jsonp&url="+url+"&callback=jQuery1110030292543070338573_1425849946154&_=1425849946155";
		int linkedIn = getCount(linkedInPage, "count");

		//final String googleplusPage = "http://economia.estadao.com.br/estadao/sharrre.php?url="+url+"&type=googlePlus";		
		//int googlePlus = getCount(googleplusPage, "count");

		int total = comentarios+tweeter+tweeter_2+facebook+facebook_2+linkedIn;
		System.out.println("c:"+comentarios+",t:"+(tweeter+tweeter_2)+",f:"+(facebook+facebook_2)+",l:"+linkedIn+",total:"+total);
		return "c:"+comentarios+",t:"+(tweeter+tweeter_2)+",f:"+(facebook+facebook_2)+",l:"+linkedIn+",total:"+total;
	}

	public int getCount(String url, String atributo){

		Document pagina = obtemPaginaIgnoringType(url);
		while(pagina == null){
			pagina = obtemPaginaIgnoringType(url);
		}

		String json = pagina.select("body").text();
		if(json.contains("\"error\"")){
			System.out.println("json:"+json);
			System.out.println("url:"+url);
			System.exit(0); 
		}

		if(json.contains("(")){
			json = json.substring(json.indexOf("(")+1, json.lastIndexOf(")"));
		}

		int count  = 0;
		JSONParser parser = new JSONParser();
		KeyFinder finder = new KeyFinder();
		finder.setMatchKey(atributo);
		try{
			while(!finder.isEnd()){
				parser.parse(json, finder, true);
				if(finder.isFound()){
					finder.setFound(false);
					try{
						count = ((Long)finder.getValue()).intValue();
					}catch(ClassCastException e){
						count = Integer.parseInt((String)finder.getValue());
					}
					return count;
				}
			}           
		}
		catch(ParseException pe){
			System.out.println("url:"+url);
			System.out.println("json: "+json);
			pe.printStackTrace();
			System.exit(0); 	 	
		}
		return count;
	}

	public static void main(String args[]) throws IOException{

		String searchDateStart= "01/01/2001";
		String searchDateFinish="25/04/2017";
		NoticiasJornalFolhaSP n = new NoticiasJornalFolhaSP();
		/*
		 * Na Folha de São Paulo o nome do cardeno é PODER ao invés de POLITICA
		 * como é normalmente conhecido. 
		 */
		n.insereInformacao(searchDateStart, searchDateFinish, "politica");

	}



}
