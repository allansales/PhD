db.informacoesGerais.find().limit(9000).forEach(
	function (doc){
		print(doc.title);
	})
