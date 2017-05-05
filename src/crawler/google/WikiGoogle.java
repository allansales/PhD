package crawler.google;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class WikiGoogle {

	private static Document obtemPagina(String nome_empresa) throws IOException{
		try{
			Document doc = Jsoup.connect("https://www.google.com.br/search?q="+nome_empresa+" empresa wikipedia&gws_rd=ssl").timeout(0).
					userAgent("Mozilla").get();
			return doc;
		}catch(HttpStatusException e){
			e.printStackTrace();
			return null;
		}
	}

	public static void main(String args[]){

		String nomes[] = null;
		int contados = 0;
		int encontrados = 0;

		try {
			BufferedReader in = new BufferedReader(new FileReader("src/crawler/EmpresasBolsa.txt"));

			while (in.ready()) {
				contados++;
				nomes = in.readLine().split(";");
				String nome_empresa = (nomes[0].toLowerCase());
				Document doc = obtemPagina(nome_empresa);
				if(doc!= null){
					System.out.println(nome_empresa);
					Elements sites = doc.select("cite");
					String melhorLink = "";
					for (Element site : sites) {
						String link = site.text();
						if(link.contains("pt.wikipedia.org/wiki/")){
							encontrados++;
							System.out.println("\t -"+link);
							break;
						}else if(link.contains("wikipedia.org/wiki/")){
							encontrados++;
							melhorLink += "\t -"+link+"\n";
						}
					}
					if(melhorLink!=null){
						System.out.println(melhorLink);
					}
				}
			}
			in.close();
			System.out.println("Contados: "+contados);
			System.out.println("Encontrados: "+encontrados);

		}catch(Exception e){
			e.printStackTrace();		
		}
	}
}
