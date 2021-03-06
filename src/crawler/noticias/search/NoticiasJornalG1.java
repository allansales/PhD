package crawler.noticias.search;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

import crawler.KeyFinder;
import crawler.Utiles;
import crawler.db.MongoDB;
import crawler.noticias.Noticia;
import crawler.noticias.Comentario;

public class NoticiasJornalG1 extends Noticia {

	//private static final String URL_G1 = "http://g1.globo.com/dynamo/plantao/politica/";
	private static final String URL_G1 = "http://falkor-cda.bastian.globo.com/feeds/8d7daa58-07fd-45c9-b1fe-aaa654957850/posts/page/";
	
	private static int NUM_PAGINA = 1;
	private static int POSICAO_BOXCOMENTARIOS = 15;
	
	private DB stocks = null;
	private DBCollection mongoCollectionNoticias = null;
	private DBCollection mongoCollectionComentarios = null;

	public NoticiasJornalG1() {}

	public NoticiasJornalG1(long timestamp, String subFonte,
			String titulo, String subTitulo, String conteudo, 
			String emissor, String url, String repercussao, String idNoticia) {

		super(timestamp, subFonte, titulo, subTitulo, conteudo, emissor, url, repercussao, idNoticia);
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

	public void insereInformacao(String dataInicial, String dataFinal) throws IOException, ParseException {
		
		stocks = MongoDB.getInstance();
		mongoCollectionNoticias = stocks.getCollection("testeN");
		mongoCollectionComentarios = stocks.getCollection("testeC");
		
		long unixTimesTampDataInicial = 0; 
		long unixTimesTampDataFinal = 0;

		unixTimesTampDataInicial = Utiles.dataToTimestamp(dataInicial, "0000");
		unixTimesTampDataFinal = Utiles.dataToTimestamp(dataFinal, "2359");

		//String url = URL_G1+NUM_PAGINA+".json";
		String url = URL_G1+NUM_PAGINA;
		
		Document pagina = obtemPaginaIgnoringType(url);
		
		System.out.println("url: "+url);
		
		while(pagina == null){
			pagina = obtemPagina(url);
		}
		
		JSONArray noticiasG1Pagina = getAttr(pagina.select("body").text(),"items");
		
		boolean limiteAlcancado =  verificaLimiteInformacao(noticiasG1Pagina, 
				unixTimesTampDataInicial, unixTimesTampDataFinal);
		
		while(!limiteAlcancado){
			NUM_PAGINA++;
			System.out.println("PAGINA "+NUM_PAGINA);
			pagina = obtemPagina(URL_G1+NUM_PAGINA+".json");
			while(pagina == null){
				pagina = obtemPagina(URL_G1+NUM_PAGINA+".json");
			}
			noticiasG1Pagina = getAttr(pagina.select("body").text(),"conteudos");
			limiteAlcancado =  verificaLimiteInformacao(noticiasG1Pagina, 
					unixTimesTampDataInicial, unixTimesTampDataFinal);
		}

	}

	public JSONArray getAttr(String json, String atributo){

		JSONParser parser = new JSONParser();
		Object obj = null;

		try {
			obj = parser.parse(json);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		JSONObject jsonObject = (JSONObject) obj;
		Iterator iterator = jsonObject.keySet().iterator();

		while(iterator.hasNext()){
			String key = (String) iterator.next();
			if(key.equals(atributo)){
				return (JSONArray) jsonObject.get(key);
			}
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public boolean verificaLimiteInformacao(JSONArray noticias, long unixTimesTampDataInicial,
			long unixTimesTampDataFinal) throws IOException, ParseException {

		if(!noticias.isEmpty()){
			
			if(unixTimesTampDataFinal == Utiles.ZERO){
				
				for (Object notic : noticias.toArray()) {
					List<Object> objetos = criaInformacao((JSONObject) notic);
					if(objetos == null){
						continue;
					}
					Noticia noticia = (Noticia) objetos.get(0);
					List<Comentario> comentarios = (List<Comentario>) objetos.get(1);
					
					if(noticia != null){
						if(noticia.getTimestamp() >= unixTimesTampDataInicial){
							//Se a lista de comentarios vier vazia, nao adiciona no BD							
							if(comentarios.size() != 0){
								mongoCollectionComentarios.insert(converterToMap(comentarios));	
							}
							mongoCollectionNoticias.insert(converterToMap(noticia));
						
						}else{
							return true;
						}
					}
				}

			}else{
				for (Object notic : noticias.toArray()) {

					List<Object> objetos = criaInformacao((JSONObject) notic);
					if(objetos == null){
						continue;
					}
					Noticia noticia = (Noticia) objetos.get(0);
					List<Comentario> comentarios = (List<Comentario>) objetos.get(1);
					if(noticia != null){
						if((noticia.getTimestamp() <= unixTimesTampDataFinal) && 
								(noticia.getTimestamp() >= unixTimesTampDataInicial)){
							//Se a lista de comentarios vier vazia, nao adiciona no BD
							if(comentarios.size() != 0){								
								mongoCollectionComentarios.insert(converterToMap(comentarios));	
							}
							mongoCollectionNoticias.insert(converterToMap(noticia));
						}else if(noticia.getTimestamp() < unixTimesTampDataInicial){
							return true;
						}	
					}
				}
				return false;
			}
		}

		return true;
	}

	public List<DBObject> converterToMap(List<Comentario> comentarios) {
		
		List<DBObject> comentarios_dbo = new ArrayList<DBObject>();
		
		for (Comentario comentario : comentarios) {
			DBObject comentario_dbo = new BasicDBObject();
			comentario_dbo.put("idNoticia", comentario.getIdNoticia());  
			comentario_dbo.put("comentario", comentario.getComentario());  
			comentario_dbo.put("idUsuario", comentario.getIdUsuario());  
			comentario_dbo.put("ThumbsUp", comentario.getVotosThumbsUp());  
			comentario_dbo.put("ThumbsDown", comentario.getVotosThumbsDown());
			comentario_dbo.put("n_respostas", comentario.getQuantidade_respostas());
			comentarios_dbo.add(comentario_dbo);
		}
		return comentarios_dbo;    	
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
		news.put("idNoticia", noticia.getIdNoticia());
		
		return news;    
	}
	
	public List<String> getElementByRegex(String regex, String jquery){
		List<String> padroes = new ArrayList<String>();
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(jquery);
		while(m.find()) {
		    padroes.add(m.group(1));
		}
		return padroes;
	}
	

	public List<Object> criaInformacao(JSONObject data) throws ParseException{
		
		JSONObject content = (JSONObject) data.get("content");
		data = content;
		
		//informacoes da noticia
		String titulo = "";
		String subTitulo = "";
		String emissor = "";
		String conteudo = "";
		int repercussao = 0;
		String idNoticia = "";
		Document doc = null;
		long timestamp = 0;
		String url = "";
		List<String> elementos = new ArrayList<String>();

		try{
			url = data.get("url").toString();
			titulo = data.get("title").toString();
			subTitulo = "";

			if(data.get("summary")!= null){
				subTitulo = data.get("summary").toString();
			}
			
			System.out.println("\t -"+titulo);

			doc = obtemPagina(url);						

			int tentativas = 0;
			while((doc == null) && (tentativas <= 5)){
				doc = obtemPagina(url);
				tentativas++;	
			}

			if(doc == null){
				return null;
			}

			String momento = doc.select("body").first().attr("data-generated-at");
//			if(momento.isEmpty()){
//				momento = doc.select(".data-criacao").text();
//				if(momento.isEmpty()){
//					return null;
//				}
//			}

			String dataHora[] = momento.split(" ");

			timestamp = Utiles.dataToTimestamp(dataHora[0], dataHora[1].replace(":", ""));	

			emissor = doc.select(".content-publication-data__from").text();
			
			conteudo = doc.getElementsByAttributeValue("itemprop","articleBody").text();
			
			conteudo = conteudo.replace("|", "");
			conteudo = conteudo.replace("\"", "");
			conteudo = conteudo.replace("\'", "");

			if(doc.toString().contains("SETTINGS")){
				String jquery = doc.getElementById("SETTINGS").toString();

				if(doc.getElementById("boxComentarios") == null){
					repercussao = 0;
				}
				
				
				String[] trash_and_json = jquery.split("cdaaas.SETTINGS = ");
				String settings = trash_and_json[1].split(";")[0]		;		
//						.replaceAll(": ","\":")
//						.replaceAll("\\s{2,10}","\"")
//						.replace("\"}","}");
						//.replaceAll("\\s+","")
						//.replaceAll("\"\"", "\"");

				System.out.println(settings);
				
				JSONParser parser = new JSONParser();
				JSONObject json_settings = (JSONObject) parser.parse(settings); //refatora a string e transforma em json
				System.out.println(json_settings);
				
//				jquery = jquery.replace("\n", " ").replace("\r", "");
//				
//				List<String> elementos_1 = getElementByRegex("\\'(.*?)\\'", jquery); //Utiliza o que estiver entre aspas simples
//				List<String> elementos_2 = getElementByRegex("\"([^\"]*)\"", jquery); //Utiliza o que estiver entre aspas duplas
//
//				elementos.addAll(elementos_1);
//				elementos.addAll(elementos_2);
//
//				System.out.println("elementos: "+elementos);
//				
//				if(!elementos.get(1).contains("/jornalismo/g1/politica")){ //Busca o uri e verifica se pertence ao caderno de politica
//					System.out.println("entrei aqui");
//					return null;
//				}
			}
			
			repercussao = calculaRepercussao(titulo, elementos);
			//repercussao = 0;
			//idNoticia = "G1-"+elementos.get(4);
			idNoticia = "G1-0";
			
		} catch (Exception e){
			Utiles.writeFile(doc, titulo);
			System.out.println(e);
			System.out.println("Não consigo buscar informacoes da noticia");
			return null;
		}

		List<Comentario> comentarios = new ArrayList<Comentario>();
//		try{
//			int n_json = 0;
//			while(repercussao > comentarios.size()){
//				n_json++;
//
//				JSONArray item = getItens(titulo, n_json, elementos);
//				if(item.size()==0){
//					break;
//				}
//				comentarios.addAll(getComentarios(item, idNoticia));
//
//			}	
//		} catch (Exception e){
//			System.out.println("Não tem espaço para comentários nessa página ou a página veio com erro.");	
//		}
		
		Noticia noticia = new NoticiasJornalG1(timestamp, "G1", titulo, subTitulo, conteudo, emissor, url, String.valueOf(repercussao), idNoticia);
		List<Object> retorno = new ArrayList<Object>();
		retorno.add(noticia);
		retorno.add(comentarios);

		return retorno;

	}

	public List<Object> criaInformacaoAntigas(JSONObject data) throws ParseException{
		
		JSONObject content = (JSONObject) data.get("content");
		data = content;
		
		//informacoes da noticia
		String titulo = "";
		String subTitulo = "";
		String emissor = "";
		String conteudo = "";
		int repercussao = 0;
		String idNoticia = "";
		Document doc = null;
		long timestamp = 0;
		String url = "";
		List<String> elementos = new ArrayList<String>();

		try{
			url = data.get("permalink").toString();
			titulo = data.get("titulo").toString();
			subTitulo = "";

			if(data.get("subtitulo")!= null){
				subTitulo = data.get("subtitulo").toString();
			}
			
			System.out.println("\t -"+titulo);

			doc = obtemPagina(url);
			String jquery = doc.getElementsByAttributeValue("type", "text/javascript").get(POSICAO_BOXCOMENTARIOS).toString();

			if(!jquery.contains("#boxComentarios")){
				return null;
			}
			jquery = jquery.replace("\n", " ").replace("\r", "");

			List<String> elementos_1 = getElementByRegex("\\'(.*?)\\'", jquery); //Utiliza o que estiver entre aspas simples
			List<String> elementos_2 = getElementByRegex("\"([^\"]*)\"", jquery); //Utiliza o que estiver entre aspas duplas

			elementos.addAll(elementos_1);
			elementos.addAll(elementos_2);
			

			if(!elementos.get(1).contains("/jornalismo/g1/politica")){ //Busca o uri e verifica se pertence ao caderno de politica
				return null;
			}

			int tentativas = 0;
			while((doc == null) && (tentativas <= 5)){
				doc = obtemPagina(url);
				tentativas++;	
			}

			if(doc == null){
				return null;
			}

			String momento = doc.select(".materia-cabecalho .published").text();
			if(momento.isEmpty()){
				momento = doc.select(".data-criacao").text();
				if(momento.isEmpty()){
					return null;
				}
			}

			String dataHora[] = momento.split(" ");
			timestamp = Utiles.dataToTimestamp(dataHora[0], dataHora[1].replace("h", ""));	

			emissor = doc.select(".fn").text() +" "+doc.select(".locality").text();

			conteudo = doc.select(".entry-content p").text();

			conteudo = conteudo.replace("|", "");
			conteudo = conteudo.replace("\"", "");
			conteudo = conteudo.replace("\'", "");

			repercussao = calculaRepercussao(titulo, elementos);
			System.out.println("repercussao: "+repercussao);
			idNoticia = "G1-"+elementos.get(4);
			
			
		} catch (Exception e){
			Utiles.writeFile(doc, titulo);
			System.out.println("Não consigo buscar informacoes da noticia");
			return null;
		}

		List<Comentario> comentarios = new ArrayList<Comentario>();
		try{
			int n_json = 0;
			while(repercussao > comentarios.size()){
				n_json++;

				JSONArray item = getItens(titulo, n_json, elementos);
				if(item.size()==0){
					break;
				}
				comentarios.addAll(getComentarios(item, idNoticia));

			}	
		} catch (Exception e){
			System.out.println("Não tem espaço para comentários nessa página ou a página veio com erro.");	
		}
		
		Noticia noticia = new NoticiasJornalG1(timestamp, "G1", titulo, subTitulo, conteudo, emissor, url, String.valueOf(repercussao), idNoticia);

		List<Object> retorno = new ArrayList<Object>();
		retorno.add(noticia);
		retorno.add(comentarios);

		return retorno;

	}

	@SuppressWarnings("deprecation")
	public int calculaRepercussao(String titulo, List<String> elementos){		
		
		String uri_jq = elementos.get(1);
		String url_jq = elementos.get(2);
		String titulo_jq = elementos.get(3);
		String idExterno_jq = elementos.get(4);
		String shortUrl_jq = elementos.get(7);
		
		final String n_comentariosPage = "http://comentarios.globo.com/comentarios/" + uri_jq.replace("/","%40%40") + "/" + idExterno_jq + "/" + url_jq.replace(":","%3A").replace("/","%40%40") + "/" + shortUrl_jq.replace(":","%3A").replace("/","%40%40") + "/" + URLEncoder.encode(titulo_jq.trim()) +"/numero";
		int n_comentarios = getCount(n_comentariosPage, "numeroDeComentarios");			
		
		return n_comentarios;
	}

	
	@SuppressWarnings("deprecation")
	public int calculaRepercussaoAntiga(String titulo, List<String> elementos){		
		
		String uri_jq = elementos.get(1);
		String url_jq = elementos.get(2);
		String titulo_jq = elementos.get(3);
		String idExterno_jq = elementos.get(4);
		String shortUrl_jq = elementos.get(7);
		
		final String n_comentariosPage = "http://comentarios.globo.com/comentarios/" + uri_jq.replace("/","%40%40") + "/" + idExterno_jq + "/" + url_jq.replace(":","%3A").replace("/","%40%40") + "/" + shortUrl_jq.replace(":","%3A").replace("/","%40%40") + "/" + URLEncoder.encode(titulo_jq.trim()) +"/numero";
		int n_comentarios = getCount(n_comentariosPage, "numeroDeComentarios");			
		
		return n_comentarios;
	}
	
	@SuppressWarnings("deprecation")
	public JSONArray getItens(String titulo, int n_json, List<String> elementos) throws ParseException {		
		
		String uri_jq = elementos.get(1);
		String url_jq = elementos.get(2);
		String titulo_jq = elementos.get(3);
		String idExterno_jq = elementos.get(4);
		String shortUrl_jq = elementos.get(7);
		
		String comentariosPage = "http://comentarios.globo.com/comentarios/" + uri_jq.replace("/","%40%40") + "/" + idExterno_jq + "/" + url_jq.replace(":","%3A").replace("/","%40%40") + "/" + shortUrl_jq.replace(":","%3A").replace("/","%40%40") + "/" + URLEncoder.encode(titulo_jq.trim()) + "/" + n_json + ".json";
		Document pagina = obtemPaginaIgnoringType(comentariosPage);
		while(pagina == null){
			pagina = obtemPaginaIgnoringType(comentariosPage);
		}
				
		String json = pagina.select("body").text();
		json = json.substring(28, json.length()-1);
				
		JSONParser parser = new JSONParser();
		JSONObject json_comentarios = (JSONObject) parser.parse(json);
		JSONArray itens = (JSONArray) json_comentarios.get("itens");
		
		return itens;
	}
	
	public List<Comentario> getComentarios(JSONArray itens, String idNoticia) throws ParseException {		
				
		List<Comentario> comentarios = new ArrayList<Comentario>();
		
		for (int i = 0; i < itens.size(); ++i) {
			
			JSONObject item = (JSONObject) itens.get(i);
			//adiciona os comentarios da noticia na lista
			Comentario comment = getComentarioInfo(item, false);
			comment.setIdNoticia(idNoticia);
			comentarios.add(comment);

			//adiciona as respostas do comentario na lista
			if(Integer.parseInt(comment.getQuantidade_respostas())>0){
				JSONArray replies = (JSONArray) item.get("replies");
				for (int j = 0; j < replies.size(); ++j) {
					JSONObject reply = (JSONObject) replies.get(j);
					Comentario resposta = getComentarioInfo(reply, true);
					resposta.setIdNoticia(idNoticia);
					comentarios.add(resposta);
				}	
			}
		}
		
		return comentarios;
	}

	public Comentario getComentarioInfo(JSONObject item, boolean isReply){
		
		Comentario comment = new Comentario();
		
		JSONObject json_usuario = (JSONObject) item.get("Usuario");
		comment.setIdUsuario(json_usuario.get("usuarioId").toString());
		
		comment.setComentario(item.get("texto").toString());
		comment.setVotosThumbsUp(item.get("VotosThumbsUp").toString());
		comment.setVotosThumbsDown(item.get("VotosThumbsDown").toString());
		
		comment.setQuantidade_respostas("isReply");
		if(!isReply){
			comment.setQuantidade_respostas(item.get("qtd_replies").toString());	
		}

		return comment;
	}
	
	public int getCount(String url, String atributo){
		
		Document pagina = obtemPaginaIgnoringType(url);
		//se a pagina nao tiver espaco para comentario, insere no banco com repercussao 0
		if(pagina == null){
			return 0;
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
	
	public static void main(String args[]) throws IOException, ParseException{

		String searchDateStart= "01/07/2010";
		//String searchDateFinish="24/05/2017";
		String searchDateFinish="27/07/2017";
		NoticiasJornalG1 n = new NoticiasJornalG1();
		n.insereInformacao(searchDateStart, searchDateFinish);
		
	}

}