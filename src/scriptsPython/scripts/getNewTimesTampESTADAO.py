#coding: utf-8
from pymongo import MongoClient
import sys
import requests  
from lxml import html  
from string import maketrans
import datetime

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
	>>> data = '09 Junho 2015 | 06h 01'
	>>> getTimestampUnix(data)
	3265874189

	"""
	time = timestamp.lower().split(' ')
	padrao = ""
	
	if len(time) == 6:
		timestamp = time[0]+"-"+MESES[time[1]]+"-"+time[2]+"-"+time[4]+"-"+time[5]
		padrao = '%d-%m-%Y-%Hh-%M'

	else:
		timestamp = time[0]+"-"+MESES[time[1]]+"-"+time[2]+"-"+time[4]
		padrao = '%d-%m-%Y-%H:%M'
	
	unixTimesTamp = int(datetime.datetime.strptime(timestamp, padrao).strftime("%s"))
	return unixTimesTamp

def getStringSemEspacamento(linha):
	"""
	Recebe uma string e retira as tabulacoes quebras de linha e espacos
	inicial e final em formato utf-8.

	"""
	return linha.encode('utf-8').translate(maketrans("\n\t\r","   ")).strip() 

def connectMongoDb():
	"""
	Cria uma conexao com o banco MongoDB, na base de dados stocks, na collection jornaisEspecificos.

	"""
	client = MongoClient()
	db = client.stocks
	collection = db.informacoesGerais
	return collection


"""



"""
if __name__ == "__main__":
	
	urls = sys.argv[1:]
	collection = connectMongoDb()
	
	for url in urls:
		print url
		try:
			response = requests.get(url)  
			body = html.fromstring(response.text)
			data = getStringSemEspacamento(body.xpath('//p[@class="data"]/text() | //span[@class="data"]/text()')[0])
			timestamp = getTimestampUnix(data)
			print timestamp
		except:
			print "ERROR!"
			# se uma thread nao encontrar sucesso, a proxima pode encontrar entao utilizamos o continue e nao o break
		#	continue


			#for doc in collection.find({"subFonte":"ESTADAO"}, limit=3):