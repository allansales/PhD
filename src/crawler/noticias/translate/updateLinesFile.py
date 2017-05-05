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
		group = list(islice(f,6))
		if not group:
			break
		id = group[0].rstrip()
		print 'Id: ', id
		title = group[2].rstrip()
		content = group[4].rstrip()
		collection.find_one_and_update({'_id':ObjectId(id)},{'$set':{'title':title,'content':content}})