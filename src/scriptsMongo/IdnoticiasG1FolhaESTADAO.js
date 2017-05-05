db.informacoesGerais.find({}).forEach(

	function (doc){
		
		if(["G1", "ESTADAO","FOLHASP"].indexOf(doc.subFonte) > -1){
			
			print(doc._id.str);
			print(doc.content);
			
		}
	}

);
