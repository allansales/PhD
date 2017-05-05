
print("Data,DiadaSemana,Dia,Mes,Hora,Ano,Jornal,qtComentarios,qtTweets,qtFacebook,qtLinkedIn,qtGooglePlus,polaridade");

db.informacoesGerais.find({}).forEach(

	function (doc){
		
		var subFonte = doc.subFonte;
		var polaridade = doc.polarity;

		if(["ESTADAO","FOLHASP","G1"].indexOf(subFonte) > -1){
			var result = "";
			var date = new Date(doc.timestamp * 1000);    
			// Hours part from the timestamp
			var hora = date.getHours();
			// Minutes part from the timestamp
			var minutes = date.getMinutes();
			// Seconds part from the timestamp
			//var seconds = "0" + date.getSeconds();    
			var diaSemana = (date.getDay()+1);
			var mes = (date.getMonth()+1)+"";
			var dia = date.getDate()+"";	
			var ano = date.getFullYear();
			var data = ano+((mes.length<2) ? "0"+mes : mes)+((dia.length<2) ? "0"+dia : dia)+((hora.length<2) ? "0"+hora : hora)+((minuto.length<2) ? "0"+minuto : minuto);

			result += data+","+diaSemana+","+dia+","+mes+","+hora+","+ano+","+subFonte+",";
			
			var repercussoes = doc.repercussao;
			var engajamento = {"c":0,"t":0,"f":0,"l":0,"g":0};

			if(repercussoes){
				repercussoes = repercussoes.split(",");
				repercussoes.forEach(function(rep){
				    siglaValor = rep.split(":");
					sigla = siglaValor[0];
					if(sigla != "total"){
						valor = siglaValor[1];
						engajamento[sigla] = valor;
					}
				});
					
				for (var i in engajamento){
					result+=engajamento[i]+",";
				}
				result+=polaridade;
				print(result);
			}
		}
	}

);
