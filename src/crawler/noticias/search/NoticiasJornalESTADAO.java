package crawler.noticias.search;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

import crawler.KeyFinder;
import crawler.Utiles;
import crawler.db.MongoDB;
import crawler.noticias.Noticia;

import crawler.Utiles;

public class NoticiasJornalESTADAO extends Noticia{

	private static final String URL_ESTADAO = "http://busca.estadao.com.br/modulos/busca-resultado?modulo=busca-resultado&config%5Bbusca%5D%5Bpage%5D=$NUMERO_DA_PAGINA$&config%5Bbusca%5D%5Bparams%5D=editoria%5B%5D%3Dpolitica%26q%3Dpolitica&ajax=1";
	private static int NUM_PAGINA = 1;

	private DB stocks = null;
	private DBCollection mongoCollectionNoticias = null;
	private DBCollection mongoCollectionComentarios = null;
	
	public NoticiasJornalESTADAO(){

	}

	public NoticiasJornalESTADAO(long timestamp, String subFonte,
			String titulo, String subTitulo, String conteudo, 
			String emissor, String url, String repercussao) {

		super(timestamp, subFonte, titulo, subTitulo, conteudo, emissor, url, repercussao);
	}

	public Document obtemPagina(String url){

		Connection.Response res;
		Document paginaInicial = null;

		try {

			res = Jsoup.connect(url).method(Method.GET).execute();
			paginaInicial = res.parse();

		} catch (HttpStatusException e) {

			return null;

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
	
	private String extraiDataMateria(String dataEHora){
		String[] dataEHora_array = dataEHora.split(" de ");
		String dia = dataEHora_array[0];
		String mes = Utiles.dataEscritaParaNumerica(dataEHora_array[1]);
		String ano = dataEHora_array[2].split(" | ")[0];
		
		return dia+"/"+mes+"/"+ano;
	}
	
	public void insereInformacao(String dataInicial, String dataFinal,
			String consulta) throws IOException {

		stocks = MongoDB.getInstance();
		mongoCollectionNoticias = stocks.getCollection("estadaoNoticias");
		mongoCollectionComentarios = stocks.getCollection("estadaoComentarios");

		long unixTimesTampDataInicial = 0; 
		long unixTimesTampDataFinal = 0;

		unixTimesTampDataInicial = Utiles.dataToTimestamp(dataInicial, "0000");
		unixTimesTampDataFinal = Utiles.dataToTimestamp(dataFinal, "2359");

		String url = URL_ESTADAO.replace("$NUMERO_DA_PAGINA$", Integer.toString(NUM_PAGINA));
		Document pagina = obtemPagina(url);

		while(pagina == null){
			pagina = obtemPagina(url);
		}
		
		Elements noticiasEstadaoPagina = pagina.select("a.link-title");
		String tag_span = pagina.select("span.data-posts").get(0).text();
		String data = extraiDataMateria(tag_span);

//		String link_materia = "";
//		long unixTimesTampDataAtual = 0;
//		for (int i = 0; i < tag_a.size(); i++) { //tamanho de data_materia sempre eh igual ao de tag_a
//			link_materia = tag_a.get(i).attr("href");
//			data = extraiDataMateria(tag_span.get(i).text());
//			unixTimesTampDataAtual = Utiles.dataToTimestamp(data, "0000");
//			
//			System.out.println(data + " " + link_materia);	
//		}
		
		boolean limiteAlcancado =  verificaLimiteInformacao(data, noticiasEstadaoPagina, 
				unixTimesTampDataInicial, unixTimesTampDataFinal, consulta);

		System.out.println("ACABEI AQUI");
		while(!limiteAlcancado){
			NUM_PAGINA++;
			System.out.println("PAGINA: "+NUM_PAGINA);
			pagina = obtemPagina(URL_ESTADAO.replace("$NUMERO_DA_PAGINA$", Integer.toString(NUM_PAGINA)));
			while(pagina == null){
				pagina = obtemPagina(URL_ESTADAO.replace("$NUMERO_DA_PAGINA$", Integer.toString(NUM_PAGINA)));
			}
			noticiasEstadaoPagina = pagina.select(".listaultimas ");
			data = noticiasEstadaoPagina.select(".listaultimasdata").text();
			noticiasEstadaoPagina = noticiasEstadaoPagina.select("ul .ultimas");
			limiteAlcancado =  verificaLimiteInformacao(data,noticiasEstadaoPagina, 
					unixTimesTampDataInicial, unixTimesTampDataFinal, consulta);
		}

	}

	public long timestampDoDia(String data){
		
		return Utiles.dataToTimestamp(data,"0000");

	}



	public boolean verificaLimiteInformacao(String data, Elements dias, long unixTimesTampDataInicial,
			long unixTimesTampDataFinal, String consulta) throws IOException {
		
		long timesTampDia = timestampDoDia(data);
		if(!dias.isEmpty()){
			
			if(unixTimesTampDataFinal == Utiles.ZERO){
				for (Element dia : dias) {
					if(timesTampDia >= unixTimesTampDataInicial){
						
						Noticia noticia = criaInformacao(data, dia, consulta);
						if(noticia != null){
							mongoCollectionNoticias.insert(converterToMap(noticia));
						}
					}else{
						return true;
					}
				}

			}else{

				for (Element dia : dias) {

					if((timesTampDia <= unixTimesTampDataFinal) && 
							(timesTampDia >= unixTimesTampDataInicial)){

						Noticia noticia = criaInformacao(data, dia, consulta);

						if(noticia != null){

							mongoCollectionNoticias.insert(converterToMap(noticia));
						}
					}else if(timesTampDia < unixTimesTampDataInicial){
						return true;
					}	

				}
				return false;
			}
		}

		return true;
	}

	public DBObject converterToMap(Noticia noticia) {   
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
	public Noticia criaInformacao(String data, Element el, String consulta){

		
		long timestamp = Utiles.dataToTimestamp(data, "0000");		

		String url = el.attr("href");
		Document doc = obtemPagina(url);

		int tentativas = 0;
		while((doc == null) && (tentativas < 50)){
			doc = obtemPagina(url);
			tentativas++;
		}
		
		String titulo = doc.select("section[data-titulo]").attr("data-titulo");
		System.out.println("\t -"+titulo);
		
		String subTitulo = doc.select("meta[name=description]").attr("content");
		String emissor = doc.select("section[data-credito]").attr("data-credito");;		
		String conteudo = doc.select(".n--noticia__content.content > p").text();
		
		conteudo = conteudo.replace("|", "");
		conteudo = conteudo.replace("\"", "");
		conteudo = conteudo.replace("\'", "");
		
		String guid = doc.select("#guid_noticia").attr("value");
		//String repercussao = calculaRepercussao(url, guid);
		String repercussao = "0";

		return new NoticiasJornalESTADAO(timestamp, "ESTADAO",
				titulo, subTitulo, conteudo, 
				emissor, url, repercussao);

	}

	public String calculaRepercussao(String url, String guid){


		final String comentariosPage = "http://economia.estadao.com.br/servicos/comentarios/contador/?guid[]="+guid;
		int comentarios = getCount(comentariosPage, "contador");

		final String tweeterPage = "https://cdn.api.twitter.com/1/urls/count.json?url="+url+"&callback=jQuery11100053468162895262794_1425342668803&_=1425342668804";
		int tweeter = getCount(tweeterPage, "count");
		
		final String tweeterPage_2 = "http://urls.api.twitter.com/1/urls/count.json?callback=jQuery183004098643323709983_1426095974484&url="+url+"&_=1426095975713";
		int tweeter_2 = getCount(tweeterPage_2, "count");

		final String facebookPage = "https://graph.facebook.com/fql?q=SELECT%20url,%20normalized_url,%20share_count,%20like_count,%20comment_count,%20total_count,commentsbox_count,%20comments_fbid,%20click_count%20FROM%20link_stat%20WHERE%20url=%27"+url+"%27&callback=jQuery11100053468162895262794_1425342668801&_=1425342668802";
		int facebook = getCount(facebookPage, "total_count");
		
		final String facebookPage_2 = "http://graph.facebook.com/?callback=jQuery183004098643323709983_1426095974483&id="+url+"&_=1426095975709";
		int facebook_2 = getCount(facebookPage_2, "count");
		
		final String linkedInPage = "https://www.linkedin.com/countserv/count/share?format=jsonp&url="+url+"&callback=jQuery11100053468162895262794_1425342668805&_=1425342668806";
		int linkedIn = getCount(linkedInPage, "count");
		
		final String googleplusPage = "http://economia.estadao.com.br/estadao/sharrre.php?url="+url+"&type=googlePlus";		
		int googlePlus = getCount(googleplusPage, "count");
		
		int total = comentarios+tweeter+tweeter_2+facebook+facebook_2+linkedIn+googlePlus;
		System.out.println("c:"+comentarios+",t:"+(tweeter+tweeter_2)+",f:"+(facebook+facebook_2)+",l:"+linkedIn+"g:"+googlePlus+",total:"+total);
		return "c:"+comentarios+",t:"+(tweeter+tweeter_2)+",f:"+(facebook+facebook_2)+",l:"+linkedIn+"g:"+googlePlus+",total:"+total;
	}

	public int getCount(String url, String atributo){
		
		Document pagina = obtemPaginaIgnoringType(url);
		while(pagina == null){
			pagina = obtemPaginaIgnoringType(url);
		}
		String json = pagina.select("body").text();
		
		if(json.contains("(")){
			json = json.substring(json.indexOf("(")+1, json.indexOf(")"));
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
					count = ((Long)finder.getValue()).intValue();
					return count;
				}
			}           
		}
		catch(ParseException pe){
			pe.printStackTrace();
		}
		return count;
	}



	public static void main(String args[]) throws IOException{

		String searchDateStart= "15/05/2017";
		String searchDateFinish="17/05/2017";
		NoticiasJornalESTADAO n = new NoticiasJornalESTADAO();
		n.insereInformacao(searchDateStart, searchDateFinish, "politica");
		

	}

}
