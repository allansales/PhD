library(dplyr)
library(ggplot2)
library(readr)
library(tm)

gastos_parlamentares <- read_csv('dados/gastos_parlamentares_2016.csv')

assinaturas_revistas_jornais <- gastos_parlamentares %>% 
  filter(txtDescricao == "ASSINATURA DE PUBLICAÇÕES")

# padronizando as editoras
fornecedor <- assinaturas_revistas_jornais %>% select(txtFornecedor) %>% VectorSource() %>% Corpus()
fornecedor <- tm_map(fornecedor, tolower)
fornecedor <- tm_map(fornecedor, removePunctuation, preserve_intra_word_dashes = FALSE) 

assinaturas_revistas_jornais <- assinaturas_revistas_jornais %>% mutate(editora = fornecedor[[1]])

# descricao dos dados
n_assinaturas_editora = assinaturas_revistas_jornais %>%
  count(txtFornecedor) %>%
  arrange(-n)

n_assinaturas_editora_partido = assinaturas_revistas_jornais %>%
  group_by(sgPartido) %>% 
  count(editora) 

percent_assinaturas_editora_partido = n_assinaturas_editora_partido %>%
  group_by(sgPartido) %>% 
  mutate(porcentagen = n/sum(n))
