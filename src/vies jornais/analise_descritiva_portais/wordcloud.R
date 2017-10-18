source("mongo_utils.R")
library(tm)
library(wordcloud2)
library(readr)

noticias_estadao <- get_collection("estadaoNoticias")
noticias_folha <- get_collection("folhaNoticias")

conteudo_estadao <- select(noticias_estadao, conteudo)
conteudo_folha <- select(noticias_folha, conteudo)

titulo_estadao <- select(noticias_estadao, titulo)
titulo_folha <- select(noticias_folha, titulo)

gera_tabela_frequencias <- function(texto){
  
  texto <- Corpus(VectorSource(texto))
  texto <- tm_map(texto, tolower)
  texto <- tm_map(texto, removePunctuation, preserve_intra_word_dashes = TRUE) 
  texto <- tm_map(texto, removeWords, stopwords("pt")) 
  texto <- tm_map(texto, removeNumbers) 
  texto <- tm_map(texto, stripWhitespace) 
  texto <- tm_map(texto, stemDocument)
  texto <- tm_map(texto, PlainTextDocument)
  
  
  dtm <- TermDocumentMatrix(texto)
  matriz <- as.matrix(dtm)
  vector <- sort(rowSums(matriz),decreasing=TRUE)
  data <- data.frame(word = names(vector),freq=vector)

  return(data)  
}

#tf_conteudo_estadao <- gera_tabela_frequencias(conteudo_estadao)
#tf_conteudo_folha <- gera_tabela_frequencias(conteudo_folha)
#write_csv(tf_conteudo_estadao, "frequencia_palavras_estadao.csv")
#write_csv(tf_conteudo_folha, "frequencia_palavras_folha.csv")

tf_estadao <- read_csv("frequencia_palavras_estadao.csv")
tf_folha <- read_csv("frequencia_palavras_folha.csv")

wordcloud2(head(tf_estadao,100),  fontFamily = 'Segoe UI', size = 1, color = "random-light")
wordcloud2(head(tf_folha,100),  fontFamily = 'Segoe UI', size = 1, color = "random-light")

tf_titulo_estadao <- gera_tabela_frequencias(titulo_estadao)
tf_titulo_folha <- gera_tabela_frequencias(titulo_folha)

wordcloud2(head(tf_titulo_estadao,100),  fontFamily = 'Segoe UI', size = 1, color = "random-light")
wordcloud2(head(tf_titulo_folha,100),  fontFamily = 'Segoe UI', size = 1, color = "random-light")
