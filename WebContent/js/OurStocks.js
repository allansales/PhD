$(document).ready(function(){
	var my_posts = $("[rel=tooltip]");
	var size = $(window).width();
	for(i=0;i<my_posts.length;i++){
		the_post = $(my_posts[i]);
		if(the_post.hasClass('invert') && size >=767 ){
			the_post.tooltip({ placement: 'left'});
			the_post.css("cursor","pointer");
		}else{
			the_post.tooltip({ placement: 'rigth'});
			the_post.css("cursor","pointer");
		}
	}
	new Autocomplete("busca", {
		srcType : "array", 
		onInput : function(newValue, oldValue) {
			var autocomplete = this;
			$.ajax({
				type: 'GET',
				url: '/PhD/produtos/busca.json?busca='+newValue,
				async:false,
				dataType: 'json',
				success: function(data) {
					autocomplete.addValues(data);
				},
				error : function(jqXHR, textStatus, errorThrown) {
					alert("Error status :"+textStatus);  
					alert("Error type :"+errorThrown);  
				}
			});

		}
	});

	$('#searchButton').click(function(){
		$.ajax({
			type: 'GET',
			url: '/PhD/acoes?q='+$('#busca').val(),
			async:false,
			dataType: 'json',
			beforeSend:function(data){ // Are not working with dataType:'jsonp'
			      $('#load').css('display','block');
			},
			success: function(data) {
				montaColuna(data);
				$('#load').css('display','none');
			},
			error : function(jqXHR, textStatus, errorThrown) {
				alert("Error status :"+textStatus);  
				alert("Error type :"+errorThrown);  
			}
		});

	});

	$('.selectpicker').selectpicker();

	$('#confMenu').click(
			function() {
				$( "#search" ).fadeOut( "slow",  function complete(){
					$( "#menu" ).fadeIn( "slow" );
				});
			});

	$('#confSearch').click(function() {
		$( "#menu" ).fadeOut( "slow",  function complete(){
			$( "#search" ).fadeIn( "slow" );
		});
	});

	var dow_prices = [16154.39
	                  ,16130.40
	                  ,16040.56
	                  ,16133.23
	                  ,16103.30
	                  ,16207.14
	                  ,16179.66
	                  ,16198.41
	                  ,16272.65
	                  ,16321.71
	                  ,16168.03
	                  ,16395.88
	                  ,16360.18
	                  ,16421.89
	                  ,16452.72
	                  ,16418.68
	                  ,16351.25
	                  ,16340.08
	                  ,16108.89
	                  ,16065.67
	                  ,16247.22
	                  ,16336.19
	                  ,16222.17
	                  ,16331.05
	                  ,16302.70
	                  ,16276.69
	                  ,16367.88
	                  ,16268.99
	                  ,16264.23
	                  ,16323.06
	                  ];
	dow_volume = [84064837
	              ,91252875
	              ,80564723
	              ,77717931
	              ,126582640
	              ,244559949
	              ,99462277
	              ,93984551
	              ,97625540
	              ,122110544
	              ,92760132
	              ,96119258
	              ,73975341
	              ,75901024
	              ,80685020
	              ,68208475
	              ,78150836
	              ,77898533
	              ,86165214
	              ,85660847
	              ,88926593
	              ,79145434
	              ,90113882
	              ,91530690
	              ,353667405
	              ,110620603
	              ,89111351
	              ,92767094
	              ,93625717
	              ,86374508
	              ]; 

	$('#dow').sparkline(dow_volume, {
		height: '1.5em', 
		type: 'bar', 
		barSpacing: 0, 
		barWidth: 3,
		barColor: '#ddd', 
		tooltipPrefix: 'Volume: '
	});

	$('#dow').sparkline(dow_prices, {
		composite: true, 
		height: '1.5em', 
		fillColor: false,
		lineColor:'#0000bf',
		spotColor: '#ff0000',
		minSpotColor: '#ff0000',
		maxSpotColor: '#ff0000',
		tooltipPrefix: 'Index: '

	});

	$('#nasdaq').sparkline(dow_volume, {
		height: '1.5em', 
		type: 'bar', 
		barSpacing: 0, 
		barWidth: 3,
		barColor: '#ddd', 
		tooltipPrefix: 'Volume: '
	});

	$('#nasdaq').sparkline(dow_prices, {
		composite: true, 
		height: '1.5em', 
		fillColor: false,
		lineColor:'#0000bf',
		spotColor: '#ff0000',
		minSpotColor: '#ff0000',
		maxSpotColor: '#ff0000',
		tooltipPrefix: 'Index: '

	});

});

