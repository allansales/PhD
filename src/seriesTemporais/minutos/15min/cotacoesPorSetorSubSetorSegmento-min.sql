--Verifica se tem alguma acao do book nao existe na tabela de codigos de negociacoes
select distinct cod_negociacao from book15minutos where cod_negociacao not in (select cod_negociacao from codigos_empresas);

-- seleciona os setores
select b.cod_negociacao, 
c.cod_cvm, 
setor,sub_setor, 
segmento, 
fechamento_atual,
abertura,
maximo,
minimo,
fechamento_anterior, 
negocios,
quantidade_papeis,
volume_financeiro,
datahora 
from empresas e, book15minutos b, codigos_empresas c 
where int4(e.cod_cvm) = c.cod_cvm and b.cod_negociacao = c.cod_negociacao

-- agrupa acoes por setor
select
setor,
datahora,
round(avg(fechamento_atual)::numeric,2),
sum(negocios),
sum(quantidade_papeis),
sum(volume_financeiro)
from empresas e, book15minutos b, codigos_empresas c 
where int4(e.cod_cvm) = c.cod_cvm and b.cod_negociacao = c.cod_negociacao
group by setor, datahora

-- agrupa acoes por subsetor
select
sub_setor,
datahora,
round(avg(fechamento_atual)::numeric,2),
sum(negocios),
sum(quantidade_papeis),
sum(volume_financeiro)
from empresas e, book15minutos b, codigos_empresas c 
where int4(e.cod_cvm) = c.cod_cvm and b.cod_negociacao = c.cod_negociacao
group by sub_setor, datahora

-- agrupa acoes por segmento
select
segmento,
datahora,
round(avg(fechamento_atual)::numeric,2),
sum(negocios),
sum(quantidade_papeis),
sum(volume_financeiro)
from empresas e, book15minutos b, codigos_empresas c 
where int4(e.cod_cvm) = c.cod_cvm and b.cod_negociacao = c.cod_negociacao
group by segmento, datahora