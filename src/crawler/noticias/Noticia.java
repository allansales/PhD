package crawler.noticias;

import java.util.List;

import org.jsoup.nodes.Element;

import crawler.Crawlable;


public abstract class Noticia implements Crawlable{

	private long  timestamp;
	private String fonte;
	private String subFonte;
	private String titulo;
	private String subTitulo;
	private String conteudo;
	private String emissor;
	private String url;
	private String idNoticia;
	private String repercussao;

	public Noticia(){
		
	}

	public Noticia(long timestamp, String subFonte,
			String titulo, String subTitulo, String conteudo, 
			String emissor, String url, String repercussao, String id){

		setTimestamp(timestamp);
		setFonte("JORNAL");
		setSubFonte(subFonte);
		setTitulo(titulo);
		setSubTitulo(subTitulo);
		setConteudo(conteudo);
		setEmissor(emissor);
		setUrl(url);
		setRepercussao(repercussao);
		setIdNoticia(id);
		
	}

	public List<Noticia> criaInformacao(Element el, String tipo, String consulta){
		return null;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	@Override
	public String toString() {
		return timestamp + "|" + fonte
				+ "|" + subFonte + "|" + titulo
				+ "|" + subTitulo + "|" + conteudo
				+ "|" + emissor + "|" + url + "|"
				+ repercussao;
	}

	public long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public String getFonte() {
		return fonte;
	}
	public void setFonte(String fonte) {
		this.fonte = fonte;
	}
	public String getSubFonte() {
		return subFonte;
	}
	public void setSubFonte(String subFonte) {
		this.subFonte = subFonte;
	}
	public String getTitulo() {
		return titulo;
	}
	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}
	public String getSubTitulo() {
		return subTitulo;
	}
	public void setSubTitulo(String subTitulo) {
		this.subTitulo = subTitulo;
	}
	public String getConteudo() {
		return conteudo;
	}
	public void setConteudo(String conteudo) {
		this.conteudo = conteudo;
	}
	
	public String getEmissor() {
		return emissor;
	}
	public void setEmissor(String emissor) {
		this.emissor = emissor;
	}
	public String getRepercussao() {
		return repercussao;
	}
	public void setRepercussao(String repercussao) {
		this.repercussao = repercussao;
	}
	public String getIdNoticia() {
		return idNoticia;
	}
	public void setIdNoticia(String idNoticia) {
		this.idNoticia = idNoticia;
	}

}
