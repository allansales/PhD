source("../../word_embeddings/cria_word_embedding.R")
source("../../utils/mongo_utils.R")
source("../../utils/utils.R")

# filtro de noticias
data_inicio = "2014-01-01"
data_fim = "2014-12-31"
path_saida = "embeddings/"

partidos <- c("pmdb","ptb","pdt","pt","dem ","psb","psdb","ptc","psc","pmn","prp","pps","pv","pp","pstu","pcb","prtb","phs","psdc","pco","ptn","psl","prb","psol","ppl","pros","psd |psd[.]"," sd |sd[.]") #,"pr","pen", "pcdob", "ptdob"
candidatos <- c("dilma","aécio","levy","marina silva","luciana genro","eduardo jorge","josé maria","pastor everaldo", "iasi","eymael","rui costa","eduardo campos")

entity <- c(candidatos, partidos)
pattern <- paste(entity, collapse = "|")

# busca noticias de acordo com o filtro
noticias <- get_todas_noticias_processadas() %>% filter(timestamp >= data_inicio & timestamp <= data_fim) %>% noticias_tema(pattern, "titulo") %>% select(conteudo = conteudo_processado, idNoticia)

comentarios <- get_todos_comentarios() %>% filter(idNoticia %in% noticias$idNoticia) %>% select(conteudo = comentario_processado, idNoticia)

conteudo = noticias %>% bind_rows(comentarios)

tema = "noticias_comentarios"
modelo_noticia_comentario = cria_word_embedding(tema = tema, n_layers = 300, noticias = conteudo, analogias = NULL, texto_col = "conteudo", path_saida = path_saida)
