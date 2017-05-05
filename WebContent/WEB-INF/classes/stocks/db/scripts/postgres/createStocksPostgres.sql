-- ==================== SCRIPT DE DELECAO DE TABELAS ====================
DROP TABLE isins_inexistentes;
DROP TABLE cotacoes;
DROP TABLE links_noticias_empresas;
DROP TABLE contato_investidores;
DROP TABLE empresas_isin;
DROP TABLE empresas;
DROP SEQUENCE id_contato;
DROP SEQUENCE id_link;
DROP SEQUENCE id_cotacao;
DROP SEQUENCE id;
-- ==================== SCRIPT DE CRIACAO DE TABELAS ====================

-- =========== TABELA DE DESCRICAO DAS EMPRESAS =========== 
CREATE TABLE empresas (
    -- Atributos Gerais
    nome_empresa          VARCHAR(100),
    nome_pregao           VARCHAR(30),
    cod_negociacao        VARCHAR(100),
    cod_cvm               CHAR(10),
    cnpj                  CHAR(14),
    atividade_principal   VARCHAR(500),
    
    -- Atributos de Classificacao
    setor                 VARCHAR(100),
    sub_setor             VARCHAR(100),
    segmento              VARCHAR(100),

    -- Atributos de Contato
    site                  VARCHAR(100),
    endereco              VARCHAR(100),
    cidade                VARCHAR(100),
    cep                   VARCHAR(20),
    estado                CHAR(2),
    telefone              VARCHAR(100),
    fax                   VARCHAR(100),
    emails                VARCHAR(200),
    twitter_empresa       VARCHAR(300),
    facebook_empresa      VARCHAR(300),   

    PRIMARY KEY           (cnpj)
);

-- =========== TABELA com os ISINs das EMPRESAs =========== 
/*
    Uma empresa pode ter 1 ou mais ISINs
    Nessa tabela o ISIN eh a chave primaria pois nao podem existir dois ISINs 
    iguais
    Alem disso, o vertica soh permite criar chaves estrangeiras que referenciem
    chaves primarias da tabela alvo, isso acontece entre COTACAO e EMPRESA_ISIN
    Mais detalhes: https://my.vertica.com/docs/6.1.x/HTML/index.htm#12191.htm
*/
CREATE TABLE empresas_isin (
    cnpj                  CHAR(14) NOT NULL,
    cod_isin              CHAR(12) NOT NULL,
    PRIMARY KEY           (cod_isin),
    FOREIGN KEY           (cnpj) REFERENCES empresas (cnpj)

);

-- =========== TABELA com os CONTATOs das EMPRESAs =========== 
CREATE SEQUENCE id_contato;
CREATE TABLE contato_investidores (
    nome_contato          VARCHAR(100) NOT NULL,
    twitter_contato       VARCHAR(200),
    facebook_contato      VARCHAR(200), 
    cnpj                  CHAR(14) NOT NULL,
    id_contato INTEGER NOT NULL default nextval('id_contato'),
    PRIMARY KEY           (id_contato),
    FOREIGN KEY           (cnpj) REFERENCES empresas (cnpj)
);

-- =========== TABELA com os LINKS das NOTICIAS das EMPRESAs =========== 
CREATE SEQUENCE id_link;
CREATE TABLE links_noticias_empresas (
    fonte                 VARCHAR(100),
    sub_fonte             VARCHAR(100),
    cnpj                  CHAR(14) NOT NULL,
    data_noticia          TIMESTAMP,
    titulo                VARCHAR(500),
    link                  VARCHAR(500),
    id_link INTEGER NOT NULL default nextval('id_link'),
    PRIMARY KEY           (id_link),
    FOREIGN KEY           (cnpj) REFERENCES empresas (cnpj)
);

-- =========== TABELAS DO HISTORICO DE COTACOES =========== 
/* 
-- Significado dos dados:
--     Mais informacoes ver o Dicionario de Dados (SeriesHistoricas_Layout.pdf)
-- Diferenca entre tipos NUMERIC e MONEY:
--     Precos estao definidos como o tipo MONEY
--     Outros numeros em ponto flutuante estao como NUMERIC
--     No Vertica todo ponto flutuante eh um NUMERIC, a diferenca estah 
--     apenas na definicao default da precisao e casas decimais.
--     Mais informacoes: https://my.vertica.com/docs/6.1.x/HTML/index.htm#12295.htm
-- Chave estrangeira:
--     cotacao.cod_isin REFERENCIA empresa_isin.cod_isin
*/
CREATE SEQUENCE id_cotacao;
CREATE TABLE cotacoes (
    data_pregao           TIMESTAMP,
    cod_bdi               CHAR(2),
    cod_negociacao        CHAR(12),
    tipo_mercado          INTEGER, 
    nome_resumido         CHAR(12),
    especificacao_papel   CHAR(10),
    prazo_termo           CHAR(3),
    moeda_referencia      CHAR(4),
    preco_abertura        NUMERIC(11, 2),
    preco_maximo          NUMERIC(11, 2), 
    preco_minimo          NUMERIC(11, 2), 
    preco_medio           NUMERIC(11, 2), 
    preco_ultimo          NUMERIC(11, 2), 
    preco_melhor_compra   NUMERIC(11, 2), 
    preco_melhor_venda    NUMERIC(11, 2), 
    total_negocios        INTEGER,
    qtd_titulos           BIGINT,
    volume_titulos        NUMERIC(16, 2), 
    preco_exercicio       NUMERIC(11, 2), 
    ind_mercado_opcoes    INTEGER, 
    data_vencimento       TIMESTAMP,
    fator_cotacao         INTEGER, 
    pontos_exercicio      NUMERIC(13, 6),
    cod_isin              CHAR(12),
    num_distribuicao      CHAR(3),
    id_cotacao INTEGER NOT NULL default nextval('id_cotacao'),
    PRIMARY KEY           (id_cotacao)
);

-- =========== TABELAS TEMPORARIA com ISINs INEXISTENTES =========== 
/*
    Para resovermos temporariamente o problema da existencia de cotacoes sem
    o seu correspondente isin na tabela 'empresa_isin', criamos uma tabela 
    com os 'cod_isin' dos isins inexistentes. 
*/
CREATE SEQUENCE id;
CREATE TABLE isins_inexistentes (
    id INTEGER NOT NULL default nextval('id'),
    cod_isin              CHAR(20) NOT NULL,
    PRIMARY KEY           (id)
);

CREATE TABLE informacoes(

	data BIGINT,
	conteudo text,
	url VARCHAR(250),
	fonte VARCHAR(50),
	subFonte VARCHAR(50),
 	sobreTipo VARCHAR(50),
	sobre VARCHAR(50),
	emissor VARCHAR(200),
	imagem VARCHAR(250),
	repercussao INTEGER,
	PRIMARY KEY (url)
	

);