function montaTelefones(telefones){

	var tels = telefones.split(","); 
	var resultado = "";

	for(var i = 0; i < tels.length; i++){

		resultado+="<small>"+tels[i]+"</small><br>";
	}
	return resultado; 
}
function informacoesPapeis(codigosNegociados){
	var informacoesPapeis = "<div>"+
	"<p class='lead'>Informacoes de Papeis</p>"+
	"<table class='table table-condensed'>"+
	"<tbody>"+
	"<tr>"+
	"<td><span class='label label-primary'>PAPEIS:</span></td>"+
	"<td><span class='label label-danger'>"+codigosNegociados+"</span></td>"+
	"</tr>"+
	"</tbody>"+
	"</table>"+
	"</div>";
	return informacoesPapeis;

}
function informacoesGerais(cnpj,codigo_cvm, atividadePrincipal,setor,sub_setor, segmento){

	var informacoesGerais = "<div>"+
	"<p class='lead'>Informacoes Gerais</p>"+
	"<table class='table table-condensed'>"+
	"<tbody>"+
	"<tr>"+
	"<td><span class='label label-primary'>CNPJ:</span></td>"+
	"<td>"+cnpj+"</td>"+
	"</tr>"+
	"<tr>"+
	"<td><span class='label label-primary'>CVM:</span></td>"+
	"<td>"+codigo_cvm+"</td>"+
	"</tr>"+
	"<tr>"+
	"<td><span class='label label-primary'>ATIVIDADE:</span></td>"+
	"<td class='text-justify'><small>"+atividadePrincipal+"</small></td>"+
	"</tr>"+
	"<tr>"+
	"<td><span class='label label-success'>SETOR:</span></td>"+
	"<td>"+setor+"</td>"+
	"</tr>"+
	"<tr>"+
	"<td><span class='label label-info'>SUB-SETOR:</span></td>"+
	"<td>"+sub_setor+"</td>"+
	"</tr>"+
	"<tr>"+
	"<td><span class='label label-warning'>SEGMENTO:</span></td>"+
	"<td>"+segmento+"</td>"+
	"</tr>"+
	"</tbody>"+
	"</table>"+
	"</div>";
	return informacoesGerais;
}

