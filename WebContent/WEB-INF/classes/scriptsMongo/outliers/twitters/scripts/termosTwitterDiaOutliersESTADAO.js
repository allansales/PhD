
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
Datas em que o número de tweets no dia foi maior que 2500 para o ESTADAO
*/
var mapaDeDatas = ["10-19-2012","4-14-2014","10-9-2014","10-15-2014","10-16-2014",
"10-27-2014","11-20-2014","11-21-2014","11-28-2014","12-15-2014","1-8-2015","1-23-2015","2-4-2015","2-5-2015", "2-26-2015"];

var palavras_titulo = {};
var palavras_conteudo = {};
var BADWORDS = [",",".","'","´"];

db.informacoesGerais.find({}).forEach(

	function (doc){
		
		var jornal = doc.subFonte;

		if("ESTADAO" == jornal){

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
