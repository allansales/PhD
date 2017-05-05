--Criar uma tabela de noticias e carrega-la com as informacoes do csv
create table noticias(
data bigint,
diadaSemana smallint,
dia smallint,
mes smallint,
hora smallint,
ano smallint,
jornal varchar(10),
qtComentarios integer,
qtTweets integer,
qtFacebook integer,
qtLinkedIn integer,
qtGooglePlus integer,
polaridade integer
)

--Criar um index por data para vetorizar as consultas
CREATE INDEX data_idx ON noticias (data);
-- Preencher a tabela
copy noticias FROM '/Users/zegildo/Desktop/atributosPorMinuto.csv' DELIMITER ',' CSV

--Criar uma nova tabela com os resultados da consulta
CREATE TABLE setor15min AS
	select
	setor,
	datahora,
	round(avg(fechamento_atual)::numeric,2) as media,
	sum(negocios) as negocios,
	sum(quantidade_papeis) as quantidade_papeis,
	sum(volume_financeiro) as volume_financeiro
	from empresas e, book15minutos b, codigos_empresas c 
	where int4(e.cod_cvm) = c.cod_cvm and b.cod_negociacao = c.cod_negociacao
	group by setor, datahora

alter table setor15min alter column datahora type bigint using datahora::bigint;

-- Como vamos fazer muitas operacoes com datas vamos criar um index para essas datas tambem.
CREATE INDEX datahora_idxx ON setor15min (datahora);

--Alter table para adicoinar as novas colunas
ALTER TABLE setor15min ADD COLUMN qt numeric; 
ALTER TABLE setor15min ADD COLUMN mQtCom numeric;
ALTER TABLE setor15min ADD COLUMN meQtCom numeric;
ALTER TABLE setor15min ADD COLUMN dpQtCom numeric;
ALTER TABLE setor15min ADD COLUMN vQtCom numeric;
ALTER TABLE setor15min ADD COLUMN sQtCom numeric;
ALTER TABLE setor15min ADD COLUMN mQtTw numeric;
ALTER TABLE setor15min ADD COLUMN meQtTw numeric;
ALTER TABLE setor15min ADD COLUMN dpQtTw numeric;
ALTER TABLE setor15min ADD COLUMN vQtTw numeric;
ALTER TABLE setor15min ADD COLUMN sQtTw numeric;
ALTER TABLE setor15min ADD COLUMN mQtFb numeric;
ALTER TABLE setor15min ADD COLUMN meQtFb numeric;
ALTER TABLE setor15min ADD COLUMN dpQtFb numeric;
ALTER TABLE setor15min ADD COLUMN vQtFb numeric;
ALTER TABLE setor15min ADD COLUMN sQtFb numeric;
ALTER TABLE setor15min ADD COLUMN mQtLk numeric;
ALTER TABLE setor15min ADD COLUMN meQtLk numeric;
ALTER TABLE setor15min ADD COLUMN dpQtLk numeric;
ALTER TABLE setor15min ADD COLUMN vQtLk numeric;
ALTER TABLE setor15min ADD COLUMN sQtLk numeric;
ALTER TABLE setor15min ADD COLUMN mQtGp numeric;
ALTER TABLE setor15min ADD COLUMN meQtGp numeric;
ALTER TABLE setor15min ADD COLUMN dpQtGp numeric;
ALTER TABLE setor15min ADD COLUMN vQtGp numeric;
ALTER TABLE setor15min ADD COLUMN sQtGp numeric;
ALTER TABLE setor15min ADD COLUMN mPol numeric;
ALTER TABLE setor15min ADD COLUMN mePol numeric;
ALTER TABLE setor15min ADD COLUMN dpPol numeric;
ALTER TABLE setor15min ADD COLUMN vPol numeric;
ALTER TABLE setor15min ADD COLUMN sPol numeric;

--Calcula a mediana
CREATE OR REPLACE FUNCTION final_median(anyarray) RETURNS float8 AS
$$ 
DECLARE
  cnt INTEGER;
