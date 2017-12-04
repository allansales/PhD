library("dplyr")
library("wordVectors")
library("partitions")

## Funcoes para o calculo do vies
# Calcula score de uma palavra para os conjuntos A e B
cosSim_model <- function(modelo, w, col){
  cosineSimilarity(modelo[[w]], modelo[[col]])
}

score_w <- function(w, features, modelo){
  
  mean_cosSim = function(w, col){
    
    mean = features %>% group_by(get(col)) %>% 
      summarise(cosine = cosSim_model(modelo, w, get(col))) %>% 
      summarise(mean = mean(cosine))  
    
    return(mean)
  }
  
  mean_w_A = mean_cosSim(w, "A")
  mean_w_B = mean_cosSim(w, "B")
  
  return((mean_w_A - mean_w_B) %>% as.numeric())
}

score_targets <- function(targets, features, modelo){
  
  sum_s_w = function(col, features, modelo){
    targets %>% group_by(get(col)) %>% 
      summarise(s_w = score_w(get(col), features, modelo)) %>%
      summarise(s = sum(s_w))  
  }
  
  sum_w_X = sum_s_w("X", features, modelo)
  sum_w_Y = sum_s_w("Y", features, modelo)
  return(sum_w_X - sum_w_Y)
}

## Teste de permutacao
## TO DO

## Tamanho do efeito
effect_size <- function(targets, features, modelo){
  
  s_w = function(col){
    targets %>% group_by(get(col)) %>% 
      summarise(s_w = score_w(get(col), features, modelo))
  }
  
  x = s_w("X")
  y = s_w("Y")
  
  x_mean = x %>% summarise(mean = mean(s_w)) %>% as.numeric()
  y_mean = y %>% summarise(mean = mean(s_w)) %>% as.numeric()
  
  w_sd = bind_rows(x, y) %>% summarise(sd = sd(s_w)) %>% as.numeric()
  
  return((x_mean - y_mean)/w_sd)
}

## WEFAT
w_wefat <- function(w, features, modelo){
  
  numerador = score_w(w, features, modelo)
  
  s_w = function(modelo, w, col){
    features %>% group_by(get(col)) %>% 
      summarise(s = cosSim_model(modelo, w, get(col)))  
  }
  
  s_a = s_w(modelo, w, "A")
  s_b = s_w(modelo, w, "B")
  
  w_wefat = bind_rows(s_a, s_b) %>% summarise(sd = sd(s)) %>% as.numeric()
  return(w_wefat)
}

wefat <- function(targets, features, modelo){
  
  get_w_wefat = function(col){
    targets %>% group_by(get(col)) %>% summarise(w_wefat = w_wefat(get(col), features, modelo))
  }
  
  w_wefat_x = get_w_wefat("X")
  w_wefat_y = get_w_wefat("Y")
  
  w_wefat = bind_rows(w_wefat_x, w_wefat_y) %>% rename(palavra = "get(col)") %>% arrange(-w_wefat)
  return(w_wefat)
}
