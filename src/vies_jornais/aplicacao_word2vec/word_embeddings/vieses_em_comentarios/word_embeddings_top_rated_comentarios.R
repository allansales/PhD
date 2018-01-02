source("../../word_embeddings/cria_word_embedding.R")
source("../../utils/mongo_utils.R")
source("busca_top_rated_comentarios.R")

comentarios_estadao = get_collection("estadaoComentariosProcessados")
comentarios_estadao$ThumbsUp = comentarios_estadao$ThumbsUp %>% as.numeric()
comentarios_estadao$ThumbsDown = comentarios_estadao$ThumbsDown %>% as.numeric()

comentarios_folha = get_collection("folhaComentariosProcessados")
comentarios_folha$ThumbsUp = comentarios_folha$ThumbsUp %>% as.numeric()
comentarios_folha$ThumbsDown = comentarios_folha$ThumbsDown %>% as.numeric()

comentarios_estadao_top = get_comments_by_one_boundary(comentarios_estadao, "ThumbsUp")

comentarios_folha_top = get_comments_by_one_boundary(comentarios_folha, "ThumbsUp")
comentarios_folha_bottom = get_comments_by_one_boundary(comentarios_folha, "ThumbsDown")

comentarios_folha_top_and_bottom = get_comments_by_both_boundaries(comentarios_folha)

path_saida = "embeddings/"
tema = "estadao_thumbs_up"
modelo_estadao_top = cria_word_embedding(tema = tema, n_layers = 300, noticias = comentarios_estadao_top, analogias = NULL, texto_col = "comentario_processado", path_saida = path_saida)

tema = "folha_thumbs_up"
modelo_folha_top = cria_word_embedding(tema = tema, n_layers = 300, noticias = comentarios_folha_top, analogias = NULL, texto_col = "comentario_processado", path_saida = path_saida)

tema = "folha_thumbs_down"
modelo_folha_down = cria_word_embedding(tema = tema, n_layers = 300, noticias = comentarios_folha_bottom, analogias = NULL, texto_col = "comentario_processado", path_saida = path_saida)

tema = "folha_thumbs_up_down"
modelo_folha_down = cria_word_embedding(tema = tema, n_layers = 300, noticias = comentarios_folha_top_and_bottom, analogias = NULL, texto_col = "comentario_processado", path_saida = path_saida)
