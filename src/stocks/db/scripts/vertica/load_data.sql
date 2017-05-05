------------------ SCRIPT DE "BULK LOAD" DOS DADOS ------------------ 

-- Todos os arquivos de dados estao no servidor na pasta "/home/stocks/git/stocks/data"

-- ================= CARGA da tabela EMPRESA =================
-- CARREGA os dados das empresas a partir de um arquivo CSV
COPY empresa
FROM '/home/stocks/data/DadosEmpresas.csv'
DELIMITER ','    -- Delimitador das colunas
ENCLOSED BY '"'  -- Caractere que abre e fecha strings
ESCAPE AS '\'    -- Caractere de escape
NULL AS 'NA';    -- Como o NULL eh definido

select ANALYZE_CONSTRAINTS('empresa');

-- ================= CARGA da tabela EMPRESA_ISIN =================
-- CARREGA os dados dos ISINs das empresas a partir de um arquivo CSV
COPY empresa_isin
FROM '/home/stocks/data/DadosEmpresasISINs.csv'
DELIMITER ','    -- Delimitador das colunas
ENCLOSED BY '"'  -- Caractere que abre e fecha strings
ESCAPE AS '\'    -- Caractere de escape
NULL AS 'NA';    -- Como o NULL eh definido

select ANALYZE_CONSTRAINTS('empresa_isin');

-- ================= CARGA da tabela CONTATO_INVESTIDOR =================
-- CARREGA os dados dos investidores das empresas a partir de um arquivo CSV

COPY contato_investidor
FROM '/home/stocks/data/DadosContatosInvestidores.csv'
DELIMITER ','    -- Delimitador das colunas
ENCLOSED BY '"'  -- Caractere que abre e fecha strings
ESCAPE AS '\'    -- Caractere de escape
NULL AS 'NA';    -- Como o NULL eh definido

select ANALYZE_CONSTRAINTS('contato_investidor');

-- ================= CARGA da tabela CONTATO_INVESTIDOR =================
-- CARREGA os dados dos links das noticias das empresas a partir de um arquivo CSV

COPY link_noticias_empresa
FROM '/home/stocks/data/news/links_*.csv'
DELIMITER ','    -- Delimitador das colunas
ENCLOSED BY '"'  -- Caractere que abre e fecha strings
ESCAPE AS '\'    -- Caractere de escape
NULL AS 'NA';    -- Como o NULL eh definido

select ANALYZE_CONSTRAINTS('link_noticias_empresa');

-- ================= CARGA da tabela COTACAO =================
-- CARREGA os dados das cotacoes a partir dos arquivos CSV locais 

-- OBS.: no futuro podemos refatorar esse SQL para gera-lo e fazer a transacao
-- 		 automaticamente no proprio codigo python (que leh o UTF e escreve o CSV)

COPY cotacao 
FROM '/home/stocks/data/Historico_Cotacoes_CSV/cotacoes_*.csv' 
DELIMITER ',' 
ENCLOSED BY '"' 
ESCAPE AS '\'    -- Caractere de escape
NULL 'NA';

-- A funcao ANALYZE_CONSTRAINST retorna os valores das chaves estrangeiras
-- (sem repeticao) que não tiverem correspondencia.
INSERT INTO isin_inexistente (cod_isin) 
	SELECT load_results.'Column Values' as cod_isin 
	FROM (select ANALYZE_CONSTRAINTS('cotacao')) as load_results;
COMMIT;



--	ATENCAO:
--	Eh importante checar os arquivos de log de excecoes ao termino de cada carga. 
--	O Vertica rejeita linhas se houver alguma excecao na leitura (mais colunas, menos colunas, etc.)
--	Ver arquivos de log abaixo:
--		<db_dir>/<catalog_dir>/CopyErrorLogs/<tablename-filename-of-source>-copy-from-exceptions
--		<db_dir>/<catalog_dir>/CopyErrorLogs/<tablename-filename-of-source>-copy-from-rejected-data