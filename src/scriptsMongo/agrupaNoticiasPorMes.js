
function generenateDatesBetween(startMes, stopMes){
	
	var dateMap = {};
   
   	for (mes = startMes; mes <= stopMes; mes++){
   		dateMap[mes] = {"G1":0, "FOLHASP":0, "ESTADAO":0};
   	}
    return dateMap;
}	
	

var mapaDeDatas = generenateDatesBetween(1,12);

db.informacoesGerais.find({}).forEach(

	function (doc){
		
		if(["ESTADAO","FOLHASP","G1"].indexOf(doc.subFonte) > -1){
			
			var date = new Date(doc.timestamp * 1000);        
			var dateKey = (date.getMonth()+1);
			
			if(dateKey in mapaDeDatas){
				mapaDeDatas[dateKey][doc.subFonte]++;
			}else{
				print("Deu errado aqui: "+dateKey);
			}
		}
	}

);

var MES = new Array("JAN", "FEV",
      "MAR", "ABR", "MAI",
      "JUN", "JUL", "AGO", 
      "SET", "OUT", "NOV", "DEZ");

print("mes,jornal,quantidade");
for (var data in mapaDeDatas){

	print(MES[data-1]+",G1,"+mapaDeDatas[data]["G1"]);
	print(MES[data-1]+",FOLHASP,"+mapaDeDatas[data]["FOLHASP"])
	print(MES[data-1]+",ESTADAO,"+mapaDeDatas[data]["ESTADAO"]);
}