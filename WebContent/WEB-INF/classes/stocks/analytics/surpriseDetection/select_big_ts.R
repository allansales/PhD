#remove and clean objects from the memory
rm(list = ls())

# =============================================================================
# SOURCE() and LIBRARY()
# =============================================================================

if(!require(lubridate)){
  install.packages('lubridate')
}else{
  require(lubridate)
}

if(!require(plyr)){
  install.packages('plyr')
}else{
  require(plyr)
}

# =============================================================================
# FUNCTIONS
# =============================================================================
SelectBigCotacoes <- function(num.cotacoes, pk.cols){
	# Put data in memory
	cat("Reading data...\n")
	ts.data <- NULL

  cotacoes.dir <- "/Users/zegildo/Desktop/POS-GRADUACAO/DOUTORADO/Stocks/src/data_collection/collect_cotacoes/BM&FBOVESPA/Historico_Cotacoes_CSV"
  #generate a list with a files's path 
  cotacoes.csvs <- list.files(cotacoes.dir)

  #Put all over the files into a single structure
  for (csv.file in cotacoes.csvs){
		cat ("  ", csv.file, "\n")
		data <- read.csv(paste(cotacoes.dir, csv.file, sep = "/"), 
		                 # Define the column names
		                 col.names = c("dataPregao", "codbdi", "codneg", "tpmerc", 
		                               "nomres", "especi", "prazot", "modref", "preabe", "premax", 
		                               "premin", "premed", "preult", "preofc", "preofv", "totneg", 
		                               "quatot", "voltot", "preexe", "indopc", "datven", "fatcot",
		                               "ptoexe", "codisi", "dismes"), 
		                 # Define the column classes (improve time performance)
		                 colClasses = c(rep("character", 3), "numeric", rep("character", 4),
                                    rep("numeric", 7), rep("numeric", 2), rep("numeric", 2),
                                    "numeric", "character", "numeric", "numeric", 
                                    rep("character", 2)), 
		                 # Define the file encoding
                     fileEncoding="latin1",
                     # The strings are characters not factors (improve time performance)
		                 stringsAsFactors = F)

		# Select the the ts data (average price in stock)
		ts.data <- rbind(ts.data, data)
	}
  rm(data)
  
	# Cast dataPregao to date object
	cat("Cast dataPregao to Date...\n")
	ts.data$dataPregao <- as.Date(ts.data$dataPregao, "%Y%m%d")

	# Select the cotacoes
	cat("Select the ISINs in 2013...\n")
	isin.2013 <- unique(ts.data[year(ts.data$dataPregao) == 2013, "codisi"])
  
  cat("Count the quantity of cotacoes per selected ISIN with BDI: 02 or 96...\n")
  #aplica a função para cada parte do conjunto sem janela deslizante como no rollaplly
	cotacao.size <- ddply(subset(ts.data, (codbdi == "02" | codbdi == "96") & codisi %in% isin.2013, 
                               c(pk.cols, "nomres")), 
                        #group the extracted subset by codbdi and isin
	                      pk.cols, function(df){
	                        size <- nrow(df)
	                        return(data.frame(nomres = df$nomres[1], 
                                            codisi = df$codisi[1], 
	                                          codbdi = df$codbdi[1], 
                                            size = size))
	                      }, .progress = "text")

  cat("Return the largest cotacoes from those...\n")
  
  # Order the ISINs by cotacoes sizes
	cotacao.size <- cotacao.size[order(cotacao.size$size, decreasing = T),]
  
  # Select the cotacoes
	# 	selected.cotacoes <- cotacao.size[1:num.cotacoes,] # Select by ISIN and BDI
	#   selected.cotacoes <- cotacao.size[head(which(!duplicated(cotacao.size$codisi)), num.cotacoes),] # Select by ISIN
  
  #which return the indice of a logical object
  #which(!duplicated(cotacao.size$nomres): get the unique set of elements.
  #head(which(!duplicated(cotacao.size$nomres)), num.cotacoes): get the N most popular stocks
	selected.cotacoes <- cotacao.size[head(which(!duplicated(cotacao.size$nomres)), num.cotacoes),] # Select by NOMRES
	selected.cotacoes$id <- 1:num.cotacoes
  
  # Merge with the complete ts.data
	final.data <- merge(ts.data, selected.cotacoes, all.x = F, all.y = T,
	                    by = c(pk.cols, "nomres"))
  
  # Organize the data columns and order by date
	final.data <- final.data[order(final.data$dataPregao),]
  
  initial.cols <- c("dataPregao", "nomres", pk.cols)
	final.data <- final.data[,c(initial.cols, 
	                            colnames(final.data)[!colnames(final.data) %in% initial.cols])]
  
	# Return the desired number of empresas
	return(final.data)
}

# =============================================================================
# MAIN
# =============================================================================
num.cotacoes <- 10
pk.cols <- c("codisi", "codbdi")

ts.data.big.cotacoes <- SelectBigCotacoes(num.cotacoes, pk.cols)

cat("Selected cotacoes:\n")
print(ts.data.big.cotacoes[!duplicated(ts.data.big.cotacoes[,pk.cols]), pk.cols])

cat("\nPersist the time-series...\n")

ts.dir <- "data/time_series"
dir.create(ts.dir, showWarnings=F)

write.csv(ts.data.big.cotacoes, paste(ts.dir, "/ts_big_cotacoes.csv", sep = ""),
          row.names = F)
