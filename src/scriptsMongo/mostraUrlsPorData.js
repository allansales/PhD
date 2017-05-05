var mapaDeDatas = {};
db.informacoesGerais.find({}).forEach(

	function (doc){
		
		if(["G1"].indexOf(doc.subFonte) > -1){
			
			var date = new Date(doc.timestamp * 1000);        
			var dateKey = (date.getMonth()+1)+"/"+date.getDate()+"/"+date.getFullYear();
			
			if(dateKey == "6/26/2012"){

				if(dateKey in mapaDeDatas){
					mapaDeDatas[dateKey].push(doc.url);
				}else{
					mapaDeDatas[dateKey] = [doc.url];
				}
			}
			
		}
	}

);

for (var data in mapaDeDatas){
	
	print(data+": ");
	
	for (var url in mapaDeDatas[data]){
		print("\t"+mapaDeDatas[data][url]+"\n");

	}
}