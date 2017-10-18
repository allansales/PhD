library(stringr)
library(purrr)

padroniza_emissor <- function(col){
  tolower(col) %>% 
    str_split("[*,/-]") %>%
    map(1) %>% 
    unlist()
}