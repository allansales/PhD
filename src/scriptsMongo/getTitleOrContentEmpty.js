db.informacoesGerais.find("this.title == null || this.content == null").forEach(
	
	function (doc){

		/*
		O objeto retornado aqui eh do tipo ObjetcId que para 
		converter o seu valor para String se faz necessario 
		acessar o atributo .str ou solicitar a funçao valueOf().

		Para mais informaçoes eh importate acessar o link:

		http://docs.mongodb.org/manual/reference/object-id/

		*/
		print(doc._id.str); 

	})
