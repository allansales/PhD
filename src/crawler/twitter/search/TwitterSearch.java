package crawler.twitter.search;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import br.com.caelum.vraptor.Resource;

import com.google.gson.Gson;

import crawler.Informacao;
import crawler.Utiles;
import crawler.twitter.TweetsJson;
import dao.InformacoesDAO;


@Resource
public class TwitterSearch{

	private final int TIME = 120000;
	private final int ERRO_REPERCUSSAO = -1;

	private InformacoesDAO twitterDAO;

	public InformacoesDAO getTwitterDAO() {
		return twitterDAO;
	}


	public void setTwitterDAO(InformacoesDAO twitterDAO) {
		this.twitterDAO = twitterDAO;
	}


	public void insereInformacao(String dataInicial, String dataFinal, String sobreTipo, String consulta, InformacoesDAO infDAO){

		setTwitterDAO(infDAO);
		long unixTimesTampDataInicial = 0; 
		long unixTimesTampDataFinal = 0;

		//unixTimesTampDataInicial = Utiles.dataToTimestamp(dataInicial, "0000");
		//unixTimesTampDataFinal = Utiles.dataToTimestamp(dataFinal, "2359");

		//String consulta_utf8 = Utiles.encondeConsulta(consulta);
		Document paginaInicial = obtemPaginaInicial(consulta, dataInicial, dataFinal);

		while(paginaInicial == null){
			paginaInicial = obtemPaginaInicial(consulta, dataInicial, dataFinal);
		}

		String dataScrollCursor = obtemdataScrollCursor(paginaInicial);
		Elements twts = obtemElementosTweets(paginaInicial);
		System.out.println("Empresa:"+consulta+" Qtd twitters:"+twts.size());

		verificaLimiteInformacao(twts, sobreTipo, consulta);

		Gson gson = new Gson();
		boolean hasMoreItem = true;

		if("TWEET--".equals(dataScrollCursor)){
			hasMoreItem = false;
		}

		while(hasMoreItem){

			BufferedReader in = obtemPaginasConsecutivas(consulta, dataInicial, dataFinal, dataScrollCursor);
			TweetsJson twets = null;

			while(in == null){

				in = obtemPaginasConsecutivas(consulta, dataInicial, dataFinal, dataScrollCursor);
			}

			try{
				twets = gson.fromJson(in, TweetsJson.class);
			}catch(Exception e){
				continue;
			}
			if("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n".equals(twets.getItems_html())){
				hasMoreItem = false;
			}else{
				Element e = paginaInicial.createElement("novaRequisicao");
				e.html(twets.getItems_html());
				twts = obtemElementosTweets(e);
				System.out.println("Qtd novos tweets:"+twts.size());
				dataScrollCursor = twets.getScroll_cursor();
				verificaLimiteInformacao(twts,sobreTipo, consulta);
			}

		}
	}


	private BufferedReader obtemPaginasConsecutivas(String consulta, String dataInicial, String dataFinal, String dataScrollCursor){

		System.out.println("obtemPaginasConsecutivas");

		URLConnection tweetPage = null;
		BufferedReader in = null;

		try {
			String consulta_utf8 = Utiles.encondeConsulta(consulta);
			tweetPage = new URL("https://twitter.com/i/search/timeline?q=%22"+consulta_utf8+"%22%20since%3A"+dataInicial+"%20until:"+dataFinal+"&src=typd&include_available_features=1&include_entities=1&scroll_cursor="+dataScrollCursor)
			.openConnection();

			tweetPage.setConnectTimeout(TIME);
			tweetPage.setReadTimeout(TIME);	
			System.out.println(tweetPage.getURL());

			in = new BufferedReader(
					new InputStreamReader(tweetPage.getInputStream()));
		}catch (Exception e) {
			System.out.println("catch obtemPaginasConsecutivas");
			return null;
		}

		return in;

	}

	public Document obtemPaginaInicial(String consulta, String dataInicial, String dataFinal){

		System.out.println("obtemPaginaInicial");
		Connection.Response res;
		Document paginaInicial = null;

		try {
			res = Jsoup.connect("https://twitter.com/search?q=\""+consulta+"\" since:"+dataInicial+" until:"+dataFinal+"&src=typd")
					.method(Method.GET).timeout(TIME)
					.execute();
			paginaInicial = res.parse();
			System.out.println("https://twitter.com/search?q=\""+consulta+"\" since:"+dataInicial+" until:"+dataFinal+"&src=typd");

		}catch(Exception e){
			System.out.println("catch obtemPaginaInicial");

			return null;

		}

		return paginaInicial;

	}

