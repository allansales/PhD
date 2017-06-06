library(dplyr)
library(ggplot2)
library(mongolite)
library(anytime)
library(corrr)
library(lubridate)

get_data <- function(colecao){
  con <- mongo(db = "stocks", collection = colecao, url = "mongodb://localhost", verbose = FALSE, options = ssl_options())
  data <- con$find()
  rm(con)
  return(data)
}

g1_noticias <- get_data("g1Noticias")
#g1_comentarios <- get_data("g1Comentarios")
folha_noticias <- get_data("folhaNoticias")
# folha_comentarios <- get_data("folhaComentarios")
estadao_noticias <- get_data("estadaoNoticias")
# estadao_comentarios <- get_data("estadaoComentarios")

noticias <- bind_rows(g1_noticias, folha_noticias, estadao_noticias)
noticias <- noticias %>% mutate(data = utcdate(timestamp), repercussao = as.integer(repercussao), 
                                ano = year(data), mes = month(data), dia = day(data), dia_do_ano = yday(data))

# geral
summary(noticias)

## descricao noticias
# numero de noticias por ano
ggplot(noticias) + geom_bar(aes(x=ano))

# numero de noticias por ano x portal
ggplot(noticias) + geom_bar(aes(x=ano, fill=subFonte), position = "dodge")

# numero de noticias por mes x ano por portal
ggplot(noticias) + geom_bar(aes(x=mes, fill=subFonte), position = "dodge") + facet_wrap(~ano)

# numero de noticias por dia x mes x ano por portal
ggplot(noticias) + geom_bar(mapping = aes(x=dia, group=subFonte, color=subFonte), position = "dodge") + facet_grid(ano ~ mes)
ggplot(noticias) + geom_density(mapping = aes(x = data, ..count.., group=subFonte, color = subFonte))

# correlacao das noticias por dia
correlacao_n_noticias_portal <- noticias %>% 
  select(data, subFonte) %>% 
  table() %>% 
  correlate()

# repercussao por dia x mes x ano por portal
repercussao_dia <- noticias %>% mutate(ano = year(data), mes = month(data), dia = day(data), dia_do_ano = yday(data)) %>% 
  group_by(ano, mes, dia, subFonte) %>% 
  summarise(n_comentarios = sum(repercussao))

ggplot(repercussao_dia) + geom_jitter(mapping = aes(x=dia, y=n_comentarios, group=subFonte, color=subFonte)) + facet_grid(ano ~ mes)

# media de noticias por mes 
## melhor solucao eh uma linha da figura para cada portal. cada mes de cada portal eh representado por uma barra e a barra tem uma linha representando o desvio padrao.
n_noticias_mes_ano <- noticias %>% group_by(ano, mes, subFonte) %>% summarise(numero = n())
noticias_mes <- n_noticias_mes_ano %>% group_by(mes, subFonte) %>% summarise(media = mean(numero), dp = sd(numero), mediana = median(numero))

ggplot(noticias_mes) + geom_pointrange(mapping = aes(subFonte, media, ymin=media-dp, ymax=media+dp)) + facet_wrap(~mes)

# media de comentarios por mes

# um grafico com barras indicando a quantidade de noticias que cada portal colocou no mes, a quantidade de noticias media (por mes) do ano e a quantidade media dos 7 anos coletados
## Misturar o grafico de barras dodge com o stack ou usar o stack e passar duas linhas indicando a media do ano e a do geral http://r4ds.had.co.nz/data-visualisation.html#position-adjustments







# para cada portal
## distribuicao do numero de noticias por dia (dd/mm/yy), numero de noticias por ano x meses (algo como violin plot), numero de noticias meses x dia, 
## numero de comentarios no tempo (mesma coisa que foi feita para noticias, agora para o numero de comentarios)


## palavras mais utilizadas nas noticias no tempo
## sentimento das noticias ao longo do tempo
## polaridade das noticias ao longo do tempo (analise de valencias entra aqui?)

