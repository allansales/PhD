package entities;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Cotacoes {

	@Id
	private String id_cotacao;
	private String data_pregao;         
	private String cod_bdi;              
	private String cod_negociacao;
	private String tipo_mercado; 
	private String nome_resumido;
	private String especificacao_papel;
	private String prazo_termo;
	private String moeda_referencia;
	private String preco_abertura;
	private String preco_maximo; 
	private String preco_minimo; 
	private String preco_medio; 
	private String preco_ultimo; 
	private String preco_melhor_compra; 
	private String preco_melhor_venda; 
	private String total_negocios;
	private String qtd_titulos;
	private String volume_titulos; 
	private String preco_exercicio; 
	private String ind_mercado_opcoes; 
	private String data_vencimento;
	private String fator_cotacao; 
	private String pontos_exercicio;
	private String cod_isin;
	private String num_distribuicao;
	public String getId_cotacao() {
		return id_cotacao;
	}
	public void setId_cotacao(String id_cotacao) {
		this.id_cotacao = id_cotacao;
	}
	public String getData_pregao() {
		return data_pregao;
	}
	public void setData_pregao(String data_pregao) {
		this.data_pregao = data_pregao;
	}
	public String getCod_bdi() {
		return cod_bdi;
	}
	public void setCod_bdi(String cod_bdi) {
		this.cod_bdi = cod_bdi;
	}
	public String getCod_negociacao() {
		return cod_negociacao;
	}
	public void setCod_negociacao(String cod_negociacao) {
		this.cod_negociacao = cod_negociacao;
	}
	public String getTipo_mercado() {
		return tipo_mercado;
	}
	public void setTipo_mercado(String tipo_mercado) {
		this.tipo_mercado = tipo_mercado;
	}
	public String getNome_resumido() {
		return nome_resumido;
	}
	public void setNome_resumido(String nome_resumido) {
		this.nome_resumido = nome_resumido;
	}
	public String getEspecificacao_papel() {
		return especificacao_papel;
	}
	public void setEspecificacao_papel(String especificacao_papel) {
		this.especificacao_papel = especificacao_papel;
	}
	public String getPrazo_termo() {
		return prazo_termo;
	}
	public void setPrazo_termo(String prazo_termo) {
		this.prazo_termo = prazo_termo;
	}
	public String getMoeda_referencia() {
		return moeda_referencia;
	}
	public void setMoeda_referencia(String moeda_referencia) {
		this.moeda_referencia = moeda_referencia;
	}
	public String getPreco_abertura() {
		return preco_abertura;
	}
	public void setPreco_abertura(String preco_abertura) {
		this.preco_abertura = preco_abertura;
	}
	public String getPreco_maximo() {
		return preco_maximo;
	}
	public void setPreco_maximo(String preco_maximo) {
		this.preco_maximo = preco_maximo;
	}
	public String getPreco_minimo() {
		return preco_minimo;
	}
	public void setPreco_minimo(String preco_minimo) {
		this.preco_minimo = preco_minimo;
	}
	public String getPreco_medio() {
		return preco_medio;
	}
	public void setPreco_medio(String preco_medio) {
		this.preco_medio = preco_medio;
	}
	public String getPreco_ultimo() {
		return preco_ultimo;
	}
	public void setPreco_ultimo(String preco_ultimo) {
		this.preco_ultimo = preco_ultimo;
	}
	public String getPreco_melhor_compra() {
		return preco_melhor_compra;
	}
	public void setPreco_melhor_compra(String preco_melhor_compra) {
		this.preco_melhor_compra = preco_melhor_compra;
	}
	public String getPreco_melhor_venda() {
		return preco_melhor_venda;
	}
	public void setPreco_melhor_venda(String preco_melhor_venda) {
		this.preco_melhor_venda = preco_melhor_venda;
	}
	public String getTotal_negocios() {
		return total_negocios;
	}
	public void setTotal_negocios(String total_negocios) {
		this.total_negocios = total_negocios;
	}
	public String getQtd_titulos() {
		return qtd_titulos;
	}
	public void setQtd_titulos(String qtd_titulos) {
		this.qtd_titulos = qtd_titulos;
	}
	public String getVolume_titulos() {
		return volume_titulos;
	}
	public void setVolume_titulos(String volume_titulos) {
		this.volume_titulos = volume_titulos;
	}
	public String getPreco_exercicio() {
		return preco_exercicio;
	}
	public void setPreco_exercicio(String preco_exercicio) {
		this.preco_exercicio = preco_exercicio;
	}
	public String getInd_mercado_opcoes() {
		return ind_mercado_opcoes;
	}
	public void setInd_mercado_opcoes(String ind_mercado_opcoes) {
		this.ind_mercado_opcoes = ind_mercado_opcoes;
	}
	public String getData_vencimento() {
		return data_vencimento;
	}
	public void setData_vencimento(String data_vencimento) {
		this.data_vencimento = data_vencimento;
	}
	public String getFator_cotacao() {
		return fator_cotacao;
	}
	public void setFator_cotacao(String fator_cotacao) {
		this.fator_cotacao = fator_cotacao;
	}
	public String getPontos_exercicio() {
		return pontos_exercicio;
	}
	public void setPontos_exercicio(String pontos_exercicio) {
		this.pontos_exercicio = pontos_exercicio;
	}
	public String getCod_isin() {
		return cod_isin;
	}
	public void setCod_isin(String cod_isin) {
		this.cod_isin = cod_isin;
	}
	public String getNum_distribuicao() {
		return num_distribuicao;
	}
	public void setNum_distribuicao(String num_distribuicao) {
		this.num_distribuicao = num_distribuicao;
	}


}
