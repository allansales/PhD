library("dplyr")
library("wordVectors")
library("partitions")
library("gtools")
library("purrr")

## Funcoes para o calculo do vies
# Calcula score de uma palavra para os conjuntos A e B
cosSim_model <- function(modelo, w, col){
  cosineSimilarity(modelo[[w]], modelo[[col]])
}

score_w <- function(w, a, b, modelo){
  
  mean_cosSim = function(w, palavras){
    features = tibble(palavras = palavras)
    
    mean = features %>% group_by(palavras) %>% 
      summarise(cosine = cosSim_model(modelo, w, palavras)) %>% 
      summarise(mean = mean(cosine))  
    
    return(mean)
  }
  
  mean_w_A = mean_cosSim(w, a)
  mean_w_B = mean_cosSim(w, b)
  
  return((mean_w_A - mean_w_B) %>% as.numeric())
}

score_targets <- function(x, y, a, b, modelo){
  
  sum_s_w = function(palavras){
    scores = palavras %>% map(function(x) score_w(x, a, b, modelo)) %>% unlist() %>% sum()
    return(scores)
  }
  
  sum_w_X = sum_s_w(x)
  sum_w_Y = sum_s_w(y)
  
  return(sum_w_X - sum_w_Y)
}

## Teste de permutacao
permutacao <- function(x, y){
  
  remove_repeated_sets = function(Xi, Yi){
    duplicados = duplicated(Yi)
    Xi = Xi[!duplicados,]
    Yi = Yi[!duplicados,]
    return(list(Xi = Xi, Yi = Yi))
  }
  
  all_targets = c(x,y)
  n = length(all_targets)
  Xi = permutations(n=n, r=n/2, v=all_targets) %>% as.data.frame()
  colnames(Xi) = paste("X",colnames(Xi),sep = "")
  
  Yi = Xi %>% apply(1, FUN=function(x){
    setdiff(all_targets, x)
  }) %>% t() %>% as.data.frame()
  
  colnames(Yi) = paste("Y",colnames(Yi),sep = "")
  
  return(remove_repeated_sets(Xi, Yi))
}

score_permutacoes <- function(Xi, Yi, a, b, modelo, name){
  
  cols_in_Xi = colnames(Xi)
  cols_in_Yi = colnames(Yi)
  
  targets_permuted = bind_cols(Xi, Yi) 
  
  scores_permutacao = targets_permuted %>% apply(1, FUN = function(x){
    Xi_values = x[which(names(x) %in% cols_in_Xi)]
    Yi_values = x[which(names(x) %in% cols_in_Yi)]

    return(score_targets(Xi_values, Yi_values, a, b, modelo))
  })

  return(scores_permutacao)
}

## Tamanho do efeito
effect_size <- function(x, y, a, b, modelo){
  
  s_w = function(palavras){
    targets = tibble(palavras = palavras)

    targets %>% group_by(palavras) %>% 
      summarise(s_w = score_w(palavras, a, b, modelo))
  }
  
  x = s_w(x)
  y = s_w(y)
  
  x_mean = x %>% summarise(mean = mean(s_w)) %>% as.numeric()
  y_mean = y %>% summarise(mean = mean(s_w)) %>% as.numeric()
  
  w_sd = bind_rows(x, y) %>% summarise(sd = sd(s_w)) %>% as.numeric()
  
  return((x_mean - y_mean)/w_sd)
}

## WEFAT
w_wefat <- function(w, a, b, modelo){
  
  numerador = score_w(w, a, b, modelo)
  
  s_w = function(palavras){
    
    features = tibble(palavras = palavras)
    
    features %>% group_by(palavras) %>% 
      summarise(s = cosSim_model(modelo, w, palavras))  
  }
  
  s_a = s_w(a)
  s_b = s_w(b)
  
  w_wefat = bind_rows(s_a, s_b) %>% summarise(sd = sd(s)) %>% as.numeric()
  return(w_wefat)
}

wefat <- function(x, y, a, b, modelo){
  
  get_w_wefat = function(palavras){
    targets = tibble(palavras = palavras)
    
    targets %>% group_by(palavras) %>% 
      summarise(w_wefat = w_wefat(palavras, a, b, modelo))
  }
  
  w_wefat_x = get_w_wefat(x)
  w_wefat_y = get_w_wefat(y)
  
  w_wefat = bind_rows(w_wefat_x, w_wefat_y) %>% arrange(-w_wefat)
  return(w_wefat)
}

pvalor <- function(scores_Xi_Yi, score_X_Y){
  tbl = data_frame(value = scores_Xi_Yi > score_X_Y)
  n_true = tbl %>% filter(value == T) %>% summarise(cont = n()) %>% as.numeric()
  n_total = nrow(tbl)
  return(n_true/n_total)
}

## Verifica se todas as palavras dos dataframes estao contidas no modelo
modelo_contem_palavra <- function(features, targets, modelo){
  
  checa_palavra_em_modelo <- function(w, modelo){
    wv = modelo[[w]]
    contem = (is.nan(wv[1,1]) == F) #se vetor contem nan, entao nao existe a palavra no modelo
    return(contem)
  }
  
  ok_a = features %>% group_by(get("A")) %>% summarise(palavras_do_modelo = checa_palavra_em_modelo(get("A"), modelo))
  ok_b = features %>% group_by(get("B")) %>% summarise(palavras_do_modelo = checa_palavra_em_modelo(get("B"), modelo))
  
  ok_x = targets %>% group_by(get("X")) %>% summarise(palavras_do_modelo = checa_palavra_em_modelo(get("X"), modelo))
  ok_y = targets %>% group_by(get("Y")) %>% summarise(palavras_do_modelo = checa_palavra_em_modelo(get("Y"), modelo))
  
  all_true_a = (ok_a %>% distinct(palavras_do_modelo) %>% nrow() == 1 && ok_a %>% distinct(palavras_do_modelo) == T)
  if(!all_true_a){
    print(ok_a)
  }
  
  all_true_b = (ok_b %>% distinct(palavras_do_modelo) %>% nrow() == 1 && ok_b %>% distinct(palavras_do_modelo) == T)
  if(!all_true_b){
    print(ok_b)
  }
  
  all_true_x = (ok_x %>% distinct(palavras_do_modelo) %>% nrow() == 1 && ok_x %>% distinct(palavras_do_modelo) == T)
  if(!all_true_x){
    print(ok_x)
  }
  
  all_true_y = (ok_y %>% distinct(palavras_do_modelo) %>% nrow() == 1 && ok_y %>% distinct(palavras_do_modelo) == T)
  if(!all_true_y){
    print(ok_y)
  }
  
  all_true = ((all_true_a && all_true_b) && (all_true_x && all_true_y))
  return(all_true)
}