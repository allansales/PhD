from itertools import islice
from pymongo import MongoClient
from bson.objectid import ObjectId
from vaderSentiment import vaderSentiment

client = MongoClient()
db = client.stocks
collection = db.informacoesGerais

def checkText(text):
    analyse = vaderSentiment.sentiment(text)
    compound = analyse["compound"]
    
    if compound < -0.5:
        return -1
    elif compound > 0.5:
        return 1
    else:
        return 0
if __name__ == '__main__':

	with open('/home/zegildo/IdsNoticias.txt','r') as f:
		while True:
			group = list(islice(f,2))
			if not group:
				break
			id = group[0].rstrip()
			print 'Id: ', id
			content = group[1].rstrip()
			polarity = checkText(content)
			collection.find_one_and_update({'_id':ObjectId(id)},{'$set':{'polarity':polarity}})

