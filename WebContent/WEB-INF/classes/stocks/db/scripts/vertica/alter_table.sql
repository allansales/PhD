-- ==================== SCRIPT DE ALTERAÇÃO DE TABELAS ====================

-- ==================== TABELA empresa_isins ====================
-- Adicionamos uma nova coluna: tamanho_cotacao (para acelerar as consultas)
-- O valor default é zero (0) e nao pode ser nulo
ALTER TABLE empresa_isin 
ADD COLUMN tamanho_cotacao INTEGER DEFAULT 0 NOT NULL;

-- Atualizamos a nova coluna com o tamanho das cotações na tabela cotacao
UPDATE empresa_isin AS emp_isin
SET tamanho_cotacao = table_isin.cotacao_size
FROM (
	SELECT emp_isin.cod_isin AS cod_isin, COUNT(*) as cotacao_size
	FROM empresa_isin as emp_isin INNER JOIN (
							SELECT slice_time as data_pregao, cod_isin, TS_FIRST_VALUE(preco_ultimo IGNORE NULLS, 'const') as preco_ultimo
					        FROM cotacao
					        WHERE cod_bdi = 02
					        TIMESERIES slice_time AS '1 day' OVER (PARTITION BY cod_isin ORDER BY data_pregao)
					  		) AS acao ON emp_isin.cod_isin = acao.cod_isin 
	GROUP BY emp_isin.cod_isin
	) AS table_isin
WHERE emp_isin.cod_isin = table_isin.cod_isin;