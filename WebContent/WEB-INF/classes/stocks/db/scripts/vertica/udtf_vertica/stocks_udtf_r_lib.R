###############################################################################
# LIBRARY() and SOURCE()
###############################################################################
library(zoo)

###############################################################################
# FUNCTIONS
###############################################################################
# -----------------------------------------------------------------------------
# Transform Functions
# -----------------------------------------------------------------------------
DetectSolavanco <- function(table){

  data.pregao <- table[,1]
  serie.preco.ultimo <- table[,2]
  window.size <- table[1,3]
  limit.quantile <- table[1,4]
  
  abs.serie <- abs(diff(serie.preco.ultimo, lag=1))
  
  # Windowing... (rollapply applies the function at each window)
  is.solavanco <- rollapply(abs.serie, window.size, FUN=function(x){
    x[length(x)] > quantile(x, limit.quantile, na.rm=T)
  })
  
  # Fill in the initial values with FALSE (the window does not permit the evaluation
  # of the initial values)
  is.solavanco <- c(rep(F, length(serie.preco.ultimo) - length(is.solavanco)), is.solavanco)

  # Return the list
  return (data.frame(data.pregao, serie.preco.ultimo, is.solavanco))
}

# -----------------------------------------------------------------------------
# Transform Factory Functions
# -----------------------------------------------------------------------------
DetectSolavancoFactory <- function(){
  list(name     = DetectSolavanco, 
       udxtype  = c("transform"), 
       intype   = c("timestamp", "numeric", "int", "numeric"), 
       outtype  = c("timestamp", "numeric", "boolean"),
       outnames = c("data_pregao", "preco_ultimo", "is_solavanco"))
}

###############################################################################
# SQL Query Examples
###############################################################################

# Detect solavanco in a time-serie (by ISIN)
# select DetectSolavanco(table_acao.data_pregao, table_acao.preco_ultimo, 15, 0.95) OVER()
# from (select acao.preco_ultimo AS preco_ultimo, acao.data_pregao AS data_pregao
#       from cotacao as acao
#       where acao.cod_isin = 'BRMTIGACNOR0' and acao.cod_bdi = '02') as table_acao;