function informacoesDeEndereco(endereco, cidade, estado, cep, telefones, fax){

	var informacoesEndereco = "<div>"+
	"<p class='lead'>Endereço</p>"+
	"<table class='table table-condensed'>"+
	"<tbody>"+
	"<tr>"+
	"<td><span class='label label-primary'>Endereço</span></td>"+
	"<td>"+endereco+"</td>"+
	"</tr>"+
	"<tr>"+
	"<td><span class='label label-primary'>Cidade</span></td>"+
	"<td>"+cidade+"</td>"+
	"</tr>"+
	"<tr>"+
	"<td><span class='label label-primary'>Estado:</span></td>"+
	"<td>"+estado+"</td>"+
	"</tr>"+
	"<tr>"+
	"<td><span class='label label-primary'>CEP:</span></td>"+
	"<td>"+cep+"</td>"+
	"</tr>"+
	"<tr>"+
	"<td><span class='label label-default'><span class='glyphicon glyphicon-earphone'></span>TELEFONE:</span></td>"+
	"<td>"+telefones+"</td>"+
	"</tr>"+
	"<tr>"+
	"<td><span class='label label-default'><span class='glyphicon glyphicon-file'></span>FAX:</span></td>"+
	"<td>"+fax+"</td>"+
	"</tr>"+
	"</tbody>"+
	"</table>"+
	"</div>";
	return informacoesEndereco;
}
function divNoticias(){
	var noticias = "<div class='panel panel-info'>"+
	"<div class='panel-heading'>Notícias</div>"+
	"<div class='panel-body'>"+
	"<ul class='nav nav-pills nav-stacked'>"+
	"<li class='active'>"+
	"<a href='#'>"+ 
	"<span class='badge pull-right'>42</span>"+ 
	"Light" +
	"</a>"+
	"</li>"+
	"<li>" +
	"<a href='#'>Servicos</a>" +
	"</li>"+
	"<li>"+
	"<a href='#'>"+
	"<span class='badge pull-right'>3</span>"+
	"Energia" +
	"</a>"+
	"</li>"+
	"</ul>"+
	"</div>"+
	"</div>";
	return noticias;

}

function adicionaInfo(tipo,noticias){
	
	$('#tab_noticias').append("<div id='"+tipo+"' class='row'>"+
			"<h1><p class='lead'>"+tipo+":</p></h1>"+
			"<div id='news_Grapgh_"+tipo+"' class='col-md-6'></div>"+
			"<div id='news_Text_"+tipo+"' class='col-md-6'></div>"+
			"</div>");
	
	var comp = "<div id='news_"+tipo+"' class='mygrid-wrapper-div'></div>";
	$('#news_Text_'+tipo).append(comp);
	for (var i = 0; i < noticias.length; i++) {
		var noticis = noticias[i];
		var fonte = noticis.fonte;
		var sub_fonte = noticis.sub_fonte;
		var data_noticia = noticis.data_noticia;
		var titulo = noticis.titulo;
		var link = noticis.link;

		var componente= "<div class='media'>"+
		//"<a class='pull-left' href='#'>"+
		//"<img class='media-object' alt='64x64' data-src='holder.js/64x64' style='width: 64px; height: 64px;' src='data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHdpZHRoPSI2NCIgaGVpZ2h0PSI2NCI+PHJlY3Qgd2lkdGg9IjY0IiBoZWlnaHQ9IjY0IiBmaWxsPSIjZWVlIi8+PHRleHQgdGV4dC1hbmNob3I9Im1pZGRsZSIgeD0iMzIiIHk9IjMyIiBzdHlsZT0iZmlsbDojYWFhO2ZvbnQtd2VpZ2h0OmJvbGQ7Zm9udC1zaXplOjEycHg7Zm9udC1mYW1pbHk6QXJpYWwsSGVsdmV0aWNhLHNhbnMtc2VyaWY7ZG9taW5hbnQtYmFzZWxpbmU6Y2VudHJhbCI+NjR4NjQ8L3RleHQ+PC9zdmc+'>"+
		//"</a>"+
		"<div class='media-body'>"+
		"<h4 class='media-heading'><small>"+fonte+" <small>"+data_noticia+"</small></small></h4><small>"+titulo+"</small>"+
		"</div>"+
		"</div>";
		$('#news_'+tipo).append(componente);
	}
}

