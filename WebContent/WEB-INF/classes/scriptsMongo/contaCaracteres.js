var caracteres = 0;
db.informacoesGerais.find().forEach(
	function (doc){
		caracteres +=doc.conteudo.length;
	})
print(caracteres);