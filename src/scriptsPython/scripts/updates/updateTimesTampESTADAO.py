from pymongo import MongoClient
import sys

client = MongoClient()
db = client.stocks
collection = db.informacoesGerais

pares = sys.argv[1:]

for par in pares:
	par = par.split(" ")
	url = par[0]
	timestamp = par[1]	
	if timestamp == '0':
		print url
	#print collection.find_one({'url':url}, projection={'_id':True}),timestamp
	#print collection.find_one_and_update({'url':url},{'$set':{'timestamp':timestamp}}, projection={'_id':True})