function datapickerNotica(){

	var data = "<table class='table table-condensed'>"+
	"<thead>"+
	"<tr>"+
	"<th>"+
	"Início:"+
	"<input id='dpd1' class='form-control' type='text' value=''>"+
	"</th>"+
	"<th>"+
	"Fim:"+
	"<input id='dpd2' class='form-control' type='text' value=''>"+
	"</th>"+
	"</tr>"+
	"</thead>"+
	"</table>";
	$('#tab_noticias').append(data);

	var nowTemp = new Date();
	var now = new Date(nowTemp.getFullYear(), nowTemp.getMonth(), nowTemp.getDate(), 0, 0, 0, 0);

	var checkin = $('#dpd1').datepicker().on('changeDate', function(ev) {
		if (ev.date.valueOf() > checkout.date.valueOf()) {
			var newDate = new Date(ev.date);
			newDate.setDate(newDate.getDate() + 1);
			checkout.setValue(newDate);
		}
		checkin.hide();
		$('#dpd2')[0].focus();
	}).data('datepicker');
	var checkout = $('#dpd2').datepicker().on('changeDate', function(ev) {
		if (ev.date.valueOf() < checkin.date.valueOf()) {
			var newDate = new Date(ev.date);
			newDate.setDate(newDate.getDate() - 1);
			checkin.setValue(newDate);
		}
		checkout.hide();
	}).data('datepicker');
}

function montaPanelNoticias(noticias){
	datapickerNotica();
	adicionaInfo("Jornais",noticias);
	adicionaInfo("Blogs",noticias);
	adicionaInfo("Twitter",noticias);
}

function graficoPizzaNoticias(nome){

	$('#n').append("<div id='pizzaNoticia' style='min-width: 310px; height: 550px; max-width: 400px; margin: 0 auto'></div>");
	criaGraficoPizza('pizzaNoticia');
}

function criaGraficoPizza(id){

	$('#'+id).highcharts({
		chart: {
			type: 'column'
		},
		title: {
			text: 'Stacked column chart'
		},
		xAxis: {
			categories: ['Apples', 'Oranges', 'Pears', 'Grapes', 'Bananas']
		},
		yAxis: {
			min: 0,
			title: {
				text: 'Total fruit consumption'
			},
			stackLabels: {
				enabled: true,
				style: {
					fontWeight: 'bold',
					color: (Highcharts.theme && Highcharts.theme.textColor) || 'gray'
				}
			}
		},
		legend: {
			align: 'right',
			x: -70,
			verticalAlign: 'top',
			y: 20,
			floating: true,
			backgroundColor: (Highcharts.theme && Highcharts.theme.background2) || 'white',
			borderColor: '#CCC',
			borderWidth: 1,
			shadow: false
		},
		tooltip: {
			formatter: function() {
				return '<b>'+ this.x +'</b><br/>'+
				this.series.name +': '+ this.y +'<br/>'+
				'Total: '+ this.point.stackTotal;
			}
		},
		plotOptions: {
			column: {
				stacking: 'normal',
				dataLabels: {
					enabled: true,
					color: (Highcharts.theme && Highcharts.theme.dataLabelsColor) || 'white',
					style: {
						textShadow: '0 0 3px black, 0 0 3px black'
					}
				}
			}
		},
		series: [{
			name: 'John',
			data: [53, 3, 4, 7, 2]
		}, {
			name: 'Jane',
			data: [2, 2, 3, 2, 1]
		}, {
			name: 'Joe',
			data: [3, 4, 4, 2, 5]
		}]
	});

}



function montaMenuPapeis(cotacoes){

	var papel = cotacoes[0][0];

	$('#g').append("<ul id='papeis' class='nav nav-pills'></ul>");

	$('#papeis').append("<li class='active'>"+
			"<a data-toggle='tab' href='#tab_"+papel+"'>"+papel+"</a>" +
	"</li>");

	for(var i = 1; i < cotacoes.length; i++){

		papel = cotacoes[i][0];
		$('#papeis').append("<li class=''>"+
				"<a data-toggle='tab' href='#tab_"+papel+"'>"+papel+"</a>" +
		"</li>");

	}
}

function montaConteudoMenuPapeis(cotacoes){

	var cotacao = cotacoes[0][1];
	var papel = cotacoes[0][0];
	trataCotacoes(cotacao);
	$('#g').append("<div id='graficos' class='tab-content'></div>");
	$('#graficos').append("<div id='tab_"+papel+"' class='tab-pane fade active in'></div>");
	montaGrafico(papel,"tab_"+papel);

	for(var i = 1; i < cotacoes.length; i++){
		papel = cotacoes[i][0];
		cotacao = cotacoes[i][1];	
		trataCotacoes(cotacao);
		$('#graficos').append("<div id='tab_"+papel+"' class='tab-pane fade'></div>");
		montaGrafico(papel,"tab_"+papel);

	}
}

