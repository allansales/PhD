package stocks.data.collectors.news;

import java.io.File;

public class MakePostgresImport {
	
	public static void makeImport(String dir, String tabela, String atributos){
				
		File diretorio = new File(dir);
		File fList[] = diretorio.listFiles();

		for (File file : fList) {
			System.out.println("COPY "+tabela+" ("+atributos+") FROM '"+"/Users/zegildo/Desktop/StocksData/"+file.getName()+"' CSV;");
		}
	}

	public static void main(String args[]){
		
		String fonteNoticias = "/Users/zegildo/Desktop/POS-GRADUACAO/DOUTORADO/Stocks/src/data_collection/collect_news/newsCSV";
		String fonteCotacoes = "/Users/zegildo/Desktop/POS-GRADUACAO/DOUTORADO/Stocks/src/data_collection/collect_cotacoes/BM&FBOVESPA/Historico_Cotacoes_CSV";
		String tabelaNoticias = "links_noticias_empresas";
		String tabelaCotacoes = "cotacoes";
		String atributosNoticias = "fonte,sub_fonte,cnpj,data_noticia,titulo,link";
		String atributosCotacoes = "data_pregao,cod_bdi,cod_negociacao,tipo_mercado, nome_resumido,especificacao_papel," +
				"prazo_termo,moeda_referencia,preco_abertura,preco_maximo, preco_minimo, preco_medio," +
				"preco_ultimo, preco_melhor_compra,preco_melhor_venda, total_negocios,qtd_titulos," +
				"volume_titulos, preco_exercicio, ind_mercado_opcoes, data_vencimento,fator_cotacao, pontos_exercicio," +
				"cod_isin,num_distribuicao ";


		makeImport(fonteNoticias, tabelaNoticias, atributosNoticias);
		makeImport(fonteCotacoes, tabelaCotacoes, atributosCotacoes);
	}
}
