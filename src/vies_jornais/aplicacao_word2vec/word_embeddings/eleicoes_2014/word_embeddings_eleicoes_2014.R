source("../../../utils/mongo_utils.R")
source("../../../utils/utils.R")
source("../../../utils/embeddings_utils.R")
source("../cria_word_embedding.R")
library("wordVectors")
library("readr")

### Importa base de dados
noticias <- get_todas_noticias_processadas()
noticias_estadao <- noticias %>% filter(subFonte == "ESTADAO")
noticias_folha <- noticias %>% filter(subFonte == "FOLHASP")

n_layers = 300
data_inicio = "2014-01-01"
data_fim = "2014-12-31"
path_saida = "embeddings/"

analogias <- c("pt psdb dilma", "pt pv dilma","pt prtb dilma","pt psol dilma","pt psb dilma","pt psdc dilma","psdb pv aécio","psdb prtb aécio","psdb psol aécio","psdb psb aécio","psdb psdc aécio","pv prtb jorge","pv psol jorge","pv psb jorge","pv psdc jorge","prtb psol fidelix","prtb psb fidelix","prtb psdc fidelix","psol psb luciana","psol psdc luciana","psb psdc marina","dilma aécio rousseff","campos psb aécio","pt psdb petista")
respostas <- c("aécio","jorge","fidelix","luciana","marina","eymael","jorge","fidelix","luciana","marina","eymael","fidelix","luciana","marina","eymael","luciana","marina","eymael","marina","eymael","eymael","neves","psdb","tucano")

partidos <- c("pmdb","ptb","pdt","pt","dem ","psb","psdb","ptc","psc","pmn","prp","pps","pv","pp","pstu","pcb","prtb","phs","psdc","pco","ptn","psl","prb","psol","ppl","pros","psd |psd[.]"," sd |sd[.]") #,"pr","pen", "pcdob", "ptdob"
candidatos <- c("dilma","aécio","levy","marina silva","luciana genro","eduardo jorge","josé maria","pastor everaldo", "iasi","eymael","rui costa","eduardo campos")

entity <- c(candidatos, partidos)
pattern <- paste(entity, collapse = "|")

tema = "estadao_noticias_eleicao_2014"
noticias_estadao_tema <- noticias_estadao %>% filter(timestamp >= data_inicio & timestamp <= data_fim) %>% noticias_tema(pattern, "titulo")
we_noticias_estadao <- cria_word_embedding(tema, n_layers, analogias, noticias_estadao_tema, "conteudo_processado", path_saida)

#tema = "estadao_comentarios_eleicao_2014"
#comentarios_estadao_tema <- get_comentarios_por_data("estadaoNoticiasProcessadas", data_inicio, data_fim)
#we_comentarios_estadao <- cria_word_embedding(tema, n_layers, analogias, comentarios_estadao_tema, "conteudo_processado", path_saida)

tema = "folha_noticias_eleicao_2014"
noticias_folha <- noticias %>% filter(subFonte == "FOLHASP")
noticias_folha_tema <- noticias_folha %>% filter(timestamp >= data_inicio & timestamp <= data_fim) %>% noticias_tema(pattern, "titulo")
we_noticias_folha <- cria_word_embedding(tema, n_layers, analogias, noticias_folha_tema, "conteudo_processado", path_saida)

tema = "folha_comentarios_eleicao_2014"
comentarios_folha_tema <- get_docs_by_idNoticia(colecao = "folhaComentariosProcessados", vetor_ids = noticias_folha_tema$idNoticia)
we_comentarios_folha <- cria_word_embedding(tema, n_layers, analogias, comentarios_folha_tema, "comentario_processado", path_saida)