function trataPapeis(nome,cotacoes){

	montaMenuPapeis(cotacoes);
	montaConteudoMenuPapeis(cotacoes);
	graficoPizzaNoticias(nome);

}

function montaGrafico(papel, id){

	var grafico = 
		"<div id='"+papel+"' style='height: 500px; min-width: 653px; display: block;'></div>";

	$('#'+id).append(grafico);

	// Create the chart
	$('#'+papel).highcharts('StockChart', {

		rangeSelector : {
			selected : 1
		},

		title : {
			text : papel+' Stock Price'
		},

		yAxis: [{
			title: {
				text: 'Cotação'
			},
			height: 200,
			lineWidth: 2
		}, {
			title: {
				text: 'Volume'
			},
			top: 300,
			height: 100,
			offset: 0,
			lineWidth: 2
		}],

		series : [{
			type:'line',
			name : 'Preço',
			data : precos,
			tooltip: {
				valueDecimals: 2
			}
		},
		{
			type: 'column',
			name: 'Volume',
			yAxis: 1,
			data: volumes
		}]
	});	
}

var precos = [];
var volumes = [];
function trataCotacoes(dadosCotacoes){
	precos = [];
	volumes = [];
	for(var i = 0; i < dadosCotacoes.length; i++){
		var time = Date.parse(dadosCotacoes[i][0].replace(" ","T"));
		var valor = dadosCotacoes[i][1];
		var volume = dadosCotacoes[i][2];
		precos.push([time,Number(valor)]);
		volumes.push([time,Number(volume)]);
	}
}

function montaBarraLateral(nomePregao,cnpj,codigo_cvm, atividadePrincipal,
		setor, sub_setor, segmento, codigosNegociados, endereco, cidade,
		estado, cep, telefone, fax, site){

	var  lateral = 

		$(divNoticias()+

				"<div class='panel panel-info'>"+
				"<div id='lateral' class='panel-body'>"+
				"<div class='text-center'>" +
				"<strong>"+nomePregao+"</strong>"+
				"<img class='img-circle' alt='100x100' src='"+$('#imgs').val()+'/'+cnpj+".jpg'/>"+		
				"</p>"+
				"</div>"+
				informacoesGerais(cnpj,codigo_cvm, atividadePrincipal,setor,sub_setor, segmento)+
				informacoesPapeis(codigosNegociados)+
				informacoesDeEndereco(endereco, cidade, estado, cep, montaTelefones(telefone), montaTelefones(fax))+	
				"</div>"+
				"<div class='panel-footer text-center'>"+
				"<a href="+site+" class='btn btn-primary btn-xs' role='button'>"+site+"</a>"+
				"</div>"+
		"</div>").hide().fadeIn(2000);

	$('#tab_geral').append(lateral);

}

