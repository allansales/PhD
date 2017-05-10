package crawler.noticias.search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
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

import crawler.Utiles;
import crawler.db.MongoDB;
import crawler.noticias.Comentario;
import crawler.noticias.Noticia;

public class NoticiasJornalFolhaSP extends Noticia {

	private static final String URL_FOLHASP = "http://search.folha.com.br/search?";
	private static final String comentariosPage_1 = "http://comentarios1.folha.com.br/comentarios.jsonp?callback=get_comments&service_name=Folha+Online&category_name=Poder&external_id=";
	private static final String comentariosPage_2 = "&type=news&show_replies=false&show_with_alternate=false&link_format=html&order_by=plugin&callback=get_comments";
	
	private static int NUM_PAGINA = 1;
	
	private DB stocks = null;
	private DBCollection mongoCollectionNoticias = null;

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
			String consulta) throws IOException, ParseException {

		stocks = MongoDB.getInstance();
		mongoCollectionNoticias = stocks.getCollection("folhaNoticias");
		
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

	}

	public boolean verificaLimiteInformacao(Elements noticias, long unixTimesTampDataInicial,
			long unixTimesTampDataFinal, String consulta) throws IOException, ParseException {

		if(!noticias.isEmpty()){

			if(unixTimesTampDataFinal == Utiles.ZERO){

				for (Element noticia : noticias) {

					Noticia noticiaAtual = criaInformacao(noticia);
					if(noticiaAtual != null){
						long timesTampDia = noticiaAtual.getTimestamp();
						if(timesTampDia >= unixTimesTampDataInicial){
							mongoCollectionNoticias.insert(converterToMap(noticiaAtual));
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
							mongoCollectionNoticias.insert(converterToMap(noticiaAtual));

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
		//news.put("idNoticia", noticia.getIdNoticia());

		return news;    
	}

	public Noticia criaInformacao(Element el) throws ParseException{

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
			
			System.out.println("\t -"+titulo);

			if(titulo.contains("\"")){
				return null;
			}

			String emissor = doc.select(".author p").text();
			
			if(emissor.isEmpty()){
				emissor = doc.select("#articleBy p ").text();
			}

			String id = doc.select(".shortcut").attr("data-shortcut");
			id = id.substring(id.lastIndexOf("o")+1,id.length());
			
			int repercussao = calculaRepercussao(id);
			String conteudo = doc.select("article .content p").text();
			System.out.println("repercussao: "+repercussao);
			
			List<Comentario> comentarios = getComentarios(repercussao, id);
			System.out.println("tamanho da lista: "+comentarios.size());
			
			if(conteudo.isEmpty()){
				conteudo = doc.select("#articleNew p").text();
			}

			return new NoticiasJornalFolhaSP(timestamp, "FOLHASP",
					titulo, "", conteudo, 
					emissor, url, Integer.toString(repercussao));
		}

		return null;

	}
	
	public int calculaRepercussao(String id){
		String comentariosPage = comentariosPage_1+id+comentariosPage_2;
		int comentarios = getCount(comentariosPage, "total_comments");
		return comentarios;
	}

	private List<Comentario> getComentarios(int repercussao, String id) throws ParseException{
		String comentariosInfo = comentariosPage_1+id+comentariosPage_2;
		
		String json = getJson(comentariosInfo);
		JSONParser parser = new JSONParser();
		JSONObject json_comentarios = (JSONObject) parser.parse(json);

		JSONObject subjects = (JSONObject) json_comentarios.get("subject");
		String comentariosPageModel = subjects.get("subject_comments_url").toString()+"&sr=";
		List<Comentario> comentarios = extraiComentariosDoHTML(repercussao, comentariosPageModel);
		
		return comentarios;
	}
	
	
	//TODO adicionar idNoticia, parar o loop quando houver duas interacoes sem mudar o numero de comentarios e retornar do jeito que esta
	private List<Comentario> extraiComentariosDoHTML(int repercussao, String comentariosPageModel) {
		
		List<Comentario> lista_comentarios = new ArrayList<Comentario>();
		
		int n_comentario = 1;
		while(repercussao > lista_comentarios.size()){
			
			String comentariosPage = comentariosPageModel+n_comentario;

			System.out.println(comentariosPage);
			
			Document pagina = obtemPaginaIgnoringType(comentariosPage);
			while(pagina == null){
				pagina = obtemPaginaIgnoringType(comentariosPage);
			}
			
			Elements bloco_users_comentarios_html = pagina.select(".comment.comment_li");
			
			int n_comentarios_pagina = bloco_users_comentarios_html.size();
			
			for (int i = 0; i < n_comentarios_pagina; i++) {
				Element bloco_users_comentarios = bloco_users_comentarios_html.get(i);
				
				Elements usuarios = bloco_users_comentarios.getElementsByClass("comment-meta");
				Elements comentarios = bloco_users_comentarios.getElementsByClass("comment-body");
				
				for (int j = 0; j < usuarios.size(); j++) { //usuarios e comentarios tem sempre o mesmo tamanho
					
					Comentario comment = new Comentario();
					
					Element usuario = usuarios.get(j);
					
					comment.setIdUsuario(usuario.getElementsByTag("span").get(0).text());
					comment.setVotosThumbsUp(usuario.getElementsByClass("good").text());
					comment.setVotosThumbsDown(usuario.getElementsByClass("bad").text());
					
					String quantidade_respostas = "isReply"; 
					if(j==0){ //se eh o primeiro usuario 
						quantidade_respostas = Integer.toString(bloco_users_comentarios.getElementsByClass("comment_li").size()-1); //desconsidera o proprio comentario
					}
					comment.setQuantidade_respostas(quantidade_respostas);
					
					comment.setComentario(comentarios.get(j).getElementsByTag("p").get(0).text());
					
					lista_comentarios.add(comment);
				}
			}
			
			// Caso haja mais de uma pagina de comentarios, atualiza o link para ir para a proxima
			if(lista_comentarios.size()!=0){
				n_comentario += n_comentarios_pagina;		
			}
			
			System.out.println("tamanho lista: "+lista_comentarios.size());
	
		}
		
		//Utiles.writeFile(pagina, Integer.toString(lista_comentarios.size()));	
		
		return lista_comentarios;
	}

	private String getJson(String url){
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
		
		return json;
	}
	
	public int getCount(String url, String atributo){

		String json = getJson(url);

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

	public static void main(String args[]) throws IOException, ParseException{

		String searchDateStart= "01/07/2010";
		String searchDateFinish="08/05/2017";
		NoticiasJornalFolhaSP n = new NoticiasJornalFolhaSP();
		n.insereInformacao(searchDateStart, searchDateFinish, "poder");

	}



}