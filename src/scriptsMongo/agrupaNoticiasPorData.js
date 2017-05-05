
function generenateDatesBetween(startDate, stopDate){
	var GAP_DAYS = 1;
	var dateMap = {};
    var currentDate = new Date(startDate);
    stopDate = new Date(stopDate);
    while (currentDate <= stopDate) {
    	var dataFormatada = currentDate.getDay()+"-"+(currentDate.getMonth()+1)+"-"+currentDate.getDate()+"-"+currentDate.getFullYear();
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
			var dateKey = date.getDay()+"-"+(date.getMonth()+1)+"-"+date.getDate()+"-"+date.getFullYear();
			
			if(dateKey in mapaDeDatas){
				mapaDeDatas[dateKey][doc.subFonte]++;
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

print("Dia da Semana, Mes, Dia, Ano, Jornal, Quantidade");
for (var data in mapaDeDatas){
	dataSplit = data.split("-");

	diaDaSemana = DIAS_DA_SEMANA[dataSplit[0]];
	mes = MES[dataSplit[1]-1];
	dia = dataSplit[2];
	ano = dataSplit[3];

	print(diaDaSemana+","+mes+","+dia+","+ano+","+"G1,"+mapaDeDatas[data]["G1"]);
	print(diaDaSemana+","+mes+","+dia+","+ano+","+"FOLHASP,"+mapaDeDatas[data]["FOLHASP"]);
	print(diaDaSemana+","+mes+","+dia+","+ano+","+"ESTADAO,"+mapaDeDatas[data]["ESTADAO"]);

}