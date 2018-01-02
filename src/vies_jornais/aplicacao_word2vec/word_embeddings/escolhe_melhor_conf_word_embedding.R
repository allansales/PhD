source("../utils/mongo_utils.R")
source("../utils/utils.R")
source("../utils/embeddings_utils.R")
library("wordVectors")
library("readr")

### Importa base de dados
noticias <- get_todas_noticias_processadas()
noticias_estadao <- noticias %>% filter(subFonte == "ESTADAO")
noticias_folha <- noticias %>% filter(subFonte == "FOLHASP")
noticias_g1 <- noticias %>% filter(subFonte == "G1")

### Noticias eleicao
partidos <- c("pmdb","ptb","pdt","pt","dem ","psb","psdb","ptc","psc","pmn","prp","pps","pv","pp","pstu","pcb","prtb","phs","psdc","pco","ptn","psl","prb","psol","ppl","pros","psd |psd[.]"," sd |sd[.]") #,"pr","pen", "pcdob", "ptdob"
candidatos <- c("dilma","aécio","levy","marina silva","luciana genro","eduardo jorge","josé maria","pastor everaldo", "iasi","eymael","rui costa","eduardo campos")

entity <- c(candidatos, partidos)
pattern <- paste(entity, collapse = "|")

# definicao dos diretorios
diretorio_saida = "saida_distancia_partidos/word_embeddings_configuracoes/eleicoes_2014"
train_file <- paste(diretorio_saida,".csv",sep="")
binary_file <- paste(diretorio_saida,".bin",sep="")
analogias <- c("pt psdb dilma", "pt pv dilma","pt prtb dilma","pt psol dilma","pt psb dilma","pt psdc dilma","psdb pv aécio","psdb prtb aécio","psdb psol aécio","psdb psb aécio","psdb psdc aécio","pv prtb jorge","pv psol jorge","pv psb jorge","pv psdc jorge","prtb psol fidelix","prtb psb fidelix","prtb psdc fidelix","psol psb luciana","psol psdc luciana","psb psdc marina","dilma aécio rousseff","campos psb aécio","pt psdb petista")
respostas <- c("aécio","jorge","fidelix","luciana","marina","eymael","jorge","fidelix","luciana","marina","eymael","fidelix","luciana","marina","eymael","luciana","marina","eymael","marina","eymael","eymael","neves","psdb","tucano")

### data drame com todas as configuracoes de embeddings
dados <- c("noticias_estadao","noticias_folha")
data_fim <- c("2014-12-31","2014-10-26")
data_inicio <- c("2014-05-30","2014-01-01")
n_layers <- c(100,200,300,400)
method <- c(0,1)
confs <- expand.grid(dados = dados, data_inicio = data_inicio, data_fim = data_fim,  n_layers = n_layers, method = method)

filter_noticias_por_data <- function(dados, data_inicio, data_fim){
  noticias = get(dados) %>% filter(timestamp >= data_inicio & timestamp <= data_fim) %>% noticias_tema(pattern, "titulo")
  return(noticias)
}

treina_modelo <- function(train_file, noticias, n_layers, method = 0){ # method 0 = skip-gram, 1 = bag of words
  noticias %>% select(conteudo_processado) %>% write_csv(train_file)
  train_word2vec(train_file, threads = 8, cbow = method, vectors = n_layers)
}

acuracia_analogias <- function(dados, data_inicio, data_fim, n_layers, train_file, binary_file, analogias, respostas, method){
  noticias = filter_noticias_por_data(dados, data_inicio, data_fim)
  modelo = treina_modelo(train_file, noticias, n_layers, method)
  analogias = verifica_analogias(binary_file, analogias, respostas)
  return(analogias$acuracia)
}

run <- function(dados, data_inicio, data_fim, n_layers, train_file, binary_file, analogias, respostas, method){
  dados = dados %>% as.character()
  data_inicio = data_inicio %>% as.character()
  data_fim = data_fim %>% as.character()
  acc = acuracia_analogias(dados, data_inicio, data_fim, n_layers, train_file, binary_file, analogias, respostas, method)
  return(acc)
}


### method = 0, skip-gram
conf_acuracia_estadao_1_2 <- confs %>% filter(dados == "noticias_estadao", n_layers %in% c(100, 200), method == 0) %>% rowwise() %>% mutate(acuracia = run(dados, data_inicio, data_fim, n_layers, train_file, binary_file, analogias, respostas, method))
write_csv(conf_acuracia_estadao_1_2, "acuracia_configuracao_embeddings/acuracia_configuracao_estadao_100_200.csv")

