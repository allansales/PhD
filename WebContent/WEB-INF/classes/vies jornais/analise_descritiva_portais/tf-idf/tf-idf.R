source("../mongo_utils.R")
source("../utils/utils.R")
library(tidytext)
library(ggplot2)
library(readr)

noticias <- get_todas_noticias() %>% 
  mutate(emissor=padroniza_emissor(emissor)) %>% 
  filter(subFonte %in% c("FOLHASP","ESTADAO"), ano >= 2014)

noticias_estadao <- noticias %>% filter(subFonte=="ESTADAO")
noticias_folhasp <- noticias %>% filter(subFonte=="FOLHASP")

tf_idf_table <- function(noticias, col){
  noticias_words <- noticias %>%
    unnest_tokens(word, conteudo) %>%
    count(documents=get(col), word, sort = TRUE) %>%
    ungroup()
  
  total_words <- noticias_words %>% 
    group_by(documents) %>% 
    summarize(total = sum(n))
  
  noticias_words <- left_join(noticias_words, total_words)
  
  noticias_words <- noticias_words %>%
    bind_tf_idf(word, documents, n)

  return(noticias_words)  
}

words_url_estadao <- tf_idf_table(noticias_estadao, "url")
write_csv(words_url_estadao, "words_url_estadao.csv")
rm(words_url_estadao)

words_url_folhasp <- tf_idf_table(noticias_folhasp, "url")
write_csv(words_url_folhasp, "words_url_folhasp.csv")
rm(words_url_folhasp)

words_emissor_estadao <- tf_idf_table(noticias_estadao, "emissor")
write_csv(words_emissor_estadao, "words_emissor_estadao.csv")
rm(words_emissor_estadao)

words_emissor_folhasp <- tf_idf_table(noticias_folhasp, "emissor")
write_csv(words_emissor_folhasp, "words_emissor_folhasp.csv")
rm(words_emissor_folhasp)

words_subFonte <- tf_idf_table(noticias, "subFonte")
write_csv(words_subFonte, "words_subFonte.csv")

noticias_words = words_subFonte
rm(words_subFonte)

freq_by_rank <- noticias_words %>%
  group_by(documents) %>%
  mutate(rank = row_number(),
         `term frequency` = n/total)

ggplot(noticias_words, aes(n/total, fill = documents)) +
  geom_histogram(show.legend = FALSE) +
  xlim(NA, 0.0009) +
  facet_wrap(~documents, ncol = 2, scales = "free_y")

freq_by_rank %>%
  ggplot(aes(rank, `term frequency`, color = documents)) +
  geom_line(size = 1.2, alpha = 0.8) +
  scale_x_log10() +
  scale_y_log10()

freq_by_rank %>%
  ggplot(aes(rank, `term frequency`, color = documents)) +
  geom_line(size = 1.2, alpha = 0.8)

# rank_subset <- freq_by_rank %>%
#   filter(rank < 500,
#          rank > 10)
# 
# lm(log10(`term frequency`) ~ log10(rank), data = rank_subset)
# 
# freq_by_rank %>%
#   ggplot(aes(rank, `term frequency`, color = documents)) +
#   geom_abline(intercept = -1.1516, slope = -0.9209, color = "gray50", linetype = 2) +
#   geom_line(size = 1.2, alpha = 0.8) +
#   scale_x_log10() +
#   scale_y_log10()

palavras_importantes <- noticias_words %>%
  arrange(desc(tf_idf)) %>%
  mutate(word = factor(word, levels = rev(unique(word))))

palavras_importantes %>%
  top_n(20) %>%
  ggplot(aes(word, tf_idf, fill = documents)) +
  geom_col() +
  labs(x = NULL, y = "tf-idf") +
  coord_flip()

palavras_importantes %>%
  group_by(documents) %>%
  top_n(15) %>%
  ungroup %>%
  ggplot(aes(word, tf_idf, fill = documents)) +
  geom_col(show.legend = FALSE) +
  labs(x = NULL, y = "tf-idf") +
  facet_wrap(~documents, ncol = 2, scales = "free") +
  coord_flip()

rm(noticias_words)
