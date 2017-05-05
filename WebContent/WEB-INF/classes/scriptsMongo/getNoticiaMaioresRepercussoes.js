	
var mapaDeDatas = {}

db.informacoesGerais.find({}).forEach(

	function (doc){
		
		var subFonte = doc.subFonte;
		var polaridade = doc.polarity;
		var url = doc.url;

		if(["ESTADAO","FOLHASP","G1"].indexOf(subFonte) > -1){	
			mapaDeDatas[url] = {"c":0,"t":0,"f":0,"l":0,"g":0,"total":0};
			repercussoes = doc.repercussao.split(",");
			for (var rep in repercussoes){
				siglaValor = repercussoes[rep].split(":");
				sigla = siglaValor[0];
				valor = siglaValor[1];
				mapaDeDatas[url][sigla] += parseInt(valor);
			}
				
		}
	}

);

var c = 0;
var urlC = "";

var t = 0;
var urlT = "";

var f = 0;
var urlF = "";

var l = 0;
var urlL = "";

var g = 0;
var urlG = "";

var total = 0;
var urlTotal = "";


for (var url in mapaDeDatas){
	if(mapaDeDatas[url]["c"] > c){
		urlC = url;
		c  = mapaDeDatas[url]["c"];
	}
	if(mapaDeDatas[url]["t"] > t){
		urlT = url;
		t  = mapaDeDatas[url]["t"];
	}
	if(mapaDeDatas[url]["f"] > f){
		urlF = url;
		f  = mapaDeDatas[url]["f"];
	}
	if(mapaDeDatas[url]["l"] > l){
		urlL = url
		l  = mapaDeDatas[url]["l"]
	}
	if(mapaDeDatas[url]["g"] > g){
		urlG = url
		g  = mapaDeDatas[url]["g"]
	}
	if(mapaDeDatas[url]["total"] > total){
		urlTotal = url
		total  = mapaDeDatas[url]["total"]
	}

}

print("c - "+urlC+" - "+c);
print("t - "+urlT+" - "+t);
print("f - "+urlF+" - "+f);
print("l - "+urlL+" - "+l);
print("g - "+urlG+" - "+g);
print("total - "+urlTotal+" - "+total);
