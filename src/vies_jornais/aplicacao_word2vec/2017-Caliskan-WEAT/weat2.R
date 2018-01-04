library("dplyr")
library("wordVectors")
library("partitions")
library("gtools")
library("purrr")

cosSim_model <- function(w1, w2, modelo){
  cosineSimilarity(modelo[[w1]],modelo[[w2]]) %>% as.numeric()
}

create_cosine_metrics <- function(x, y, a, b, modelo){ #tabela de diferenca de similaridades das palavras de x e y para os conjuntos a e b
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
  
  w_sim_a = targets_a %>% group_by(target) %>% summarise(mean_cos = mean(cos_sim)) %>% arrange(target)
  w_sim_b = targets_b %>% group_by(target) %>% summarise(mean_cos = mean(cos_sim)) %>% arrange(target)
  
  w_dif_a_b = bind_cols(w_sim_a %>% select(target), #poderia usar w_sim_b. as duas terao a mesma ordem
                        (w_sim_a %>% select(mean_cos) - 
                           w_sim_b %>% select(mean_cos)))
 
  w_dif_a_b = w_dif_a_b %>% rename(mean_dif_cos = mean_cos)

  return(list(dif_sim_table = w_dif_a_b, w_cos_dist = pares))
}

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

score <- function(tables, dif_sim_table){

  sum_w_sim = function(words_set, dif_sim_table){
    dif_sim_table %>% filter(target %in% words_set) %>% summarise(sum_sim = sum(mean_dif_cos)) %>% as.numeric()
  }
  
  score_Xi = tables$Xi %>% apply(1, sum_w_sim, dif_sim_table)
  score_Yi = tables$Yi %>% apply(1, sum_w_sim, dif_sim_table)
  score_Xi_Yi = score_Xi - score_Yi
  
  return(bind_cols(tables$Xi, tables$Yi, score = score_Xi_Yi))
}

pvalor <- function(x, y, scores_permutacao, dif_sim_table){
  X = x %>% t() %>% as_data_frame()
  Y = y %>% t() %>% as_data_frame()
  table = list(Xi = X, Yi = Y)
  score_x_y = score(table, dif_sim_table)
  
  tbl = data_frame(value = scores_permutacao$score > score_x_y$score)
  n_true = tbl %>% filter(value == T) %>% summarise(cont = n()-1) %>% as.numeric() #menos 1 porque uma das linhas do denominador tera o mesmo valor que score_x_y$score
  n_total = nrow(tbl)
  return(n_true/n_total)
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