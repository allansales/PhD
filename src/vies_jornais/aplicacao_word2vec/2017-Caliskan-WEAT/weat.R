library("dplyr")
library("wordVectors")
library("partitions")
library("gtools")
library("purrr")
library("perm")

cosSim_model <- function(w1, w2, modelo){
  cosineSimilarity(modelo[[w1]],modelo[[w2]]) %>% as.numeric()
}

create_pares <- function(x, y, a, b, modelo){
  targets = c(x, y)
  features = c(a, b)
  
  pares = expand.grid(target = targets, feature = features)
  
  pares$target = pares$target %>% as.character()
  pares$feature = pares$feature %>% as.character()
  
  pares = pares %>%
    rowwise() %>%
    mutate(cos_sim = cosSim_model(target, feature, modelo))
  
  targets_a = pares %>% filter(feature %in% a)
  targets_b = pares %>% filter(feature %in% b)
  return(list(targets_a = targets_a, targets_b = targets_b))
}

create_cosine_metrics <- function(targets_a, targets_b){ #tabela de diferenca de similaridades das palavras de x e y para os conjuntos a e b

  w_sim_a = targets_a %>% group_by(target) %>% summarise(mean_cos = mean(cos_sim)) %>% arrange(target)
  w_sim_b = targets_b %>% group_by(target) %>% summarise(mean_cos = mean(cos_sim)) %>% arrange(target)

  w_dif_a_b = bind_cols(w_sim_a %>% select(target), #poderia usar w_sim_b. as duas terao a mesma ordem
                        (w_sim_a %>% select(mean_cos) -
                           w_sim_b %>% select(mean_cos)))

  w_dif_a_b = w_dif_a_b %>% rename(mean_dif_cos = mean_cos)

  return(w_dif_a_b)
}


permutacao <- function(x, y){
  
  all_targets = c(x,y)
  n = length(all_targets)
  Xi = combinat::combn(all_targets, n/2) %>% t() %>% as.data.frame()
  colnames(Xi) = paste("X",colnames(Xi),sep = "")

  Yi = Xi %>% apply(1, FUN=function(x){
    setdiff(all_targets, x)
  }) %>% t() %>% as.data.frame()
  colnames(Yi) = paste("Y",colnames(Yi),sep = "")
  
  return(list(Xi = Xi, Yi = Yi))
}

effect_size <- function(x, y, dif_sim_table){
  
  x_mean = dif_sim_table %>% filter(target %in% x) %>% summarise(mean_dif = mean(mean_dif_cos)) %>% as.numeric()
  y_mean = dif_sim_table %>% filter(target %in% y) %>% summarise(mean_dif = mean(mean_dif_cos)) %>% as.numeric()
  
  w_sd = dif_sim_table %>% summarise(sd = sd(mean_dif_cos)) %>% as.numeric()
  
  return((x_mean - y_mean)/w_sd)
}

wefat <- function(dif_sim_table, w_cos_dist){
  
  dif_sim_table = dif_sim_table %>% arrange(target)
  w_sd = w_cos_dist %>% group_by(target) %>% summarise(sd = sd(cos_sim)) %>% arrange(target)
  
  w_wefat = bind_cols(dif_sim_table %>% select(target),
                      dif_sim_table %>% select(mean_dif_cos)/
                      w_sd %>% select(sd))
  
  return(w_wefat)
}

execute <- function(x, y, targets_a, targets_b){
  organize_input = function(x, y){
    X = x %>% t() %>% as_data_frame()
    Y = y %>% t() %>% as_data_frame()
    table = list(Xi = X, Yi = Y)
  }

  dif_sim_table = create_cosine_metrics(targets_a, targets_b)
  
  p_test = permutation_test(dif_sim_table, x, y)
  
  p_valor = p_test$p.value
  score_x_y = p_test$estatistica
  
  tam_efeito = effect_size(x, y, dif_sim_table)

  valores = data_frame(p_valor, tam_efeito)
  return(list(valores = valores, score_X_Y = score_x_y))
}

permutation_test <- function(dif_sim_table, x, y){
  xi = dif_sim_table %>% filter(target %in% x)
  yi = dif_sim_table %>% filter(target %in% y)
  
  permtest = permTS(xi$mean_dif_cos, yi$mean_dif_cos, alternative = "greater")
  return(list(p.value = permtest$p.value, estatistica = permtest$statistic))
}

## Escolhe palavras para definir um conjunto
get_palavras_proximas <- function(alvo, sim_boundary, word_embedding){
  # 200 poderia ser qualquer numero. foi escolhido apenas para garantir que teriamos muitas palavras a ser filtradas
  words = closest_to(word_embedding, word_embedding[[alvo]], 200)
  colnames(words)[2] = "similarity"
  words = words %>% filter(similarity >= sim_boundary)
  return(words)
}

define_palavras <- function(palavras_1, palavras_2){
  cut = min(length(palavras_1), length(palavras_2))
  palavras_1 = palavras_1[1:cut]
  palavras_2 = palavras_2[1:cut]
  return(list(conjunto_1 = palavras_1, conjunto_2 = palavras_2))
}

define_conjunto <- function(palavra_1, palavra_2, limiar, modelo, modelo_consulta){
  
  palavras_1 = get_palavras_proximas(palavra_1, limiar, modelo)
  palavras_2 = get_palavras_proximas(palavra_2, limiar, modelo)
  
  contem_1 = modelo_contem_palavra(palavras_1, modelo_consulta)
  contem_2 = modelo_contem_palavra(palavras_2, modelo_consulta)
  
  conjuntos = define_palavras(contem_1$word, contem_2$word)
  return(conjuntos)
}

## Verifica se todas as palavras dos dataframes estao contidas no modelo
modelo_contem_palavra <- function(palavras_sim, modelo){
  
  checa_palavra_em_modelo <- function(w, modelo){
    wv = modelo[[w]]
    contem = (is.nan(wv[1,1]) == F) #se vetor contem nan, entao nao existe a palavra no modelo
    return(contem)
  }
  
  contem = palavras_sim %>% group_by(word) %>% 
    filter(checa_palavra_em_modelo(word, modelo)) 
  
  return(contem)
}