source("../mongo_utils.R")
library(RYandexTranslate)

api_key <- "trnsl.1.1.20170713T192723Z.8c72c913866296bc.b4abce71b9496ca861a3f491af5f9fc074cf596c"

estadao_comentarios <- get_collection("estadaoComentarios")

translate = function (text = "", lang = "pt-en", api_key = "trnsl.1.1.20170713T192723Z.8c72c913866296bc.b4abce71b9496ca861a3f491af5f9fc074cf596c") 
{
  url = "https://translate.yandex.net/api/v1.5/tr.json/translate?"
  url = paste(url, "key=", api_key, sep = "")
  if (text != "") {
    url = paste(url, "&text=", text, sep = "")
  }
  if (lang != "") {
    url = paste(url, "&lang=", lang, sep = "")
  }
  url = gsub(pattern = " ", replacement = "%20", x = url)
  d = RCurl::getURL(url, ssl.verifyhost = 0L, ssl.verifypeer = 0L)
  d = jsonlite::fromJSON(d)
  d$code = NULL
  d
}


subset <- estadao_comentarios %>% slice(1:10)
sapply(subset$comentario, translate)

subset %>% mutate(traducao = translate(comentario))
