library(readr)
library(wordVectors)

cria_word_embedding <- function(tema, n_layers = 300, analogias = NULL, noticias, texto_col, path_saida){
  train_file <- paste(path_saida,tema,".csv",sep="")
  binary_file <- paste(path_saida,tema,".bin",sep="")
  noticias %>% select_(texto_col) %>% write_csv(train_file)
  modelo <- train_word2vec(train_file, threads = 8, vectors = n_layers)
  
  ### Regras que devem ser acertadas para validar embedding
  if(!is.null(analogias)){
    analogias = verifica_analogias(binary_file, analogias, respostas)  
  }
  
  ### Cria tsv com embedding
  #path_modelo_tsv = paste(path_saida,tema,".tsv",sep="")
  #modelo@.Data %>% as.data.frame() %>% write_tsv(path_modelo_tsv)
  
  #path_modelo_names_tsv = paste(path_saida,tema,"_names",".tsv",sep="")
  #modelo@.Data %>% rownames() %>% as.data.frame() %>% write_tsv(path_modelo_names_tsv)
  
  return(list(modelo, analogias))
}

