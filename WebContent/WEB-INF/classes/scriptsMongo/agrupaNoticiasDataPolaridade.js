
function generenateDatesBetween(startDate, stopDate){
	var GAP_DAYS = 1;
	var dateMap = {};
    var currentDate = new Date(startDate);
    stopDate = new Date(stopDate);
    while (currentDate <= stopDate) {
    	var dataFormatada = currentDate.getDay()+"-"+(currentDate.getMonth()+1)+"-"+currentDate.getDate()+"-"+currentDate.getFullYear();
        dateMap[dataFormatada]={"G1":{"qt":0,"pos":0,"neg":0,"neu":0},
        						"FOLHASP":{"qt":0,"pos":0,"neg":0,"neu":0},
        						"ESTADAO":{"qt":0,"pos":0,"neg":0,"neu":0}
        						};
        currentDate.setDate(currentDate.getDate() + GAP_DAYS); // Adiciona 1 dias;
    }
    return dateMap;
}	
	

var mapaDeDatas = generenateDatesBetween("01-01-2000","12-31-2015");

db.informacoesGerais.find({}).forEach(

	function (doc){
		
		if(["ESTADAO","FOLHASP","G1"].indexOf(doc.subFonte) > -1){
			
			var date = new Date(doc.timestamp * 1000);        
			var dateKey = date.getDay()+"-"+(date.getMonth()+1)+"-"+date.getDate()+"-"+date.getFullYear();
			var polaridade = doc.polarity;

			if(dateKey in mapaDeDatas){
				mapaDeDatas[dateKey][doc.subFonte]["qt"]++;
				if(polaridade == 1){
					mapaDeDatas[dateKey][doc.subFonte]["pos"]++;
				}else if(polaridade == -1){
					mapaDeDatas[dateKey][doc.subFonte]["neg"]++;
				}else if(polaridade == 0){
					mapaDeDatas[dateKey][doc.subFonte]["neu"]++;
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

print("DiadaSemana,Mes,Dia,Ano,Jornal,qt,pos,neg,neu");

for (var data in mapaDeDatas){
	
	dataSplit = data.split("-");
	diaDaSemana = DIAS_DA_SEMANA[dataSplit[0]];
	mes = MES[dataSplit[1]-1];
	dia = dataSplit[2];
	ano = dataSplit[3];

	for (var jornal in jornais){
		qt = mapaDeDatas[data][jornais[jornal]]["qt"];
		pos = mapaDeDatas[data][jornais[jornal]]["pos"];
		neg = mapaDeDatas[data][jornais[jornal]]["neg"];
		neu = mapaDeDatas[data][jornais[jornal]]["neu"];
		
		print(diaDaSemana+","+mes+","+dia+","+ano+","+jornais[jornal]+","+qt+","+pos+","+neg+","+neu);
	
	}
}
