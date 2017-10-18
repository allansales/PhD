source("mongo_utils.R")
library(corrr)

noticias <- get_todas_noticias()

# geral
summary(noticias)

### descricao noticias
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

# medias e desvios padroes dos portais por mes
n_noticias_mes_ano <- noticias %>% group_by(ano, mes, subFonte) %>% summarise(numero = n())
noticias_mes <- n_noticias_mes_ano %>% group_by(mes, subFonte) %>% summarise(media = mean(numero), dp = sd(numero), mediana = median(numero))

ggplot(noticias_mes) + geom_pointrange(mapping = aes(subFonte, media, ymin=media-dp, ymax=media+dp)) + facet_wrap(~mes) 

# noticias por mes para comparar as medias e desvios padroes dos portais com eles mesmos
ggplot(noticias_mes, aes(mes, media)) + geom_col() + geom_pointrange(aes(ymin=media-dp, ymax=media+dp)) + facet_wrap(~subFonte, dir = "v")

# intervalo de confianca de quantidade de noticias por mes
ic_noticias_mes <- n_noticias_mes_ano %>% group_by(mes, subFonte) %>% 
  summarise(media = mean(numero), erro = qt(0.975,df=n()-1)*sd(numero)/sqrt(n()))

ggplot(ic_noticias_mes, aes(mes, media)) + geom_crossbar(aes(ymin=media-erro, ymax=media+erro)) + facet_wrap(~subFonte, dir = "v")
ggplot(ic_noticias_mes) + geom_pointrange(aes(subFonte, media, ymin=media-erro, ymax=media+erro)) + facet_wrap(~mes) 

## separando por ano de eleicao
# intervalo de confianca de quantidade de noticias por mes
n_noticias_mes_ano_eleicao <- noticias %>% group_by(ano, mes, is_ano_eleicao, subFonte) %>% summarise(numero = n())

ic_noticias_mes_eleicao <- n_noticias_mes_ano_eleicao %>% group_by(mes, is_ano_eleicao, subFonte) %>% 
  summarise(media = mean(numero), erro = qt(0.975,df=n()-1)*sd(numero)/sqrt(n()))

ggplot(ic_noticias_mes_eleicao) + geom_pointrange(mapping = aes(subFonte, media, ymin=media-erro, ymax=media+erro, color=is_ano_eleicao), position = position_dodge(width = 0.5)) + facet_wrap(~mes)