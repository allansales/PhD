
function generenateDatesBetween(startDate, stopDate){
	var GAP_DAYS = 1;
	var dateMap = {};
    var currentDate = new Date(startDate);
    stopDate = new Date(stopDate);
    while (currentDate <= stopDate) {
    	var dataFormatada = (currentDate.getMonth()+1)+"-"+currentDate.getDate()+"-"+currentDate.getFullYear();
        dateMap[dataFormatada]={"qt":0,"pos":0,"neg":0,"neu":0};
        currentDate.setDate(currentDate.getDate() + GAP_DAYS); // Adiciona 1 dias;
    }
    return dateMap;
}	
	

var mapaDeDatas = generenateDatesBetween("01-01-2000","12-31-2015");

db.informacoesGerais.find({}).forEach(

	function (doc){
		
		if(["ESTADAO","FOLHASP","G1"].indexOf(doc.subFonte) > -1){
			
			var date = new Date(doc.timestamp * 1000);        
			var dateKey = (date.getMonth()+1)+"-"+date.getDate()+"-"+date.getFullYear();
			var polaridade = doc.polarity;

			if(dateKey in mapaDeDatas){
				mapaDeDatas[dateKey]["qt"]++;
				if(polaridade == 1){
					mapaDeDatas[dateKey]["pos"]++;
				}else if(polaridade == -1){
					mapaDeDatas[dateKey]["neg"]++;
				}else if(polaridade == 0){
					mapaDeDatas[dateKey]["neu"]++;
				}
				
			}else{
				print("Deu errado aqui: "+dateKey);
			}
		}
	}

);

print("Data,Quantidade,Positivas,Negativas,Neutras");

for (var data in mapaDeDatas){

	dataSplit = data.split("-");
	mes = dataSplit[0];
	if(mes.length < 2){
		mes = "0"+mes
	}
	dia = dataSplit[1];
	if(dia.length < 2){
		dia = "0"+dia
	}
	ano = dataSplit[2];

	qt = mapaDeDatas[data]["qt"];
	positivas = mapaDeDatas[data]["pos"];
	negativas = mapaDeDatas[data]["neg"];
	neutras = mapaDeDatas[data]["neu"];
	
	print(dia+"/"+mes+"/"+ano+","+qt+","+positivas+","+negativas+","+neutras);

	
}
