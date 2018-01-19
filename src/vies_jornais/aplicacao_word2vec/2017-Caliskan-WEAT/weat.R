library("dplyr")
library("wordVectors")
library("partitions")
library("gtools")
library("purrr")

cosSim_model <- function(w1, w2, modelo){
  cosineSimilarity(modelo[[w1]],modelo[[w2]]) %>% as.numeric()
}

# create_cosine_metrics <- function(x, y, a, b, modelo){ #tabela de diferenca de similaridades das palavras de x e y para os conjuntos a e b
#   
#   targets = c(x, y)
#   features = c(a, b)
#   
#   pares = expand.grid(target = targets, feature = features)
#   
#   pares$target = pares$target %>% as.character()
#   pares$feature = pares$feature %>% as.character()
#   
#   pares = pares %>%
#     rowwise() %>% 
#     mutate(cos_sim = cosSim_model(target, feature, modelo))
#   
#   targets_a = pares %>% filter(feature %in% a)
#   targets_b = pares %>% filter(feature %in% b)
#   
#   w_sim_a = targets_a %>% group_by(target) %>% summarise(mean_cos = mean(cos_sim)) %>% arrange(target)
#   w_sim_b = targets_b %>% group_by(target) %>% summarise(mean_cos = mean(cos_sim)) %>% arrange(target)
#   
#   w_dif_a_b = bind_cols(w_sim_a %>% select(target), #poderia usar w_sim_b. as duas terao a mesma ordem
#                         (w_sim_a %>% select(mean_cos) - 
#                            w_sim_b %>% select(mean_cos)))
#  
#   w_dif_a_b = w_dif_a_b %>% rename(mean_dif_cos = mean_cos)
# 
#   return(list(dif_sim_table = w_dif_a_b, w_cos_dist = pares))
# }

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

score <- function(tables, dif_sim_table){

  sum_w_sim = function(words_set, dif_sim_table){
    dif_sim_table %>% filter(target %in% words_set) %>% summarise(sum_sim = sum(mean_dif_cos)) %>% as.numeric()
  }
  
  score_Xi = tables$Xi %>% apply(1, sum_w_sim, dif_sim_table)
  score_Yi = tables$Yi %>% apply(1, sum_w_sim, dif_sim_table)
  score_Xi_Yi = score_Xi - score_Yi
  
  return(bind_cols(tables$Xi, tables$Yi, score = score_Xi_Yi))
}

pvalor <- function(scores_permutacao, score_x_y){
  tbl = data_frame(value = scores_permutacao$score > score_x_y$score)
  
  n_true = tbl %>% filter(value == T) %>% summarise(cont = n()) %>% as.numeric() 
  n_total = nrow(tbl)-1 #menos 1 porque uma das linhas do denominador tera o mesmo valor que score_x_y$score
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

execute <- function(x, y, a, b, permutacoes, targets_a, targets_b, modelo){
  
  organize_input = function(x, y){
    X = x %>% t() %>% as_data_frame()
    Y = y %>% t() %>% as_data_frame()
    table = list(Xi = X, Yi = Y)
  }

  dif_sim_table = create_cosine_metrics(targets_a, targets_b)

  entrada_x_y = organize_input(x, y)
  score_x_y = score(entrada_x_y, dif_sim_table)
  
#  print(permutacoes)
  scores_permutacao = score(permutacoes, dif_sim_table) %>% arrange(-score)
  
  p_valor = pvalor(scores_permutacao, score_x_y)
  tam_efeito = effect_size(x, y, dif_sim_table)

  valores = data_frame(p_valor, tam_efeito)
  
  return(list(valores = valores, scores_permutacao = scores_permutacao, score_X_Y = score_x_y))
}


## Escolhe palavras para definir um conjunto
get_palavras_proximas <- function(alvo, sim_boundary, word_embedding){
  # 200 poderia ser qualquer numero. foi escolhido apenas para garantir que teriamos muitas palavras a ser filtradas
  words = closest_to(word_embedding, word_embedding[[alvo]], 200)
  colnames(words)[2] = "similarity"
  words = words %>% filter(similarity >= sim_boundary)
  return(words$word)
}

#possivel de ser o metodo para pegar palavras proximas
# library("fastrtext")
# path = "../word_embeddings/wikipedia_portugues/embeddings/wiki.pt.bin"
# we_wikipedia = load_model(path)
# 
# 
# aecio_10 = get_nn(we_wikipedia, "psd", 20)
# aecio_10

define_palavras <- function(palavras_1, palavras_2){
  cut = min(length(palavras_1), length(palavras_2))
  palavras_1 = palavras_1[1:cut]
  palavras_2 = palavras_2[1:cut]
  return(list(conjunto_1 = palavras_1, conjunto_2 = palavras_2))
}

define_conjunto <- function(palavra_1, palavra_2, limiar, modelo){
  palavras_1 = get_palavras_proximas(palavra_1, limiar, modelo)
  palavras_2 = get_palavras_proximas(palavra_2, limiar, modelo)
  conjuntos = define_palavras(palavras_1, palavras_2)
  return(conjuntos)
}

## Verifica se todas as palavras estao contidas no vocabulario dos portais
checa_vies <- function(x, y, a, b, permutacoes, targets_a, targets_b, modelo){
  contem = modelo_contem_palavra(x, y, a, b, modelo)
  if(contem){
    resultados_vies = execute(x, y, a, b, permutacoes, targets_a, targets_b, modelo)
    return(resultados_vies)  
  }
}

## Verifica se todas as palavras dos dataframes estao contidas no modelo
modelo_contem_palavra <- function(x, y, a, b, modelo){
  
  checa_palavra_em_modelo <- function(w, modelo){
    wv = modelo[[w]]
    contem = (is.nan(wv[1,1]) == F) #se vetor contem nan, entao nao existe a palavra no modelo
    return(contem)
  }
  
  targets = data_frame(X = x, Y = y)
  features = data_frame(A = a, B = b)
  
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