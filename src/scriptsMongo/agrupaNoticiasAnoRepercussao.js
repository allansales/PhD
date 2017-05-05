
function generenateDatesBetween(startDate, stopDate){
	var dateMap = {};
	var currentData = startDate;
    while (currentData <= stopDate) {
       	dateMap[currentData]={"G1":{"qt":0,"c":0,"t":0,"f":0,"l":0,"g":0,"total":0},
        						"FOLHASP":{"qt":0,"c":0,"t":0,"f":0,"l":0,"g":0,"total":0},
        						"ESTADAO":{"qt":0,"c":0,"t":0,"f":0,"l":0,"g":0,"total":0}
        						};
        currentData++;
    }
    return dateMap;
}	
	

var mapaDeDatas = generenateDatesBetween(2000,2015);

db.informacoesGerais.find({}).forEach(

	function (doc){
		
		if(["ESTADAO","FOLHASP","G1"].indexOf(doc.subFonte) > -1){
			
			var date = new Date(doc.timestamp * 1000);        
			var dateKey = date.getFullYear();
			
			if(dateKey in mapaDeDatas){
				mapaDeDatas[dateKey][doc.subFonte]["qt"]++;

				repercussoes = doc.repercussao.split(",");
				for (var rep in repercussoes){
					siglaValor = repercussoes[rep].split(":");
					sigla = siglaValor[0];
					valor = siglaValor[1];
					mapaDeDatas[dateKey][doc.subFonte][sigla] += parseInt(valor);
				}

				
			}else{
				print("Deu errado aqui: "+dateKey);
			}
		}
	}

);

var jornais = ["G1", "FOLHASP", "ESTADAO"];

print("Ano, Jornal, Quantidade, Comentarios, Tweets, Facebook, LinkedIn, GooglePlus, TotalRepercussao");

for (var data in mapaDeDatas){
	
	for (var jornal in jornais){
		
		qt = mapaDeDatas[data][jornais[jornal]]["qt"];
		comentarios = mapaDeDatas[data][jornais[jornal]]["c"];
		tweets = mapaDeDatas[data][jornais[jornal]]["t"];
		facebook = mapaDeDatas[data][jornais[jornal]]["f"];
		linkedIn = mapaDeDatas[data][jornais[jornal]]["l"];
		googleplus = mapaDeDatas[data][jornais[jornal]]["g"];
		total = mapaDeDatas[data][jornais[jornal]]["total"];
		
		print(data+","+jornais[jornal]+","+qt+","+comentarios+","+tweets+","+facebook+","+linkedIn+","+googleplus+","+total);
	
	}
}
