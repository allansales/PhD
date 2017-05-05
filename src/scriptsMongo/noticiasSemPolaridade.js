db.informacoesGerais.find({$and:[{subFonte:{$in:["FOLHASP","ESTADAO","G1"]}},{polarity:{$exists:false}}]})
.forEach(

function(doc){
	print(doc._id.str);
}
	);

