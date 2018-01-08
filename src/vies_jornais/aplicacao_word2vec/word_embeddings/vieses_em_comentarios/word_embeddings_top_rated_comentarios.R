source("../../word_embeddings/cria_word_embedding.R")
source("../../utils/mongo_utils.R")
source("../../utils/utils.R")
source("busca_top_rated_comentarios.R")

# filtro de noticias
data_inicio = "2014-01-01"
data_fim = "2014-12-31"
path_saida = "embeddings/"

partidos <- c("pmdb","ptb","pdt","pt","dem ","psb","psdb","ptc","psc","pmn","prp","pps","pv","pp","pstu","pcb","prtb","phs","psdc","pco","ptn","psl","prb","psol","ppl","pros","psd |psd[.]"," sd |sd[.]") #,"pr","pen", "pcdob", "ptdob"
candidatos <- c("dilma","aécio","levy","marina silva","luciana genro","eduardo jorge","josé maria","pastor everaldo", "iasi","eymael","rui costa","eduardo campos")

entity <- c(candidatos, partidos)
pattern <- paste(entity, collapse = "|")

# busca noticias de acordo com o filtro
noticias <- get_todas_noticias_processadas()
id_noticias_folha <- noticias %>% filter(subFonte == "FOLHASP") %>% filter(timestamp >= data_inicio & timestamp <= data_fim) %>% noticias_tema(pattern, "titulo") %>% select(idNoticia)

comentarios_folha = get_collection("folhaComentariosProcessados") %>% filter(idNoticia %in% id_noticias_folha$idNoticia)
comentarios_folha$ThumbsUp = comentarios_folha$ThumbsUp %>% as.numeric()
comentarios_folha$ThumbsDown = comentarios_folha$ThumbsDown %>% as.numeric()

comentarios_folha_top = get_comments_by_one_boundary(comentarios_folha, "ThumbsUp")
comentarios_folha_bottom = get_comments_by_one_boundary(comentarios_folha, "ThumbsDown")

comentarios_folha_top_and_bottom = get_comments_by_both_boundaries(comentarios_folha)

tema = "folha_thumbs_up"
modelo_folha_top = cria_word_embedding(tema = tema, n_layers = 300, noticias = comentarios_folha_top, analogias = NULL, texto_col = "comentario_processado", path_saida = path_saida)

tema = "folha_thumbs_down"
modelo_folha_down = cria_word_embedding(tema = tema, n_layers = 300, noticias = comentarios_folha_bottom, analogias = NULL, texto_col = "comentario_processado", path_saida = path_saida)

tema = "folha_thumbs_up_down"
modelo_folha_down = cria_word_embedding(tema = tema, n_layers = 300, noticias = comentarios_folha_top_and_bottom, analogias = NULL, texto_col = "comentario_processado", path_saida = path_saida)
