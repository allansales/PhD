library(mongolite)

library(dplyr)
library(ggplot2)

library(lubridate)
library(anytime)

get_collection <- function(colecao){
  con <- mongo(db = "stocks", collection = colecao, url = "mongodb://localhost", verbose = FALSE, options = ssl_options())
  data <- con$find()
  rm(con)
  return(data)
}

get_todas_noticias <- function(){
  g1_noticias <- get_collection("g1Noticias")
  folha_noticias <- get_collection("folhaNoticias")
  estadao_noticias <- get_collection("estadaoNoticias")
  
  noticias <- bind_rows(g1_noticias, folha_noticias, estadao_noticias)
  anos_eleicao <- c(2010,2012,2014,2016)
  noticias <- noticias %>% mutate(data = utcdate(timestamp), repercussao = as.integer(repercussao), 
                                  ano = year(data), mes = month(data, label=T, abbr=T), dia = day(data), dia_do_ano = yday(data),
                                  is_ano_eleicao = if_else(ano %in% anos_eleicao, TRUE, FALSE))
  
  return(noticias)  
}

get_todos_comentarios <- function(){
  g1_comentarios <- get_collection("g1Comentarios")
  folha_comentarios <- get_collection("folhaComentarios")
  estadao_comentarios <- get_collection("estadaoComentarios")
  
  comentarios <- bind_rows(g1_comentarios, folha_comentarios, estadao_comentarios)
  
  return(comentarios)  
}