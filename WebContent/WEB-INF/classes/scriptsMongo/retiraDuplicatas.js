var totalDeDelecoes = 0;

db.informacoesGerais.aggregate(
	[{
		$group:{
			_id:{timestamp:"$timestamp",subFonte:"$subFonte",titulo:"$titulo",subTitulo:"$subTitulo",repercussao:"$repercussao",
			emissor:"$emissor", url:"$url", conteudo:"$conteudo"},

			dups:{"$push":"$_id"},
			count:{$sum:1}
		}
	},
	{
		$match:{
			count:{$gt:1}
		}
	}], 
	{allowDiskUse:true}).forEach(function(doc){
		print(doc.dups);
		doc.dups.shift();
		totalDeDelecoes += doc.dups.length;
    	db.informacoesGerais.remove({_id:{$in: doc.dups}});
	});

print("Deleting total: "+totalDeDelecoes);