library(dplyr)
library(ggplot2)
library(readr)

gastos_parlamentares <- read_csv('dados/gastos_parlamentares_2016.csv')
assinaturas_revistas_jornais <- gastos_parlamentares %>% filter(txtDescricao == "ASSINATURA DE PUBLICAÇÕES")

n_assinaturas_editora = assinaturas_revistas_jornais %>% count(txtFornecedor) %>% arrange(-n)
