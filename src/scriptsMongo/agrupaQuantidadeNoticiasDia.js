


var mapaDeDatas = {};


db.informacoesGerais.find({}).forEach(

	function (doc){
		
		if(["ESTADAO","FOLHASP","G1"].indexOf(doc.subFonte) > -1){
			
			var date = new Date(doc.timestamp * 1000); 
			var ano =  date.getFullYear();
			var mes = (date.getMonth()+1);
			if(mes<10) mes = "0"+mes
			var dia = date.getDate();
			if(dia<10) dia = "0"+dia
			var dateKey = ano+"-"+mes+"-"+dia;
			
			if(dateKey in mapaDeDatas){
				mapaDeDatas[dateKey]++;
			}else{
				mapaDeDatas[dateKey] = 0;
			}
		}
	}

);



print("Date,Quantidade");
for (var data in mapaDeDatas){
	
	print(data +","+mapaDeDatas[data])

}