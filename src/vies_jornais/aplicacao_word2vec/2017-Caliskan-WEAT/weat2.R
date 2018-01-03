library("dplyr")
library("wordVectors")
library("partitions")
library("gtools")
library("purrr")

cosSim_model <- function(w1, w2, modelo){
  cosineSimilarity(modelo[[w1]],modelo[[w2]]) %>% as.numeric()
}

similarity_table <- function(x, y, a, b, modelo){
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

sim_table = similarity_table(x, y, a, b, modelo)
permutacao_table = permutacao(x, y)

targets_a = sim_table$targets_a
targets_b = sim_table$targets_b

w_sim_a = targets_a %>% group_by(target) %>% summarise(mean_cos = mean(cos_sim)) %>% arrange(target)
w_sim_b = targets_b %>% group_by(target) %>% summarise(mean_cos = mean(cos_sim)) %>% arrange(target)

w_dif_a_b = bind_cols(w_sim_a %>% select(target), #poderia usar w_sim_b. as duas terao a mesma ordem
                      (w_sim_a %>% select(mean_cos) - 
                       w_sim_b %>% select(mean_cos)))

sum_w_sim <- function(words_set, w_dif_a_b){
  w_dif_a_b %>% filter(target %in% words_set) %>% summarise(sum_sim = sum(mean_cos)) %>% as.numeric()
}

score_Xi = permutacao_table$Xi %>% apply(1, sum_w_sim, w_dif_a_b)
#score_Xi = permutacao_table$Xi %>% rowwise() %>% mutate(score = sum_w_sim(.,w_dif_a_b))
score_Yi = permutacao_table$Yi %>% apply(1, sum_w_sim, w_dif_a_b)
#score_Yi = permutacao_table$Yi %>% rowwise() %>% mutate(score = sum_w_sim(.,w_dif_a_b))
score_Xi_Yi = score_Xi - score_Yi

permutation_scores = bind_cols(permutacao_table$Xi, permutacao_table$Yi, score = score_Xi_Yi)
