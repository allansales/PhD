source("mongo_utils.R")
library("stringr")

noticias <- get_todas_noticias()

translate_sample <- noticias %>% sample_n(100)
mean(stringr::str_length(translate_sample$conteudo))


key <- "AIzaSyDfYj0hFKte0cmNXqPDbvdsQwzEnPh8RLY"




library(translateR)
google.dataset.out <- translate(content.vec = translate_sample$conteudo,
                                google.api.key = key,
                                source.lang = 'pt',
                                target.lang = 'en')

str(translate_sample)

library(translate)
a<-translate::translate(translate_sample$conteudo[100], "pt", "en", key)

write.table(a,"a.txt")

