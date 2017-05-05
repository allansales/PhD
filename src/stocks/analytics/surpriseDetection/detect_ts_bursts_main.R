rm(list = ls())

# =============================================================================
# SOURCE() and LIBRARY()
# =============================================================================
library(zoo)
library(Hmisc)
source("src/ts_analytics/ts_solavanco_detection/detect_ts_bursts_methods.R")

# =============================================================================
# FUNCTIONs
# =============================================================================
DescribeTs <- function(serie, serie.name, descriptive.ts.dir){
  # DESCRIPTIVE ANALYSIS
  
  # PDF file
  pdf(paste(descriptive.ts.dir, "/", serie.name, ".pdf", sep = ""), width = 30, height = 15)
  
  plot(serie, xlab = "Ano", ylab = "Preco Medio", lwd = 2)
  
  serie.diff <- diff(serie, lag=1)
  plot(serie.diff, xlab = "Ano", ylab = "Diff (lag 1) - Preco Medio")
  
  hist(serie.diff, breaks = 100, main = "Histograma", 
       xlab = "Diff (lag 1) - Preco Medio", ylab = "Frequencia")
  
  plot(Ecdf(~ serie.diff, q=c(.6, .7, .8, .9, .95, .99), 
            main = "Funcao de Distribuição Acumulada", 
            xlab = "Diff (lag 1) - Preco Medio", ylab = "Quantile"))
  
  plot(Ecdf(~ abs(serie.diff), q=c(.6, .7, .8, .9, .95, .99), 
            main = "Funcao de Distribuição Acumulada", 
            xlab = "Abs Diff (lag 1) - Preco Medio", ylab = "Quantile"))
  
  dev.off()
  
}

ApplyBurstSelectionMethods <- function(serie, serie.name, methods, burst.ts.dir){
  
  # PDF file
  pdf(paste(burst.ts.dir, "/", serie.name, ".pdf", sep = ""), width = 30, height = 15)
  
  # Predicted Burst series
  for(i in 1:length(methods)){
    result <- methods[[i]](serie)
    
    # Plot the TS
    plot(serie, main = result$method.name, xlab = "Ano", ylab = "Preco Medio")
    
    # Highlight the bursts
    colour <- ifelse(result$is.burst, "red", "black")
    
    segments(x0=index(serie)[-c(length(serie))], y0=serie[-c(length(serie))], 
             x1=index(serie)[-1], y1=serie[-1], 
             col = colour, lwd = 2)
    
    legend("topright", legend=c("IS solavanco", "ISN'T solavanco"), 
           col=c("red", "black"), lty = 1, lwd = 2)
  }
  
  dev.off()
}

# =============================================================================
# MAIN
# =============================================================================

# READ TS FROM BIG_COTACOES
cat("Reading data...\n")
ts.dir <- "data/time_series"
ts.data <- read.csv(paste(ts.dir, "ts_big_cotacoes.csv", sep = "/"))
ts.data$dataPregao <- as.Date(ts.data$dataPregao, format="%Y-%m-%d")

# Create the output directories
cat("Creating output directories...\n")
descriptive.ts.dir <- paste(ts.dir, "describe_ts", sep = "/")
dir.create(descriptive.ts.dir, showWarnings=F)

burst.ts.dir <- paste(ts.dir, "solavancos_ts", sep = "/")
dir.create(burst.ts.dir, showWarnings=F)

# Define the Burst Selection methods
methods <- c(GlobalBaseline, LocalBaseline, LongTermVisionDetector)

cat("Iterating over series...\n")
for (id in sort(unique(ts.data$id))){
  cat("  Id:", id, "\n")
  
  # ----------------------------------------------------------------------------
  # Prepare the time serie
  # -----------------------------------------------------------------------------
  serie.data <- ts.data[ts.data$id == id | is.na(ts.data$id), c("dataPregao", "premed")]
  serie.name <- ts.data[ts.data$id == id | is.na(ts.data$id), c("nomres","codisi", "codbdi")][1,]
  serie.name <- paste(id, 
                      sub(" +$", "", serie.name$nomres), # Replace trailer spaces
                      sub(" +$", "", serie.name$codisi), # Replace trailer spaces
                      serie.name$codbdi, sep = "_")
  
  # Generate the serie
  serie <- zoo(serie.data$premed, order.by=serie.data$dataPregao)
  
  # Adding the Dates without PREGAO (to create the GAPS in the TS)
  serie <- merge.zoo(serie, zoo(, seq.Date(start(serie), end(serie), by="day")), all=TRUE)

  # ----------------------------------------------------------------------------
  # Describe it
  # -----------------------------------------------------------------------------
  cat("    Describing it...\n")
  DescribeTs(serie, serie.name, descriptive.ts.dir)

  # ----------------------------------------------------------------------------
  # Apply the burst selection method
  # -----------------------------------------------------------------------------
  cat("    Applying the Burst Detection Methods...\n")
  ApplyBurstSelectionMethods(serie, serie.name, methods, burst.ts.dir)
}

