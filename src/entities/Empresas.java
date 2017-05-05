package entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;


@Entity
public class Empresas {

	@Id
	@Column(columnDefinition="CHAR(14)")
	private String cnpj;
	
	@Column(columnDefinition="CHAR(10)")
	private String cod_cvm;
	
	@Column(columnDefinition="CHAR(2)")
	private String estado;
	
	private String nome_empresa;     
	private String nome_pregao;
	private String cod_negociacao;
	private String atividade_principal;
	private String setor;
	private String sub_setor;
	private String segmento;
	private String site;
	private String endereco;
	private String cidade;
	private String cep;
	private String telefone;
	private String fax;
	private String emails;
	private String twitter_empresa;
	private String facebook_empresa;
	
	public String getNome_empresa() {
		return nome_empresa;
	}
	public void setNome_empresa(String nome_empresa) {
		this.nome_empresa = nome_empresa;
	}
	public String getNome_pregao() {
		return nome_pregao;
	}
	public void setNome_pregao(String nome_pregao) {
		this.nome_pregao = nome_pregao;
	}
	public String getCod_negociacao() {
		return cod_negociacao;
	}
	public void setCod_negociacao(String cod_negociacao) {
		this.cod_negociacao = cod_negociacao;
	}
	public String getCod_cvm() {
		return cod_cvm;
	}
	public void setCod_cvm(String cod_cvm) {
		this.cod_cvm = cod_cvm;
	}
	public String getCnpj() {
		return cnpj;
	}
	public void setCnpj(String cnpj) {
		this.cnpj = cnpj;
	}
	public String getAtividade_principal() {
		return atividade_principal;
	}
	public void setAtividade_principal(String atividade_principal) {
		this.atividade_principal = atividade_principal;
	}
	public String getSetor() {
		return setor;
	}
	public void setSetor(String setor) {
		this.setor = setor;
	}
	public String getSub_setor() {
		return sub_setor;
	}
	public void setSub_setor(String sub_setor) {
		this.sub_setor = sub_setor;
	}
	public String getSegmento() {
		return segmento;
	}
	public void setSegmento(String segmento) {
		this.segmento = segmento;
	}
	public String getSite() {
		return site;
	}
	public void setSite(String site) {
		this.site = site;
	}
	public String getEndereco() {
		return endereco;
	}
	public void setEndereco(String endereco) {
		this.endereco = endereco;
	}
	public String getCidade() {
		return cidade;
	}
	public void setCidade(String cidade) {
		this.cidade = cidade;
	}
	public String getCep() {
		return cep;
	}
	public void setCep(String cep) {
		this.cep = cep;
	}
	public String getEstado() {
		return estado;
	}
	public void setEstado(String estado) {
		this.estado = estado;
	}
	public String getTelefone() {
		return telefone;
	}
	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}
	public String getFax() {
		return fax;
	}
	public void setFax(String fax) {
		this.fax = fax;
	}
	public String getEmails() {
		return emails;
	}
	public void setEmails(String emails) {
		this.emails = emails;
	}
	public String getTwitter_empresa() {
		return twitter_empresa;
	}
	public void setTwitter_empresa(String twitter_empresa) {
		this.twitter_empresa = twitter_empresa;
	}
	public String getFacebook_empresa() {
		return facebook_empresa;
	}
	public void setFacebook_empresa(String facebook_empresa) {
		this.facebook_empresa = facebook_empresa;
	}
	
}
