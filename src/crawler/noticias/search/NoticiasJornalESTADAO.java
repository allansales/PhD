package crawler.noticias.search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.Matcher;


import org.apache.commons.collections.map.AbstractHashedMap;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
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

public class NoticiasJornalESTADAO extends Noticia{

	private static final String URL_ESTADAO = "http://busca.estadao.com.br/modulos/busca-resultado?modulo=busca-resultado&config%5Bbusca%5D%5Bpage%5D=$NUMERO_DA_PAGINA$&config%5Bbusca%5D%5Bparams%5D=editoria%5B%5D%3Dpolitica%26q%3Dpolitica&ajax=1";
	private static final String COMENTARIOS_PAGE_BASE = "http://data.livefyre.com/bs3/v3.1/estadao.fyre.co/";
	
	private static int NUM_PAGINA = 1;

	private DB stocks = null;
	private DBCollection mongoCollectionNoticias = null;
	private DBCollection mongoCollectionComentarios = null;
	
	public NoticiasJornalESTADAO(){

	}

	public NoticiasJornalESTADAO(long timestamp, String subFonte,
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
		
		System.out.println("PAGINA: 1");
		boolean limiteAlcancado =  verificaLimiteInformacao(data, noticiasEstadaoPagina, 
				unixTimesTampDataInicial, unixTimesTampDataFinal, consulta);

		while(!limiteAlcancado){
			NUM_PAGINA++;
			System.out.println("PAGINA: "+NUM_PAGINA);
			pagina = obtemPagina(URL_ESTADAO.replace("$NUMERO_DA_PAGINA$", Integer.toString(NUM_PAGINA)));
			
			while(pagina == null){
				pagina = obtemPagina(URL_ESTADAO.replace("$NUMERO_DA_PAGINA$", Integer.toString(NUM_PAGINA)));
			}
			
			noticiasEstadaoPagina = pagina.select("a.link-title");
			tag_span = pagina.select("span.data-posts").get(0).text();
			data = extraiDataMateria(tag_span);
			System.out.println(data);
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
						List<Object> objetos = criaInformacao(data, dia, consulta);
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
					}else{
						return true;
					}
				}
			} else {
				for (Element dia : dias) {

					if((timesTampDia <= unixTimesTampDataFinal) && 
							(timesTampDia >= unixTimesTampDataInicial)){
						List<Object> objetos = criaInformacao(data, dia, consulta);
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
		news.put("idNoticia", noticia.getIdNoticia());

		return news;    
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
		
	public List<Object> criaInformacao(String data, Element el, String consulta){
		
		try{
			long timestamp = Utiles.dataToTimestamp(data, "0000");		

			String url = el.attr("href");
			Document doc = obtemPagina(url);

			int tentativas = 0;
			while((doc == null) && (tentativas < 50)){
				doc = obtemPagina(url);
				tentativas++;
			}
			
			//TODO retirar isso quando consertar os erros de null point e outofbounds
			if(doc == null){
				return null;
			}
			
			String titulo = doc.select("section[data-titulo]").attr("data-titulo");			
			if(titulo.isEmpty() || titulo == null){
				//Utiles.writeFile(doc, "vazio");
				return null;
			}

			System.out.println("\t -"+titulo);
			
			String subTitulo = doc.select("meta[name=description]").attr("content");
			String emissor = doc.select("section[data-credito]").attr("data-credito");;		
			String conteudo = doc.select(".n--noticia__content.content > p").text();
			
			conteudo = conteudo.replace("|", "");
			conteudo = conteudo.replace("\"", "");
			conteudo = conteudo.replace("\'", "");

			List<Object> json_ids = getJSONComentarios_ids(doc);
			String id_pagina = (String) json_ids.get(1);
			String idNoticia = "ESTADAO-"+id_pagina; //posicao do ID
			
			String site_id = (String) json_ids.get(2);
			
			//comentarios
			JSONObject json_pag_comentarios = (JSONObject) json_ids.get(0); //posicao do JSON
			
			String repercussao = calculaRepercussao(json_pag_comentarios);
			System.out.println("Repercussao: "+repercussao);
			
			List<Comentario> comentarios = new ArrayList<Comentario>();
			if(Integer.parseInt(repercussao)!=0){
				
				if (Integer.parseInt(repercussao)>0 && Integer.parseInt(repercussao)<=50) {
					comentarios.addAll(getComentarios(idNoticia, json_pag_comentarios));
				} else {
					int n_pagina = 0;
					while(comentarios.size()<Integer.parseInt(repercussao)){
						JSONObject json_maior_50 = requisitaJSON_maior50(id_pagina, site_id, n_pagina);
						comentarios.addAll(getComentarios(idNoticia, json_maior_50));
						n_pagina++;
					}
				}	
			}
			
			System.out.println("N° comentarios: "+comentarios.size());
			
			Noticia noticia = new NoticiasJornalESTADAO(timestamp, "ESTADAO",
					titulo, subTitulo, conteudo, 
					emissor, url, repercussao, idNoticia);
			
			List<Object> retorno = new ArrayList<Object>();
			retorno.add(noticia);
			retorno.add(comentarios);
			
			return retorno;

		} catch (Exception e){
			System.out.println("Não tem espaço para comentários nessa página ou a página veio com erro.");
			return null;
		}

	}
	
	private String calculaRepercussao(JSONObject json) {	
		JSONObject collectionSettings = (JSONObject) json.get("collectionSettings");
		String numComentarios = collectionSettings.get("numVisible").toString();
		return numComentarios;
	}

	private List<Object> getJSONComentarios_ids(Document doc) throws ParseException{
		
		List<Object> json_id = new ArrayList<Object>();
		
		String livefyre_cm = doc.select("[data-collection-meta]").attr("data-collection-meta");
		String site_id = doc.select("[data-collection-meta]").attr("data-site-id");
		
		String id1 = livefyre_cm.substring(85, 100)+"="; //o = eh o complemento da string das posicoes 85 e 100. posicoes para buscar o codigo dos comentarios no livefyre
		String id2 = livefyre_cm.substring(85, 101); //101 algumas noticias vem com um charactere a mais
		
		JSONObject json = null;
		try{
			json = requisitaJSON_menor50(id1, site_id);
		} catch (NullPointerException e1) {
				try{
					json = requisitaJSON_menor50(id2, site_id);	
				} catch (NullPointerException e2) {
					return null;
				}
				json_id.add(json);
				json_id.add(id2);
				json_id.add(site_id);
				return json_id;
		}
		json_id.add(json);
		json_id.add(id1);
		json_id.add(site_id);
		return json_id;
	}

	private JSONObject requisitaJSON_menor50(String codigo_pagina_comentarios, String site_id) throws ParseException {
		String comentarios_page = COMENTARIOS_PAGE_BASE+site_id+"/"+codigo_pagina_comentarios+"/init";
		String json = obtemPaginaIgnoringType(comentarios_page).text();
		JSONParser parser = new JSONParser();
		return (JSONObject) parser.parse(json);
	}
	
	private JSONObject requisitaJSON_maior50(String codigo_pagina_comentarios, String site_id, int n_pagina) throws ParseException{
		String comentarios_page = COMENTARIOS_PAGE_BASE+site_id+"/"+codigo_pagina_comentarios+"/"+n_pagina+".json";
		String json = obtemPaginaIgnoringType(comentarios_page).text();
		JSONParser parser = new JSONParser();
		return (JSONObject) parser.parse(json);
	}
	
	public List<Comentario> getComentarios(String idNoticia, JSONObject json) throws ParseException{
		List<Comentario> comentarios = new ArrayList<Comentario>();

		if(json.toString().contains("headDocument")){
			json = (JSONObject) json.get("headDocument");	
		}
		
		JSONArray content = (JSONArray) json.get("content");
		
		JSONArray vis1 = getVis1(content);
		Map<String, Integer> n_respostas = conta_numero_repostas(vis1);
		for (int i = 0; i < vis1.size(); i++) {
			JSONObject comentario_json = (JSONObject) vis1.get(i);
			Comentario comentario = montaComentario(idNoticia, (JSONObject) comentario_json.get("content"), n_respostas);
			comentarios.add(comentario);
		}
		return comentarios;
	}
	
	private JSONArray getVis1(JSONArray content) throws ParseException{
		String vis1 = "[";
		for (int i = 0; i < content.size(); i++) {
			JSONObject comentario_json = (JSONObject) content.get(i);
			if (comentario_json.get("vis").toString().equals("1")) {
				vis1+=comentario_json+",";
			}
		}
	
		vis1 = StringUtils.removeEnd(vis1, ",")+"]";

		JSONParser parser = new JSONParser();
		JSONArray json = (JSONArray) parser.parse(vis1);
		return json;
	}
	
	private Map<String, Integer> conta_numero_repostas(JSONArray content){
		Map<String, Integer> n_respostas = new HashMap<String, Integer>();
		Pattern p = Pattern.compile("ancestorId\":\"(\\d*)");
		Matcher m = p.matcher(content.toString());
		while (m.find()){
			String id = m.group(1);
			if(!n_respostas.containsKey(id)){
				n_respostas.put(id, 1);
			} else {
				int value = n_respostas.get(id);
				n_respostas.put(id, value+1);
				
			}
		}
		return n_respostas;
	}

	private Comentario montaComentario(String idNoticia, JSONObject comentario_json, Map<String, Integer> n_respostas){
		String texto = comentario_json.get("bodyHtml").toString();
		
		String idUsuario = comentario_json.get("authorId").toString();
		
		JSONObject annotations = (JSONObject) comentario_json.get("annotations");
		
		int votosThumbsUp = 0;
		if(annotations.toString().contains("likedBy")){
			votosThumbsUp = ((JSONArray) annotations.get("likedBy")).size();
		}
		
		String idPost = comentario_json.get("id").toString();
		
		String quantidade_respostas = "0";
		if (n_respostas.containsKey(idPost)) {
			quantidade_respostas = n_respostas.get(idPost).toString();
		}
		
		if(comentario_json.toString().contains("ancestorId")){
			quantidade_respostas = "isReply";
		}
		
		Comentario comentario = new Comentario(idNoticia, texto, idUsuario, Integer.toString(votosThumbsUp), "0", quantidade_respostas);
		return comentario;
	}

	public static void main(String args[]) throws IOException, ParseException{

		String searchDateStart= "01/07/2010";
		String searchDateFinish="22/05/2017";
		NoticiasJornalESTADAO n = new NoticiasJornalESTADAO();
		n.insereInformacao(searchDateStart, searchDateFinish, "politica");
			
	}

}
