package crawler.wikipedia;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class WikiCrawler {

	private static Document obtemPagina(String nome_empresa) throws IOException{
		try{
			Document doc = Jsoup.connect("http://pt.wikipedia.org/wiki/"+nome_empresa).timeout(0).get();
			return doc;
		}catch(HttpStatusException e){
			return null;
		}
	}

	public static void main(String args[]){

		String nomes[] = null;
		int contados = 0;
		int encontradosEconteudo = 0;
		int encontradosSemConteudo = 0;
		int naoEncontrados = 0;
		int encontradosParaDesambiguar = 0;

		try {
			BufferedReader in = new BufferedReader(new FileReader("src/crawler/EmpresasBolsa.txt"));

			while (in.ready()) {
				contados++;
				nomes = in.readLine().split(";");
				String nome_empresa = (nomes[0].toLowerCase()).replaceAll(" ", "_");

				Document doc = obtemPagina(nome_empresa);
				if(doc != null){
					Element desamb = doc.select("#disambig").first();
					if(desamb != null){
						encontradosParaDesambiguar++;
						System.out.println(nome_empresa +"[[OK]] - conteudo a desambiguar");
						Element el= doc.select("ul").first();
						Elements lis = el.children();
						for (Element li : lis) {
							System.out.println("\t º"+li.text());
						}			
					}else{
						Element elTitulo = doc.select("#firstHeading span").first();
						String titulo = elTitulo.text();
						Element elContent = doc.select("#bodyContent").first();
						String conteudo = elContent.text();
						if(conteudo.contains("mpresa") || conteudo.contains("ovespa") || conteudo.contains("negocio")){
							System.out.println(nome_empresa +"[[OK]] - "+titulo+" - conteudo [OK]");
							encontradosEconteudo++;
						}else{
							System.out.println(nome_empresa +"[[OK]] - "+titulo+" - conteudo [NOK]");
							encontradosSemConteudo++;

						}
					}
				}else{
					System.out.println(nome_empresa +"[NOK]");
					naoEncontrados++;
				}
			}	
			in.close();
			System.out.println("Contados: "+ contados);
			System.out.println("Encontrados: "+encontradosEconteudo);
			System.out.println("Encontrados Mas Sem relacao: "+encontradosSemConteudo);
			System.out.println("Encontrados para Desambiguar: "+encontradosParaDesambiguar);
			System.out.println("Nao Encontrados: "+naoEncontrados);

		}catch(Exception e){
			e.printStackTrace();		
		}
	}
}