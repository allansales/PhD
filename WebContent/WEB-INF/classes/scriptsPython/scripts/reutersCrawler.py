#coding: utf-8
"""
Este script tem como finalidade coletar informacoes do site infomoney e 
guarda-las em uma base de dados mongoDB.

As noticias sao coletadas verificando se para um determinada pasta + id
eh retornada uma pagina com titulo, caso contrario outro padrao eh verificado

/mercados/acoes-e-indices/noticia/1869?
/mercados/noticia/1869 ?
/mercados/politica/noticia/1869 ?

Este script sera executado junto com o comando *parallel -X* do linux que 
ira testar essas possibilidades.

"""
import requests  
import time
import datetime
import json
import sys
from string import maketrans
from lxml import html  
from pymongo import MongoClient

MESES = {
	"janeiro":"01",
	"fevereiro":"02",
	"março":"03",
	"abril":"04",
	"maio":"05",
	"junho":"06",
	"julho":"07",
	"agosto":"08",
	"setembro":"09",
	"outubro":"10",
	"novembro":"11",
	"dezembro":"12"
}

def getContent(content):
	"""
	Retira todas as tabulacoes, espacos, retornos de uma string.
	Por fim concatena todos os elementos da lista em uma unica 
	string separando as partes por espacos.

	Parameters
	----------
	content: list
		uma lista de elementos fruto da execucao do seguinte comando:
		.xpath('//article/div[@id="contentNews"]/text() | //article/div[@id="contentNews"]/strong/a/text()')
		para maiores informacoes sobre o xpath: http://www.macoratti.net/vb_xpath.htm

	Returns
	-------
	String
		Uma string contendo todo o valor da lista sem tabulacoes, espacos e caracteres especiais.

	Examples
	--------
	>>> texto=[' \t\n a     ','\r\t\t\t\n\n','    nova   ']
	>>> getContent(texto)
	'a nova'

	"""
	conteudo = []
	for linha in content:
		linha = getStringSemEspacamento(linha)
		if linha:	
			conteudo.append(linha)
	return ' '.join(conteudo)



def getTimestampUnix(timestamp):
	
	"""
	Recebe o valor de horas minuto e data, retiramos o pipe '|' e passamos
	o padrao para compor o timestamp em formato unix momento UTC

	Parameters
	----------
	timestamp: string
		uma string no formato 'quarta-feira, 28 de abril de 2010 18:06 BRT'

	Returns
	-------
	int
		um inteiro representando o timestamp em formato Unix UTC.

	Examples
	--------
	>>> data = 'quarta-feira, 28 de abril de 2010 18:06 BRT'
	>>> getTimestampUnix(data)
	3265874189

	"""
	time = timestamp.split(' ')
	timestamp = time[1]+"-"+MESES[time[3]]+"-"+time[5]+"-"+time[6]
	unixTimesTamp = int(datetime.datetime.strptime(timestamp, '%d-%m-%Y-%H:%M').strftime("%s"))
	return unixTimesTamp

def verificaRepercussao(url):
	"""
	Verifica a repercussao que uma determinada url *existente* obteve nas redes sociais.

	Parameters
	----------
	url: string
		uma url valida

	Returns
	-------
	string
		uma string representando o valor que a url obteve nas principais redes sociais
		{tweet, facebook, googlePlus, linkedin e o total(soma de todos os elementos)}

	Examples
	--------
	>>> url = http://meu.teste.com.br/xpto
	>>> verificaRepercussao(url)
	't:0,f:0,g:0,l:0,total:0'

	"""
	FACEBOOK = "https://graph.facebook.com/fql?q=SELECT%20total_count%20FROM%20link_stat%20WHERE%20url=%27"+url+"%27&callback=jQuery1110045172129960119656_1426015916812&_=1426015916813"
	TWEET = "https://cdn.api.twitter.com/1/urls/count.json?url="+url+"&callback=jQuery11100053468162895262794_1425342668803&_=1425342668804"
	G_PLUS = "https://plusone.google.com/_/+1/fastbutton?url="+url
	LINKED_IN = "https://www.linkedin.com/countserv/count/share?format=jsonp&url="+url+"&callback=jQuery1110030292543070338573_1425849946154&_=1425849946155"
	
	try:

		facebook = getCountJson(FACEBOOK, "total_count","[","]")
		tweeter = getCountJson(TWEET, 'count')
		googlePlus = getCountHtml(G_PLUS)
		linkedIn = getCountJson(LINKED_IN, 'count')
		total = facebook+tweeter+googlePlus+linkedIn
		return "t:"+`tweeter`+",f:"+`facebook`+",g:"+`googlePlus`+",l:"+`linkedIn`+",total:"+`total`
	
	except:
		return "ERROR"