BEGIN
  cnt := (SELECT COUNT(*) FROM unnest($1) val WHERE val IS NOT NULL);
	IF cnt < 2 THEN
		RETURN cnt;
	ELSE

	  RETURN (SELECT avg(tmp.val)::float8 
		    FROM (SELECT val FROM unnest($1) val
			    WHERE val IS NOT NULL 
			    ORDER BY 1 
			    LIMIT 2 - MOD(cnt, 2) 
			    OFFSET CEIL(cnt/ 2.0) - 1
			  ) AS tmp
		 );
	END IF;
END
$$ LANGUAGE plpgsql;
 
CREATE AGGREGATE median(anyelement) (
  SFUNC=array_append,
  STYPE=anyarray,
  FINALFUNC=final_median,
  INITCOND='{}'
);

ALTER TABLE NOTICIAS ADD COLUMN datagroup bigint;

--Verifica como transformar de inteiro para timestamp e vice-versa uma vez que precisamos retirar horas do numero
-- Algumas vezes essas horas resultam em entrar em outro mes e isso causa muitos problemas o cast eh uma solucao 'simples'

CREATE OR REPLACE FUNCTION insertSetor15min2_0() RETURNS void AS 
$$
DECLARE
	noticiaHora CURSOR FOR select data from noticias where data between 200801010000 and 201504302359;
	horaMinuto int;
	minuto int;
	novaData bigint;
	nD varchar;
	d bigint;
BEGIN
OPEN noticiaHora;

LOOP
	FETCH noticiaHora INTO d;
	EXIT WHEN NOT FOUND;
	horaMinuto := SUBSTR(d::varchar, 9, 11)::int;
	minuto := (SUBSTR(d::varchar, 11, 11))::int;
	nD := (SUBSTR(d::varchar, 0, 11));
	
	IF  horaMinuto > 1000 and horaMinuto <= 1945  THEN
		IF  minuto > 0 and minuto <=15  THEN
			novaData:= (nD || '15')::bigint;
		ELSIF minuto >15 and minuto <= 30  THEN
			novaData := (nD|| '30')::bigint;
		ELSIF minuto >30 and minuto <= 45 THEN
			novaData:= (nD || '45')::bigint;
		ELSE
			novaData:= (nD || '00')::bigint;
		END IF;
	ELSE
		novaData := (SUBSTR(to_char((to_timestamp(d::varchar,'YYYYMMDDHH24MI')::timestamp without time zone + interval '1 day'),'YYYYMMDDHH24MI'),0,9) || '1000')::bigint;
	END IF;
	UPDATE noticias SET datagroup=novaData where noticias.data=d; 
END LOOP;
CLOSE noticiaHora; 

SELECT count(*) as qt,  

			avg(qtcomentarios) as mQtCom, 
			median(qtcomentarios) as meQtCom,
			stddev_samp(qtcomentarios) as dpQtCom, 
			var_samp(qtcomentarios) as vQtCom, 
			sum(qtcomentarios) as sQtCom, 

			avg(qtTweets) as mQtTw, 
			median(qtTweets) as meQtTw, 
			stddev_samp(qtTweets)  as dpQtTw, 
			var_samp(qtTweets)  as vQtTw, 
			sum(qtTweets)  as sQtTw, 

			avg(qtFacebook) as mQtFb, 
			median(qtFacebook )  as meQtFb, 
			stddev_samp(qtFacebook ) as dpQtFb, 
			var_samp(qtFacebook ) as vQtFb, 
			sum(qtFacebook )  as sQtFb, 

			avg(qtLinkedIn)  as mQtLk, 
			median(qtLinkedIn) as meQtLk, 
			stddev_samp(qtLinkedIn)  as dpQtLk, 
			var_samp(qtLinkedIn) as vQtLk, 
			sum(qtLinkedIn)  as sQtLk, 

			avg(qtGooglePlus)  as mQtGp, 
			median(qtGooglePlus) as meQtGp, 
			stddev_samp(qtGooglePlus)  as dpQtGp, 
			var_samp(qtGooglePlus) as vQtGp, 
			sum(qtGooglePlus)  as sQtGp, 

			avg(polaridade)  as mPol, 
			median(polaridade) as mePol,
			stddev_samp(polaridade) as dpPol, 
			var_samp(polaridade) as vPol, 
			sum(polaridade)  as sPol
			
			from noticias where data between 200801010000 and 201504302359 group by datagroup;
END;
$$ LANGUAGE plpgsql;

select insertSetor15min2_0()