conf_acuracia_estadao_3_4 <- confs %>% filter(dados == "noticias_estadao", n_layers %in% c(300, 400), method == 0) %>% rowwise() %>% mutate(acuracia = run(dados, data_inicio, data_fim, n_layers, train_file, binary_file, analogias, respostas, method))
write_csv(conf_acuracia_estadao_3_4, "acuracia_configuracao_embeddings/acuracia_configuracao_estadao_300_400.csv")

conf_acuracia_folha_1_2 <- confs %>% filter(dados == "noticias_folha", n_layers %in% c(100, 200), method == 0) %>% rowwise() %>% mutate(acuracia = run(dados, data_inicio, data_fim, n_layers, train_file, binary_file, analogias, respostas, method))
write_csv(conf_acuracia_folha_1_2, "acuracia_configuracao_embeddings/acuracia_configuracao_folha_100_200.csv")

conf_acuracia_folha_3_4 <- confs %>% filter(dados == "noticias_folha", n_layers %in% c(300, 400), method == 0) %>% rowwise() %>% mutate(acuracia = run(dados, data_inicio, data_fim, n_layers, train_file, binary_file, analogias, respostas, method))
write_csv(conf_acuracia_folha_3_4, "acuracia_configuracao_embeddings/acuracia_configuracao_folha_300_400.csv")

### method = 1, cbow
conf_acuracia_estadao_1_2_cbow <- confs %>% filter(dados == "noticias_estadao", n_layers %in% c(100, 200), method == 1) %>% rowwise() %>% mutate(acuracia = run(dados, data_inicio, data_fim, n_layers, train_file, binary_file, analogias, respostas, method))
write_csv(conf_acuracia_estadao_1_2_cbow, "acuracia_configuracao_embeddings/acuracia_configuracao_estadao_100_200_cbow.csv")

conf_acuracia_estadao_3_4_cbow <- confs %>% filter(dados == "noticias_estadao", n_layers %in% c(300, 400), method == 1) %>% rowwise() %>% mutate(acuracia = run(dados, data_inicio, data_fim, n_layers, train_file, binary_file, analogias, respostas, method))
write_csv(conf_acuracia_estadao_3_4_cbow, "acuracia_configuracao_embeddings/acuracia_configuracao_estadao_300_400_cbow.csv")

conf_acuracia_folha_1_2_cbow <- confs %>% filter(dados == "noticias_folha", n_layers %in% c(100, 200), method == 1) %>% rowwise() %>% mutate(acuracia = run(dados, data_inicio, data_fim, n_layers, train_file, binary_file, analogias, respostas, method))
write_csv(conf_acuracia_folha_1_2_cbow, "acuracia_configuracao_embeddings/acuracia_configuracao_folha_100_200_cbow.csv")

conf_acuracia_folha_3_4_cbow <- confs %>% filter(dados == "noticias_folha", n_layers %in% c(300, 400), method == 1) %>% rowwise() %>% mutate(acuracia = run(dados, data_inicio, data_fim, n_layers, train_file, binary_file, analogias, respostas, method))
write_csv(conf_acuracia_folha_3_4_cbow, "acuracia_configuracao_embeddings/acuracia_configuracao_folha_300_400_cbow.csv")


conf_acuracia_estadao_1_2 <- read_csv("acuracia_configuracao_embeddings/acuracia_configuracao_estadao_100_200.csv")
conf_acuracia_estadao_3_4 <- read_csv("acuracia_configuracao_embeddings/acuracia_configuracao_estadao_300_400.csv")
conf_acuracia_folha_1_2 <- read_csv("acuracia_configuracao_embeddings/acuracia_configuracao_folha_100_200.csv")
conf_acuracia_folha_3_4 <- read_csv("acuracia_configuracao_embeddings/acuracia_configuracao_folha_300_400.csv")

conf_acuracia_estadao_1_2_cbow <- read_csv("acuracia_configuracao_embeddings/acuracia_configuracao_estadao_100_200_cbow.csv")
conf_acuracia_estadao_3_4_cbow <- read_csv("acuracia_configuracao_embeddings/acuracia_configuracao_estadao_300_400_cbow.csv")
conf_acuracia_folha_1_2_cbow <- read_csv("acuracia_configuracao_embeddings/acuracia_configuracao_folha_100_200_cbow.csv")
conf_acuracia_folha_3_4_cbow <- read_csv("acuracia_configuracao_embeddings/acuracia_configuracao_folha_300_400_cbow.csv")

confs_acuracia <- bind_rows(conf_acuracia_estadao_1_2, conf_acuracia_estadao_3_4, conf_acuracia_folha_1_2, conf_acuracia_folha_3_4,
                            conf_acuracia_estadao_1_2_cbow, conf_acuracia_estadao_3_4_cbow, conf_acuracia_folha_1_2_cbow, conf_acuracia_folha_3_4_cbow) %>% arrange(-acuracia)
