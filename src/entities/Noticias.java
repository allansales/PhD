package entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="links_noticias_empresas")
public class Noticias {
	
	@Id
	private String id_link;
	
	@Column(columnDefinition="CHAR(14)")
	private String cnpj;
	
	private String fonte;
	private String sub_fonte;
	private String data_noticia;
	private String titulo;
	private String link;
	
	public String getId_link() {
		return id_link;
	}
	public void setId_link(String id_link) {
		this.id_link = id_link;
	}
	public String getCnpj() {
		return cnpj;
	}
	public void setCnpj(String cnpj) {
		this.cnpj = cnpj;
	}
	public String getFonte() {
		return fonte;
	}
	public void setFonte(String fonte) {
		this.fonte = fonte;
	}
	public String getSub_fonte() {
		return sub_fonte;
	}
	public void setSub_fonte(String sub_fonte) {
		this.sub_fonte = sub_fonte;
	}
	public String getData_noticia() {
		return data_noticia;
	}
	public void setData_noticia(String data_noticia) {
		this.data_noticia = data_noticia;
	}
	public String getTitulo() {
		return titulo;
	}
	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
}
