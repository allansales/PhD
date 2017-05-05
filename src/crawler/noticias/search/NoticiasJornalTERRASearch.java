package crawler.noticias.search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import crawler.Informacao;
import crawler.Utiles;
import crawler.noticias.Noticia;

public class NoticiasJornalTERRASearch extends Noticia{

	

	private final String URL_TERRA = "http://economia.terra.com.br/ultimas/?&vgnpage=";
	private int NUM_PAGINA = 1;
	private List<Noticia>informacao = new ArrayList<Noticia>();
	
	public void insereInformacao(String dataInicial, String dataFinal,
			String consulta, String tipo) {

		long unixTimesTampDataInicial = 0; 
		long unixTimesTampDataFinal = 0;


		unixTimesTampDataInicial = Utiles.dataToTimestamp(dataInicial, "0000");
		unixTimesTampDataFinal = Utiles.dataToTimestamp(dataFinal, "2359");

		Document pagina = obtemPagina(URL_TERRA+pagina+NUM_PAGINA);
		List<Noticia> noticias = new ArrayList<Noticia>();

		//Obtem todos os dias da primeira pagina.
		Elements noticiasTerraPrimeiraPagina = pagina.select(".list");
		boolean limiteAlcancado =  verificaLimiteInformacao(noticiasTerraPrimeiraPagina, 
				noticias, unixTimesTampDataInicial, unixTimesTampDataFinal, tipo, consulta);

		while(!limiteAlcancado){
			NUM_PAGINA++;
			pagina = obtemPagina(NUM_PAGINA);
			noticiasTerraPrimeiraPagina = pagina.select(".list");
			limiteAlcancado =  verificaLimiteInformacao(noticiasTerraPrimeiraPagina, noticias, 
					unixTimesTampDataInicial, unixTimesTampDataFinal, tipo, consulta);
		}
	}

	private String padronizaFormatoDDMMYYYY(String data){

		String diaMesAno [] = data.split("de");
		String dia = diaMesAno[0].trim();
		String mes = diaMesAno[1].trim();
		String ano = diaMesAno[2].trim();

		mes = mesesDoAno.get(mes);

		return dia+"/"+mes+"/"+ano;

	}

	public boolean verificaLimiteInformacao(Elements dias, long unixTimesTampDataInicial,
			long unixTimesTampDataFinal, String tipo, String consulta) {

		if(!dias.isEmpty()){

			if(unixTimesTampDataFinal == Utiles.ZERO){

				for (Element dia : dias) {
					long timesTampDia = timestampDoDia(dia);
					/*Se a ultima noticia valor da lista(o mais antiga) ainda for menor
					 * que o valor que busco, entao toda a lista deve ser adicionada.
					 */
					if(timesTampDia >= unixTimesTampDataInicial){
						List<Noticia> noticiasDoDia = criaInformacao(dia, tipo, consulta);
						informacao.addAll(noticiasDoDia);
					}else{
						return true;
					}
				}

			}else{

				for (Element dia : dias) {
					long timesTampDia = timestampDoDia(dia);
					if((timesTampDia <= unixTimesTampDataFinal) && 
							(timesTampDia >= unixTimesTampDataInicial)){
						List<Noticia> noticiasDoDia = criaInformacao(dia, tipo, consulta);
						informacao.addAll(noticiasDoDia);
					}else if(timesTampDia < unixTimesTampDataInicial){
						return true;
					}	

				}
				return false;
			}
		}

		return true;
	}


	private long timestampDoDia(Element el){
		String data = el.select("h2").text();
		String timesTamp = padronizaFormatoDDMMYYYY(data);
		return Utiles.dataToTimestamp(timesTamp,"0000");

	}

	public List<Noticia> criaInformacao(Element el, String tipo, String consulta){

		String data = el.select("h2").text();
		data = padronizaFormatoDDMMYYYY(data);
		Elements notics = el.select("a");
		List<Noticia> noticiasDoDia = new ArrayList<Noticia>();
		for (Element notic : notics) {
			String url = notic.attr("href");
			String hora = notic.select("em").text();
			hora=hora.replaceFirst("h", "");
			long timesTamp= Utiles.dataToTimestamp(data, hora);
			String tituloResumo = notic.select("strong").text();

			Connection.Response noticiaIntegra;
			Document integra = null;
			try {
				noticiaIntegra = Jsoup.connect(url)
						.method(Method.GET).timeout(60000)
						.execute();
				integra = noticiaIntegra.parse();
			} catch (IOException e) {
				System.out.println(e.getMessage());
			}

			String tituloCompleto = integra.select(".ttl-main").text();
			String texto = integra.select(".text").text();

			noticiasDoDia.add(new NoticiasJornalTERRASearch(timesTamp, tituloCompleto, tituloResumo, texto, "", 
					"", url, 0));
				
				

		}
		return noticiasDoDia;


	}


	
	public NoticiasJornalTERRASearch(long timestamp,
			String titulo, String subTitulo, String conteudo, 
			String imagem, String emissor, String url, long repercussao) {
		
		super(timestamp, "PORTAL_TERRA", titulo, subTitulo, conteudo, imagem, 
				emissor, url, repercussao);
	}

	public static void main(String args[]) throws IOException{

		String searchDateStart= "10/02/2014";
		String searchDateFinish="12/02/2014";


	}




}
