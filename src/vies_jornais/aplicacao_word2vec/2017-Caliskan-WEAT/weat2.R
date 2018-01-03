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
  
  pares %>%
    rowwise() %>% 
    mutate(cos_sim = cosSim_model(target, feature, modelo))
}

get_sim_by_target_feature <- function(sim_table, w_target, w_feature){
  sim_table[sim_table$target == w_target & sim_table$feature == w_feature, "cos_sim"] %>% as.numeric()
}

sim_table = similarity_table(x, y, a, b, folha_up_down)

permutacao_table = permutacao(x, y)