	private String obtemdataScrollCursor(Document paginaInicial){
		Element element = paginaInicial.select(".stream-container ").first();
		String dataScrollCursor = element.attr("data-scroll-cursor");
		return dataScrollCursor;

	}

	private Elements obtemElementosTweets(Element paginaInicial){

		return paginaInicial.select(".content");
	}

	private int getRetweets(String id){
		System.out.println("getRetweets");
		URL ajaxPage = null;
		final int QTD_LETRAS_ANTES_DO_NUMERO = 12;
		String retw = "Retweetado ";
		int retweet = 0;

		String link = "https://twitter.com/i/expanded/batch/"+id+"?facepile_max=1&include[]=social_proof&page_context=search&section_context=universal_all";

		try{
			ajaxPage = new URL(link);

			BufferedReader in = new BufferedReader(
					new InputStreamReader(ajaxPage.openStream()));
			String inf = in.readLine();
			int inicio = inf.lastIndexOf(retw);
			if(inicio>0){
				retw = inf.substring(inicio, inicio+QTD_LETRAS_ANTES_DO_NUMERO);
				retw = retw.split(" ")[1];
				retweet = Integer.parseInt(retw);
			}

		}catch(Exception e){
			return ERRO_REPERCUSSAO;
		}
		return retweet;
	} 


	public Informacao criaInformacao(Element element,String tipo, String consulta){
		Element itemHeader = element.select(".stream-item-header").first();
		Element itemTime = element.select(".time a").first();

		String idUsuario = itemHeader.select("a").attr("data-user-id");
		String referenciaUsuario = itemHeader.select("a").attr("href");
		String nomeUtilizadoPeloUsario = itemHeader.select("strong").text();

		String imagem = itemHeader.select("img").attr("src");
		String citacaoTwitter = "@"+itemHeader.select("span b").text();

		String momentoFormatado = itemTime.attr("title");
		String referenciaTweet = itemTime.attr("href");
		String shortTimesTamp = itemTime.select("span").attr("data-time");

		String textoTweet = element.select("p").text();

		String retweet = element.select(".tweet-timestamp").attr("href");
		int status = retweet.lastIndexOf("/");
		retweet = retweet.substring(status+1, retweet.length());
		int repercussao = getRetweets(retweet);

		while(repercussao == ERRO_REPERCUSSAO){
			repercussao = getRetweets(retweet);
		}

		Informacao t = new Informacao (Long.valueOf(shortTimesTamp), textoTweet, referenciaTweet, 
				"MÍDIAS SOCIAIS", "TWITTER",  tipo,  consulta,  citacaoTwitter, imagem, repercussao);
		return t;
	}

	public void verificaLimiteInformacao(Elements twts, String sobreTipo, String consulta){
		Informacao tw = null;
		for (Element el : twts) {
			tw = criaInformacao(el,sobreTipo, consulta);
			getTwitterDAO().inserir(tw);
		}
	}

	public boolean verificaLimiteInformacao(Elements twts,
			long unixTimesTampDataInicial, long unixTimesTampDataFinal, String sobreTipo, String consulta ){

		Informacao tw = null;
		if(!twts.isEmpty()){

			if(unixTimesTampDataFinal == Utiles.ZERO){
				for (Element el : twts) {
					tw = criaInformacao(el, sobreTipo, consulta);
					if(tw.getTimestamp() < unixTimesTampDataInicial ){
						return true;
					}
					getTwitterDAO().inserir(tw);
				}
				return false;

			}else{

				for (Element el : twts) {
					tw = criaInformacao(el,sobreTipo, consulta);
					if((tw.getTimestamp() <= unixTimesTampDataFinal) && 
							(tw.getTimestamp() >= unixTimesTampDataInicial)){
						getTwitterDAO().inserir(tw);
					}else if(tw.getTimestamp() < unixTimesTampDataInicial){
						return true;
					}	
				}
				return false;
			} 
		}
		return true;

	}
}
