db.informacoesGerais.find({subFonte:"ESTADAO"}).forEach(

	function (doc){
		print(doc.url)
	}


);
