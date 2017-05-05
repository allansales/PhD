-- ==================== SCRIPT DE CRIACAO DE TABELAS ====================

-- =========== TABELA DE DESCRICAO DAS EMPRESAS =========== 

CREATE TABLE IF NOT EXISTS empresa (
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

CREATE TABLE IF NOT EXISTS empresa_isin (
    cnpj                  CHAR(14) NOT NULL,
    cod_isin              CHAR(12) NOT NULL,
    PRIMARY KEY           (cod_isin),
    FOREIGN KEY           (cnpj) REFERENCES empresa (cnpj)
);

-- =========== TABELA com os CONTATOs das EMPRESAs =========== 
CREATE TABLE IF NOT EXISTS contato_investidor (
    id_contato            AUTO_INCREMENT,
    nome_contato          VARCHAR(100) NOT NULL,
    twitter_contato       VARCHAR(200),
    facebook_contato      VARCHAR(200), 
    cnpj                  CHAR(14) NOT NULL,
    PRIMARY KEY           (id_contato),
    FOREIGN KEY           (cnpj) REFERENCES empresa (cnpj)
);

-- =========== TABELA com os LINKS das NOTICIAS das EMPRESAs =========== 
CREATE TABLE IF NOT EXISTS link_noticias_empresa (
    id_link               AUTO_INCREMENT,
    fonte                 VARCHAR(100),
    sub_fonte             VARCHAR(100),
    cnpj                  CHAR(14) NOT NULL,
    data_noticia          TIMESTAMP,
    titulo                VARCHAR(500),
    link                  VARCHAR(500),
    PRIMARY KEY           (id_link),
    FOREIGN KEY           (cnpj) REFERENCES empresa (cnpj)
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

CREATE TABLE IF NOT EXISTS cotacao (
    id_cotacao            AUTO_INCREMENT,
    data_pregao           TIMESTAMP,
    cod_bdi               CHAR(2),
    cod_negociacao        CHAR(12),
    tipo_mercado          INTEGER, 
    nome_resumido         CHAR(12),
    especificacao_papel   CHAR(10),
    prazo_termo           CHAR(3),
    moeda_referencia      CHAR(4),
    preco_abertura        MONEY(11, 2),
    preco_maximo          MONEY(11, 2), 
    preco_minimo          MONEY(11, 2), 
    preco_medio           MONEY(11, 2), 
    preco_ultimo          MONEY(11, 2), 
    preco_melhor_compra   MONEY(11, 2), 
    preco_melhor_venda    MONEY(11, 2), 
    total_negocios        INTEGER,
    qtd_titulos           INTEGER,
    volume_titulos        NUMERIC(16, 2), 
    preco_exercicio       MONEY(11, 2), 
    ind_mercado_opcoes    INTEGER, 
    data_vencimento       TIMESTAMP,
    fator_cotacao         INTEGER, 
    pontos_exercicio      NUMERIC(13, 6),
    cod_isin              CHAR(12),
    num_distribuicao      CHAR(3),
    PRIMARY KEY           (id_cotacao),
    FOREIGN KEY           (cod_isin) REFERENCES empresa_isin (cod_isin)
);

-- =========== TABELAS TEMPORARIA com ISINs INEXISTENTES =========== 
/*
    Para resovermos temporariamente o problema da existencia de cotacoes sem
    o seu correspondente isin na tabela 'empresa_isin', criamos uma tabela 
    com os 'cod_isin' dos isins inexistentes. 
*/
CREATE TABLE IF NOT EXISTS isin_inexistente (
    id                    AUTO_INCREMENT,
    cod_isin              CHAR(20) NOT NULL,
    PRIMARY KEY           (id)
);
