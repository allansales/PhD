
function generenateDatesBetween(startDate, stopDate){
	var GAP_DAYS = 1;
	var dateMap = {};
    var currentDate = new Date(startDate);
    stopDate = new Date(stopDate);
    while (currentDate <= stopDate) {
    	var dataFormatada = (currentDate.getMonth()+1)+"-"+currentDate.getFullYear();
        dateMap[dataFormatada]={"G1":0,"FOLHASP":0,"ESTADAO":0};
        currentDate.setDate(currentDate.getDate() + GAP_DAYS); // Adiciona 1 dias;
    }
    return dateMap;
}	
	

var mapaDeDatas = generenateDatesBetween("01-01-2000","12-31-2015");

db.informacoesGerais.find({}).forEach(

	function (doc){
		
		if(["ESTADAO","FOLHASP","G1"].indexOf(doc.subFonte) > -1){
			
			var date = new Date(doc.timestamp * 1000);        
			var dateKey = (date.getMonth()+1)+"-"+date.getFullYear();
			
			if(dateKey in mapaDeDatas){
				mapaDeDatas[dateKey][doc.subFonte]++;
			}else{
				print("Deu errado aqui: "+dateKey);
			}
		}
	}

);

print("data,G1,FOLHASP,ESTADAO");
for (var data in mapaDeDatas){
	print(data+","+mapaDeDatas[data]["G1"]+","+mapaDeDatas[data]["FOLHASP"]+","+mapaDeDatas[data]["ESTADAO"]);

}