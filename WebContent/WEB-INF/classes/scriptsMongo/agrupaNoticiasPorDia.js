
function generenateDatesBetween(startMes, stopMes){
	
	var dateMap = {};
   
   	for (mes = startMes; mes <= stopMes; mes++){
   		dateMap[mes] = {"G1":0, "FOLHASP":0, "ESTADAO":0};
   	}
    return dateMap;
}	
	

var mapaDeDatas = generenateDatesBetween(1,31);

db.informacoesGerais.find({}).forEach(

	function (doc){
		
		if(["ESTADAO","FOLHASP","G1"].indexOf(doc.subFonte) > -1){
			
			var date = new Date(doc.timestamp * 1000);        
			var dateKey = date.getDate();
			
			if(dateKey in mapaDeDatas){
				mapaDeDatas[dateKey][doc.subFonte]++;
			}else{
				print("Deu errado aqui: "+dateKey);
			}
		}
	}

);

print("dia,jornal,quantidade");
for (var data in mapaDeDatas){

	print(data+",G1,"+mapaDeDatas[data]["G1"]);
	print(data+",FOLHASP,"+mapaDeDatas[data]["FOLHASP"])
	print(data+",ESTADAO,"+mapaDeDatas[data]["ESTADAO"]);
}