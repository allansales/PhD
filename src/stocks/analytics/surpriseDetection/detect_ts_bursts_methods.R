#infrastructure for regular and irregular time series
if(!require(zoo)){
  install.packages('zoo')
}else{
  require(zoo)
}
# -----------------------------------------------------------------------------
# BURST DETECTION METHODS
# -----------------------------------------------------------------------------
# These method return a list with: a logic (TRUE or FALSE) vector (is.burst) of 
# length = length(serie) - 1 and the name of the method (method.name) 

#Recebe a serie temporal, realiza a diferen??a sequencial entre os elementos da serie.
#Aplica o valor absoluto dessas diferen??as e marca as diferen??as que s??o maiores que 95%
#das diferen??as presentes na s??rie.
GlobalBaseline <- function(serie, limit.quantile = .95){
  
  diff.serie <- diff(serie, lag=1)
  abs.serie <- abs(diff.serie)
  
  is.burst <- (abs.serie > quantile(abs.serie, limit.quantile, na.rm=T))
  
  # Return the list
  return (list(is.burst = is.burst, method.name = "Global-Baseline Detector"))
}


#Marca a cada sequencia de 15 elementos, as diferen??as que distam de 95% dos dados
#Ou seja, as varia????es dentro da quizena que s??o 5% an??malas.
LocalBaseline <- function(serie, window.size = 15, limit.quantile = .95){

  diff.serie <- diff(serie, lag=1)
  abs.serie <- abs(diff.serie)
  
  # Windowing... (rollapply applies the function at each window)
  #Saber se em alguma parte da compara????o, n??o apenas no ??ltimo valor, 
  #existe algo que dista de .95% da normalidade
  is.burst <- rollapply(abs.serie, window.size, FUN=function(x){
    x[length(x)] > quantile(x, limit.quantile, na.rm=T)
    #TRUE %in%(x > quantile(x, limit.quantile, na.rm=T))
    
  })
  
  # Fill in the initial values with FALSE (the window does not permit the evaluation
  # of the initial values)
  is.burst <- c(rep(F, length(abs.serie) - length(is.burst)), is.burst)
  
  # Return the list
  return (list(is.burst = is.burst, method.name = "Local-Baseline Detector"))
}

LongTermVisionDetector <- function(serie, horizon.size = 30, horizons.window.size = 10, 
                                   limit.quantile = .95){
  
  # This algorithm is based on the LMME algorithm
  # More information see: http://www.joics.com/publishedpapers/2013_10_9_2747_2756.pdf
  
  # Split the sequence in horizon.size chunks
  # Fatia a serie temporal em conjuntos de tamanho = honrizon.size
  serie.split <- split(serie, ceiling(seq_along(serie)/horizon.size))
  
  # Define a sequence formed by the min and max points of every split
  min.max.serie <- NULL
  #obtem minimos e maximos locais(por grupos)
  for(i in 1:length(serie.split)){#para o n??mero de conjuntos
    split = serie.split[[i]]#obtem as elementos do primeiro conjunto
    min.val <- min(split, na.rm=T)#obtem valor minimo
    max.val <- max(split, na.rm=T)#obtem valor m??ximo
    
    if (length(min.max.serie) > 0){
      min.max.serie <- c(min.max.serie, split[which(split == min.val | split == max.val)])
    }else{
      #min.max.serie ficar?? apenas com apenas o menor e o maior elemento. A lista split vai retornar apenas 
      #os valores contidos nos indices (which) onde ?? minino e m??ximo.
      min.max.serie <- split[which(split == min.val | split == max.val)]
    }
  }

  # Select the EXTREME points (alternating MAX and MIN values) from min.max.serie
  #Cria um vetor de FALSES do tamanho do vetor de m??nimos e m??ximos = n??mero de grupos (horizon.size*2)
  is.extrema <- rep(F, length(min.max.serie))
  for (i in 2:(length(min.max.serie)-1)){
    #se obtem o ponto do meio, o anterior e o posterior
    point <- min.max.serie[i][[1]]
    prev.point <- min.max.serie[i-1][[1]]
    next.point <- min.max.serie[i+1][[1]]
    
    #verifica se o ponto ?? realmente m??nimo ou m??ximo em rela????o aos seus adjacentes diretos
    #provavelmente para obter apenas os extremos, os vales, tentando evitar situa????es onde 
    #o m??nimo de um grupo ?? maior que o m??ximo de outro
    is.extrema[i] <- ((point > prev.point & point > next.point) |
                        (point < prev.point & point < next.point))
  }

  # The extrema.serie is formed by the extreme points from the min.max.serie AND
  # the initial and the last points of the original serie
  extrema.serie <- c(min.max.serie[is.extrema], serie[1], serie[length(serie)])
  
  # Define the bursts
  #Se as diferen??as de valores extremos que s??o 95% significativas
  result <- LocalBaseline(extrema.serie, horizons.window.size, limit.quantile)
  is.burst <- as.vector(result$is.burst)
  is.burst <- c(is.burst, is.burst[length(is.burst)]) # Repeat the last value
  
  # We shift the interior days of the extreme.serie (because the interior burst 
  # intervals should start at the day after an extreme point, and finish at the 
  # next extreme point day)
  shifted.days <- index(extrema.serie)
  #Retirando o primeiro e o ultimo dia, todos os valores de extrema.serie recebem +1
  shifted.days[2:(length(shifted.days)-1)] <- shifted.days[2:(length(shifted.days)-1)]+1
  
  # ReCreate the extreme.serie now with TRUE or FALSE values, then MERGE it 
  # with the complete serie adding the intermediate days
  is.burst.complete <- merge.zoo(zoo(is.burst, order.by=shifted.days), 
                                 zoo(,seq(start(serie), end(serie), by="day")), all=TRUE)
  
  # Replace the NA values of the intermeadiate extreme point values with the last 
  # extreme point value (TRUE or FALSE), creating the long sight term
  #troca o NA pelo n??mero imediatamente anterior a ele na sequencia.
  is.burst.complete <- na.locf(is.burst.complete)
  
  # Remove the values unexistent in the original serie
  is.burst.complete[is.na(serie)] <- rep(NA, sum(is.na(serie)))
  
  # Remove the first value to agree with the function signature
  is.burst.complete <- is.burst.complete[-1]
  
  # Return the list
  return (list(is.burst = is.burst.complete, method.name = "Long-Term Vision Detector"))
}

# Function used to understand the LMME algorithm
LMMEStudy <- function(extrema.serie){
  plot(extrema.serie[1:length(extrema.serie)], xlab = "Ano", ylab = "Preco Medio")
  for(val in index(extrema.serie[1:length(extrema.serie)])){
    abline(v=val)
  }
}
