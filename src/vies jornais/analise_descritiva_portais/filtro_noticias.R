source("mongo_utils.R")
library("stringr")

noticias <- get_todas_noticias()
noticias_sample <- sample_n(noticias, 100)

ptrn_list <- c("Lava Jato","Aécio","Dilma","Temer","Lula","Eduardo Cunha","Fachin","Moro")
noticias_sample %>% select(conteudo) %>% rowwise()

row_contain_word <- function(x, ptrn){
  contain <- str_detect(x, ptrn)
  if (TRUE %in% contain){
    return(TRUE)
  }
  return(FALSE)
}

for(ptrn in ptrn_list){
  n <- lapply(noticias$conteudo, row_contain_word, ptrn)
  print(table(unlist(n)))
}
