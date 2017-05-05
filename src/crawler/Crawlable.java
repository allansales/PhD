package crawler;

import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import crawler.noticias.Noticia;

public interface Crawlable {
	
	public List<Noticia> criaInformacao(Element el, String tipo, String consulta);
	public Document obtemPagina(String url);
}
