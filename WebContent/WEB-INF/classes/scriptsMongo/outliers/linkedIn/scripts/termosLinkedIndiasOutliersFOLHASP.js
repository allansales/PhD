
function eliminaResquicios(palavras){

	for(var p in palavras){
		
		palavraAtual = palavras[p];
		palavraAtual = palavraAtual.toLowerCase();

		for(var b in BADWORDS){
			palavraAtual = palavraAtual.replace(BADWORDS[b],"");
		}
		palavras[p] = palavraAtual;
	}
}

function contaPalavras(texto, map){
	
	listPalavras = texto.split(" ");
	eliminaResquicios(listPalavras);

	for(var p in listPalavras){
		palavra = listPalavras[p];
		if(palavra in map){
			map[palavra]++;
		}else{
			map[palavra]=1;
		}
	}
}
	
	
/*
Datas em que o número de LinkedIn no dia foi maior que 500 para o ESTADAO
*/
var mapaDeDatas = ["1-12-2015","1-15-2015","1-21-2015","1-23-2015","1-24-2015","2-16-2015"];

var palavras_titulo = {};
var palavras_conteudo = {};
var BADWORDS = [",",".","'","´"];

db.informacoesGerais.find({}).forEach(

	function (doc){
		
		var jornal = doc.subFonte;

		if("FOLHASP" == jornal){

			var date = new Date(doc.timestamp * 1000);        
			var dateKey = (date.getMonth()+1)+"-"+date.getDate()+"-"+date.getFullYear();

			if(mapaDeDatas.indexOf(dateKey) > -1){
				
				titulo = doc.titulo;
				contaPalavras(titulo,palavras_titulo);
				
				conteudo = doc.conteudo;
				contaPalavras(titulo,palavras_conteudo);
			}
		}
	
	}

);

print("*********TITULO***********")
for (var a in palavras_titulo){
	
	print(a+":"+palavras_titulo[a]);
}

print("*******CONTEUDO***********")
for (var a in palavras_conteudo){
	
	print(a+":"+palavras_conteudo[a]);	
}