function montaGeral(nome,cotacoes,noticias){

	$('#resultado').append(
			"<ul class='nav nav-tabs'>"+
			"<li class='active'>"+
			"<a data-toggle='tab' href='#cotacao'>Cotação</a>"+
			"</li>"+
			"<li class=''>"+
			"<a data-toggle='tab' href='#noticias'>Notícias</a>"+
			"</li>"+
			"<li class=''>"+
			"<a data-toggle='tab' href='#previsao'>Previsão</a>"+
			"</li>"+
			"<li class=''>"+
			"<a data-toggle='tab' href='#geral'>Geral</a>"+
			"</li>"+
			"</ul>"+
			"<div id='menuPrincipal' class='tab-content'>" +
			"<div id='cotacao' class='tab-pane fade active in'><p></p>" +
			"<div class='panel-body'>" +
			"<div class='container'>"+
			"<div id='tab_cotacao' class='row'>"+
			"</div>"+
			"</div>"+
			"</div>"+
			"</div>"+
			"<div id='noticias' class='tab-pane fade'><p></p>" +
			"<div id='tab_noticias' class='panel-body'></div>"+
			"</div>"+
			"<div id='previsao' class='tab-pane fade'><p></p>" +
			"<div id='tab_previsao' class='panel-body'></div>"+
			"</div>" +
			"<div id='geral' class='tab-pane fade'><p></p>" +
			"<div id='tab_geral' class='panel-body'></div>"+
			"</div>"+
	"</div>");
	$('#tab_cotacao').append("<div id='g' class='col-md-7'></div>");
	$('#tab_cotacao').append("<div id='n' class='col-md-5'></div>");
	montaPanelNoticias(noticias);
	trataPapeis(nome,cotacoes);

}

function montaColuna(data){

	for(var i = 0; i < data.length; i++){

		var empresa = data[i].empresa;
		var cotacoes = data[i].cotacoes;
		var noticias = data[i].noticias;		
		var cnpj = empresa.cnpj.trim();
		var codigo_cvm = empresa.cod_cvm;
		var nomeDaEmpresa = empresa.nome_empresa+" ";
		var nomePregao = empresa.nome_pregao;
		var codigosNegociados = empresa.cod_negociacao;
		var atividadePrincipal = empresa.atividade_principal;
		var setor = empresa.setor;
		var sub_setor = empresa.sub_setor;
		var segmento = empresa.segmento;
		var endereco = empresa.endereco;
		var cidade = empresa.cidade;
		var estado = empresa.estado;
		var cep = empresa.cep;
		var telefone = empresa.telefone;
		var fax = empresa.fax;
		var site = empresa.site;


		var teste1 = "danger";

		var head = 
			$("<div class='text-center'>"+
					"<h1>"+nomeDaEmpresa+
					"<span class='label label-"+teste1+"'>"+
					"-4%</span>"+
					"</h1>"+
			"</div>").hide().fadeIn(2000);

		$("#resultado").append(head);

		montaGeral(nomeDaEmpresa,cotacoes,noticias);

		montaBarraLateral(nomePregao,cnpj,codigo_cvm, atividadePrincipal,
				setor, sub_setor, segmento, codigosNegociados, endereco, cidade,
				estado, cep, telefone, fax, site);


		//Informacoes para contato...


		//Midias sociais..
		var emails = empresa.emails;
		var twitter_empresa = empresa.twitter_empresa;
		var facebook_empresa = empresa.twitter_empresa;




	}

	function geraTabela(codNegociacao,label, nomeDaEmpresa, volume, idGrafico, porcentagem){
		//Cabecalho
		"<table class='table table-hover'>"+
		"<thead>"+
		"<tr>"+
		"<th>#</th>"+
		"<th>PregÃ£o</th>"+
		"<th>Name</th>"+
		"<th>Volume</th>"+
		"<th>Graph</th>"+
		"<th>Partial</th>"+
		"</tr>"+
		"</thead>"+


		//linha completa
		"<tr>"+
		"<td>"+
		"<div class='input-group'>"+
		"<input type='checkbox'>"+
		"</div>"+
		"</td>"+
		"<td><span class='label label-"+label+"'>"+codNegociacao+"</span></td>"+
		"<td>"+nomeDaEmpresa+"</td>"+
		"<td> R$"+volume+"</td>"+
		"<td>"+
		"<div id="+idGrafico+">"+
		"<canvas"+
		"style='display: inline-block; width: 90px; height: 40px; vertical-align: top;"+
		"width='90' height='40'></canvas>"+
		"</div>"+
		"</td>"+
		"<td><span class='text-"+label+"'>"+porcentagem+"%</span></td>"+
		"</tr>"+
		"</table>";
	}

}

