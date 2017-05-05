package crawler;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Informacao{

	@GeneratedValue
	@Id
	public int id_informacoes;
	
	public String url;
	
	public long data;
	
	@Column(columnDefinition="TEXT")
	public String conteudo;
	public String fonte;
	public String subFonte;
	public String sobreTipo;
	public String sobre;
	
	public String emissor;
	public String imagem;
	public int repercussao;
	
	public Informacao(long timestamp, String conteudo, String URL, String fonte,
			String subFonte, String sobreTipo, String sobre, String emissor,
			String imagem,int repercussao){
		
		setTimestamp(timestamp);
		setConteudo(conteudo);
		setUrl(URL);
		setFonte(fonte);
		setSubFonte(subFonte);
		setSobreTipo(sobreTipo);
		setSobre(sobre);
		setEmissor(emissor);
		setImagem(imagem);
		setRepercussao(repercussao);
		
	}
	public int getId_informacoes() {
		return id_informacoes;
	}
	public void setId_informacoes(int id_informacoes) {
		this.id_informacoes = id_informacoes;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public long getData() {
		return data;
	}
	public void setData(long data) {
		this.data = data;
	}
	public String getEmissor() {
		return emissor;
	}
	public void setEmissor(String emissor) {
		this.emissor = emissor;
	}
	public String getImagem() {
		return imagem;
	}
	public void setImagem(String imagem) {
		this.imagem = imagem;
	}
	public int getRepercussao() {
		return repercussao;
	}
	public void setRepercussao(int repercussao) {
		this.repercussao = repercussao;
	}
	public String getSubFonte() {
		return subFonte;
	}

	public void setSubFonte(String subFonte) {
		this.subFonte = subFonte;
	}

	public String getSobreTipo() {
		return sobreTipo;
	}

	public void setSobreTipo(String sobreTipo) {
		this.sobreTipo = sobreTipo;
	}

	public String getSobre() {
		return sobre;
	}

	public void setSobre(String sobre) {
		this.sobre = sobre;
	}
	public String getFonte() {
		return fonte;
	}
	public void setFonte(String fonte) {
		this.fonte = fonte;
	}
	
	public long getTimestamp() {
		return data;
	}
	public void setTimestamp(long timestamp) {
		this.data = timestamp;
	}
	public String getConteudo() {
		return conteudo;
	}
	public void setConteudo(String conteudo) {
		this.conteudo = conteudo;
	}
	
	
}
