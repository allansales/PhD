library("rword2vec")

# Verifica a validade das analogias e retorna um data frame com os resultados
verifica_analogias <- function(bin_path, analogias, respostas){
  
  # Verifica se a saida esperada de uma analogia corresponde a saida que o algoritmo esta retornando
  # TRUE se a saida esperada eh igual ao do algoritmo, FALSE caso contrario
  word2vec_resposta <- function(binary_file, analogia, saida_esperada){
    word_analogy(file_name = binary_file, search_words = analogia) %>% 
      slice(1) %>% 
      select(word) %>% 
      .[[1]] %>% 
      as.character()
  }
  
  analogia_resposta <- data_frame(analogia = analogias, resposta_esperada = respostas) %>% rowwise() %>% 
    mutate(w2v_resposta = word2vec_resposta(bin_path, analogia, resposta_esperada), acertos = (resposta_esperada == w2v_resposta))

  acuracia <- analogia_resposta %>%
    count(acertos) %>%
    mutate(freq = n / sum(n)) %>% 
    filter(acertos == T) %>% select(freq) %>%
    as.numeric()
  
  
  return(list(analogia_resposta = analogia_resposta, acuracia = acuracia))
}

# Normaliza vetores: garante que a norma de um vetor tera valor 1
normaliza_vetor <- function(vetor_palavra){
  norma <- vetor_palavra %>% pracma::Norm()
  vetor_normalizado <- vetor_palavra/norma
  return(vetor_normalizado)
}

# Retorna as palavras que se repetiram mais que FREQUENCIA vezes no corpus
palavras_mais_frequentes <- function(path, frequencia){
  eleicao_tm <- scan(path, what = "character")
  
  threshold <- frequencia
  palavras_freq <- table(eleicao_tm) %>% data.frame() %>% filter(Freq >= threshold)
  palavras_usadas <- palavras_freq %>% .$eleicao_tm %>% as.character()
  return(palavras_usadas)
}

# Recebe um dataframe de pares de palavras e um word_embedding e calcula a proximidade entre as palavras de cada par
calcula_proximidade <- function(par_palavras, modelo, palavras_normalizadas_usadas){

  # Calcula a norma da diferenca entre duas palavras  
  proximidade <- function(modelo, palavra_1, palavra_2){
    palavra_1 <- palavra_1 %>% as.character()
    palavra_2 <- palavra_2 %>% as.character()
    embed_1 <- modelo[[palavra_1]]
    embed_2 <- modelo[[palavra_2]]
    dif_norma <- (embed_1 - embed_2) %>% pracma::Norm()
    return(dif_norma)
  }
  
  proximidade_par_palavras <- par_palavras %>% rowwise() %>% mutate(prox = proximidade(palavras_normalizadas_usadas, palavra_1, palavra_2))
  inverso <- proximidade_par_palavras %>% select(palavra_1 = palavra_2, palavra_2 = palavra_1, prox)
  proximidade_par_palavras <- bind_rows(proximidade_par_palavras, inverso)
  
  return(proximidade_par_palavras)
}

# Recebe um conjunto de palavras e retorna as combinacoes de possiveis de pares pares de palavras em um dataframe
forma_par_palavras <- function(palavras){
  par_palavras <- palavras %>% rownames() %>% combn(2) %>% t() %>% as.data.frame() %>% rename(palavra_1 = V1, palavra_2 = V2)
  return(par_palavras)
}

# Calcula o s_score para cada par de palavras em relacao as duas referencias do dataframe
calcula_s_score <- function(par_palavras, word_embedding, referencia_1, referencia_2){
  
  # Calcula o cosseno entre a-b e x-y
  s_score <- function(palavra_1, palavra_2, referencia_1, referencia_2, word_embedding){
    palavra_1 <- palavra_1 %>% as.character()
    palavra_2 <- palavra_2 %>% as.character()
    x <- word_embedding[[palavra_1]]
    y <- word_embedding[[palavra_2]]
    
    a <- word_embedding[[referencia_1]]
    b <- word_embedding[[referencia_2]]
    cos <- cosineSimilarity(x-y, a-b)
  }
  
  similaridade_palavras <- par_palavras %>% filter(prox <= limiar) %>% rowwise() %>% 
    mutate(similaridade_cos = s_score(palavra_1, palavra_2, referencia_1, referencia_2, word_embedding)) %>% 
    arrange(-similaridade_cos)
  
  return(similaridade_palavras)  
}

# Calcula o Direct Bias
direct_bias_calculator <- function(entities, g, c, word_embedding){
  entities <- data_frame(entidade = entities)
  entities_bias <- entities %>% rowwise() %>% mutate(cosine_c = cosineSimilarity(word_embedding[[entidade]], g) %>% .**c)
  direct_bias_weights <- entities_bias %>% na.omit()
  return(direct_bias_weights)
}

# Calcula o Indirect bias
indirect_bias_calculator <- function(entidade_1, entidade_2, g, word_embedding){
  
  multiply <- function(x, y){
    return(x*y)
  }
  
  wg_calculator <- function(entidade, g){
    wg <- (entidade %*% t(g)) %>% as.numeric() %>% multiply(g)
    return(wg)
  }
  
  wp_calculator <- function(entidade, g){
    wg <- wg_calculator(entidade, g)
    wp <- (entidade - wg)
    return(wp)
  }
  
  entidade_1 <- entidade_1 %>% as.character()
  w <- word_embedding[[entidade_1]] 
  
  entidade_2 <- entidade_2 %>% as.character() 
  v <- word_embedding[[entidade_2]]
  
  inner_product <- w %*% t(v)
  
  wp <- wp_calculator(w, g)
  vp <- wp_calculator(v, g)
  
  bias_influence <- (wp %*% t(vp))/(pracma::Norm(wp) * pracma::Norm(vp))
  
  indirect_bias <- (inner_product - bias_influence)/inner_product
  return(indirect_bias)
}
