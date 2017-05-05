
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
Datas em que o número de tweets no dia foi maior que 100000 para o ESTADAO
*/
var mapaDeDatas = ["3-28-2011","5-12-2011","12-3-2014","5-9-2011","5-10-2011","4-17-2012","2-24-2015","2-3-2015","2-27-2015"];

var palavras_titulo = {};
var palavras_conteudo = {};
var BADWORDS = [",",".","'","´"];

db.informacoesGerais.find({}).forEach(

	function (doc){
		
		var jornal = doc.subFonte;

		if("G1" == jornal){

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
