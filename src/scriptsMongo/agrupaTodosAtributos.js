
function generenateDatesBetween(startDate, stopDate){
	var GAP_DAYS = 1;
	var dateMap = {};
    var currentDate = new Date(startDate);
    stopDate = new Date(stopDate);
    while (currentDate <= stopDate) {
    	var dataFormatada = currentDate.getDay()+"-"+(currentDate.getMonth()+1)+"-"+currentDate.getDate()+"-"+currentDate.getFullYear();
        dateMap[dataFormatada]={"G1":{"pos":0,"neg":0,"neu":0,"qt":0,"c":0,"t":0,"f":0,"l":0,"g":0,"total":0},
        						"FOLHASP":{"pos":0,"neg":0,"neu":0,"qt":0,"c":0,"t":0,"f":0,"l":0,"g":0,"total":0},
        						"ESTADAO":{"pos":0,"neg":0,"neu":0,"qt":0,"c":0,"t":0,"f":0,"l":0,"g":0,"total":0}
        						};
        currentDate.setDate(currentDate.getDate() + GAP_DAYS); // Adiciona 1 dias;
    }
    return dateMap;
}	
	

var mapaDeDatas = generenateDatesBetween("01-01-2000","12-31-2015");

db.informacoesGerais.find({}).forEach(

	function (doc){
		
		var subFonte = doc.subFonte;
		var polaridade = doc.polarity;

		if(["ESTADAO","FOLHASP","G1"].indexOf(subFonte) > -1){
			
			var date = new Date(doc.timestamp * 1000);        
			var dateKey = date.getDay()+"-"+(date.getMonth()+1)+"-"+date.getDate()+"-"+date.getFullYear();
			
			if(dateKey in mapaDeDatas){
				
				mapaDeDatas[dateKey][subFonte]["qt"]++;
				
				if(polaridade == 1){
					mapaDeDatas[dateKey][subFonte]["pos"]++;
				}else if(polaridade == -1){
					mapaDeDatas[dateKey][subFonte]["neg"]++;
				}else if(polaridade == 0){
					mapaDeDatas[dateKey][subFonte]["neu"]++;
				}
				
				repercussoes = doc.repercussao.split(",");
				for (var rep in repercussoes){
					siglaValor = repercussoes[rep].split(":");
					sigla = siglaValor[0];
					valor = siglaValor[1];
					mapaDeDatas[dateKey][subFonte][sigla] += parseInt(valor);
				}
				
			}else{
				print("Deu errado aqui: "+dateKey);
			}
		}
	}

);

var DIAS_DA_SEMANA = new Array("Sun", "Mon",
      "Tue", "Wed", "Thu",
      "Fri", "Sat");

var MES = new Array("JAN", "FEV",
      "MAR", "ABR", "MAI",
      "JUN", "JUL", "AGO", 
      "SET", "OUT", "NOV", "DEZ");

var jornais = ["G1", "FOLHASP", "ESTADAO"];

print("Data,DiadaSemana,Mes,Dia,Ano,Jornal,qtNoticias,qtComentarios,qtTweets,qtFacebook,qtLinkedIn,qtGooglePlus,qtTotalEngajamento,qtPos,qtNeg,qtNeu");

for (var data in mapaDeDatas){
	
	dataSplit = data.split("-");
	diaDaSemana = dataSplit[0];
	mesNumerico = dataSplit[1]-1;
	mes = dataSplit[1];
	dia = dataSplit[2];
	ano = dataSplit[3];
	if(mes.length < 2){
		mes = "0"+mes
	}
	diaData = dataSplit[2];
	if(diaData.length < 2){
		diaData = "0"+dia
	}
	for (var jornal in jornais){
		
		qt = mapaDeDatas[data][jornais[jornal]]["qt"];
		comentarios = mapaDeDatas[data][jornais[jornal]]["c"];
		tweets = mapaDeDatas[data][jornais[jornal]]["t"];
		facebook = mapaDeDatas[data][jornais[jornal]]["f"];
		linkedIn = mapaDeDatas[data][jornais[jornal]]["l"];
		googleplus = mapaDeDatas[data][jornais[jornal]]["g"];
		total = mapaDeDatas[data][jornais[jornal]]["total"];
		pos = mapaDeDatas[data][jornais[jornal]]["pos"];
		neg = mapaDeDatas[data][jornais[jornal]]["neg"];
		neu = mapaDeDatas[data][jornais[jornal]]["neu"];
		
		print(diaData+"/"+mes+"/"+ano+","+diaDaSemana+","+mesNumerico+","+dia+","+ano+","+jornal+","+qt+","+comentarios+","+tweets+","+facebook+","+linkedIn+","+googleplus+","+total+","+pos+","+neg+","+neu);
	
	}
}
