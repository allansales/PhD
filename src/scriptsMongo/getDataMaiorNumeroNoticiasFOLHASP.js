
function generenateDatesBetween(startDate, stopDate){
	var GAP_DAYS = 1;
	var dateMap = {};
    var currentDate = new Date(startDate);
    stopDate = new Date(stopDate);
    while (currentDate <= stopDate) {
    	var dataFormatada = (currentDate.getMonth()+1)+"-"+currentDate.getDate()+"-"+currentDate.getFullYear();
        dateMap[dataFormatada]=0;
        currentDate.setDate(currentDate.getDate() + GAP_DAYS); // Adiciona 1 dias;
    }
    return dateMap;
}	
	

var mapaDeDatas = generenateDatesBetween("01-01-2000","12-31-2015");

db.informacoesGerais.find({}).forEach(

	function (doc){
		
		if(["FOLHASP"].indexOf(doc.subFonte) > -1){
			
			var date = new Date(doc.timestamp * 1000);        
			var dateKey = (date.getMonth()+1)+"-"+date.getDate()+"-"+date.getFullYear();
			
			if(dateKey in mapaDeDatas){
				mapaDeDatas[dateKey]++;
			}else{
				print("Deu errado aqui: "+dateKey);
			}
		}
	}

);
var maiorData = ''
var maiorValor = -1
for (var data in mapaDeDatas){
	if(mapaDeDatas[data] > maiorValor){
		maiorData = data
		maiorValor  = mapaDeDatas[data]
	}
}
print(maiorData+" : "+mapaDeDatas[maiorData]);