def getCountJson(url, atributo, start="(", end=")"):
	"""
	Extrai o json de respostas feitas as solicitacoes das paginas web.

	Em muitos casos as paginas Web retornam estruturas .json dentro de estruturas simples.
	O que esse metodo faz eh retirar o lixo que envolve a estrutura .json para em seguida 
	retornar o valor solicitado de dentro da estrutura.

	Parameters
	----------
	url: string
		uma url valida.

	atributo: string
		o atributo (chave) para qual desejo conhecer o valor.

	start: string
		o delimitador inicial onde apos ele iniciara o conteudo .json valido.
	
	end: string
		o delimitador final. O conteudo .json finaliza totalmente imediatamente antes dele.

	Returns
	-------
	int
		o valor do atributo solicitado (geralmente 'count', contagem da repercussao da url fornecida pela api)

	Examples
	--------
	>>> url = http://meu.teste.com.br/xpto
	>>> jQuery1110030292543070338573_1425849946154("data:[{a:b...,count:30}]");
	>>> getCountJson(url,'count',"[","]")
	30

	"""
	response = requests.get(url)  
	conteudo = response.content
	data = json.loads(conteudo.partition(start)[-1].rpartition(end)[0])
	return int(data[atributo])

def getCountHtml(url):
	"""
	Extrai o valor do id aggregateCount de uma pagina web.

	De forma geral, a api do google plus retorna essa pagina informando o 
	valor de repercussao de uma determinada url na plataforma.

	Parameters
	----------
	url: string
		uma url valida.

	Returns
	-------
	int
		o valor do atributo aggregateCount que representa a contagem da repercussao da url fornecida.

	Examples
	--------
	>>> url = http://meu.teste.com.br/xpto
	>>> <div id="aggregateCount">33<div>
	>>> getCountHtml(url)
	33

	"""
	response = requests.get(url)  
	body = html.fromstring(response.text)
	count = body.xpath('//div[@id="aggregateCount"]/text()')[0].encode('utf-8').strip()
	return int(count)

def connectMongoDb():
	"""
	Cria uma conexao com o banco MongoDB, na base de dados stocks, na collection jornaisEspecificos.

	"""
	client = MongoClient()
	db = client.stocks
	collection = db.informacoesGerais
	return collection	

def getStringSemEspacamento(linha):
	"""
	Recebe uma string e retira as tabulacoes quebras de linha e espacos
	inicial e final em formato utf-8.

	"""
	return linha.encode('utf-8').translate(maketrans("\n\t\r","   ")).strip() 


"""
Script feito para funcionar em juncao com o comando *parallel -X*

python reutersCrawler.py hashNoticia
						 ------------
						   argv[1]


"""

if __name__ == "__main__":
	
	collection = connectMongoDb()
	hashIds = sys.argv[1:]
	
	for hashId in hashIds:

		URL = 'http://br.reuters.com/article/businessNews/%s?pageNumber=0&virtualBrandChannel=0&sp=true'%(hashId)
		URL_parcial = 'http://br.reuters.com/article/businessNews/%s'%(hashId)
		try:
			response = requests.get(URL)  
			body = html.fromstring(response.text)
			titulo = getStringSemEspacamento(body.xpath('//div[@class="article primaryContent"]/h1/text()')[0])
		
		except (ValueError, TypeError):
			print URL,' - ERROR!'
			# se uma thread nao encontrar sucesso, a proxima pode encontrar entao utilizamos o continue e nao o break
			continue

		print URL
		
		if titulo:
			
			timestamp = getTimestampUnix(body.xpath('//div[@class="timestampHeader"]/text()')[0].encode('utf-8'))
			conteudoBruto = body.xpath('//div[@id="resizeableText"]/span[@class="focusParagraph"]/p/text() | \
				//div[@id="resizeableText"]/span[@class="articleLocation"]/p/text()')
			
			conteudo = getContent(conteudoBruto)
			
			TAMANHO_STRING_EMISSOR = 50
			
			checkEmissor = lambda x: len(conteudoBruto[x]) < TAMANHO_STRING_EMISSOR
			emissor = getStringSemEspacamento(conteudoBruto[0]) if checkEmissor(0) else getStringSemEspacamento(conteudoBruto[-1]) if checkEmissor(-1) else ''
			subTitulo = ''
			fonte = 'JornalEspecifico'
			subFonte = 'REUTERS'  
			url = URL 
			repercussao = verificaRepercussao(URL_parcial)
			
			noticia = {"timestamp" : timestamp, "subFonte" : subFonte, "titulo" : titulo, 
			"subTitulo" : subTitulo, "conteudo" : conteudo, "emissor" : emissor, "url" : url, "repercussao" : repercussao}

			noticia_id = collection.insert_one(noticia).inserted_id
			print noticia_id