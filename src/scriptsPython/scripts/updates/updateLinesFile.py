from itertools import islice
from pymongo import MongoClient
from pymongo import ReturnDocument
from bson.objectid import ObjectId
import sys

client = MongoClient()
db = client.stocks
collection = db.informacoesGerais

with open('noticiasP5.txt','r') as f:
	while True:
		group = list(islice(f,2))
		if not group:
			break
		id = group[0].rstrip()
		print 'Id: ', id
		content = group[1].rstrip()
		print 'Content: ', content
		#content = group[4].rstrip()
		#collection.find_one_and_update({'_id':ObjectId(id)},{'$set':{'polarity':polarity}})

