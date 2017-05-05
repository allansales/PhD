
function generenateDatesBetween(startDate, stopDate){
	var GAP_DAYS = 1;
	var dateMap = {};
    var currentDate = new Date(startDate);
    stopDate = new Date(stopDate);
    while (currentDate <= stopDate) {
    	var dataFormatada = (currentDate.getMonth()+1)+"-"+currentDate.getDate()+"-"+currentDate.getFullYear();
        dateMap[dataFormatada]={"qt":0,"c":0,"t":0,"f":0,"l":0,"g":0,"total":0};
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
			
			if(dateKey in mapaDeDatas){
				mapaDeDatas[dateKey]["qt"]++;

				repercussoes = doc.repercussao.split(",");
				for (var rep in repercussoes){
					siglaValor = repercussoes[rep].split(":");
					sigla = siglaValor[0];
					valor = siglaValor[1];
					mapaDeDatas[dateKey][sigla] += parseInt(valor);
				}

				
			}else{
				print("Deu errado aqui: "+dateKey);
			}
		}
	}

);


print("Data,Quantidade,Comentarios,Tweets,Facebook,LinkedIn,GooglePlus,TotalRepercussao");

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
	comentarios = mapaDeDatas[data]["c"];
	tweets = mapaDeDatas[data]["t"];
	facebook = mapaDeDatas[data]["f"];
	linkedIn = mapaDeDatas[data]["l"];
	googleplus = mapaDeDatas[data]["g"];
	total = mapaDeDatas[data]["total"];
	
	print(dia+"/"+mes+"/"+ano+","+qt+","+comentarios+","+tweets+","+facebook+","+linkedIn+","+googleplus+","+total);

	
}
