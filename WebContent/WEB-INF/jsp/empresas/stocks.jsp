<!DOCTYPE HTML>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<link type="text/css" href="<c:url value="/css/bootstrap-select.css"/>"
	rel="stylesheet">
<link href="<c:url value="/bootstrap-3.1.1/css/bootstrap.min.css"/>"
	rel="stylesheet">
<link href="<c:url value="/datepicker/css/datepicker.css"/>"
	rel="stylesheet">
<link type="text/css" href="<c:url value="/css/docs_min.css"/>"
	rel="stylesheet">

<link type="text/css" href="<c:url value="/css/Autocomplete.css"/>"
	rel="stylesheet">
<title>StockHighNew</title>
</head>
<body>

	<input type='hidden' value='<c:url value="/imgs"/>' id='imgs' />

	<!-- Primeiro Menu do Cabeçalho -->
	<header id="top" class="navbar navbar-static-top bs-docs-nav"
		role="banner">
		<div class="container">
			<div class="navbar-header">
				<button class="navbar-toggle" data-target=".bs-navbar-collapse"
					data-toggle="collapse" type="button">
					<span class="sr-only">Toggle navigation</span> <span
						class="icon-bar"></span> <span class="icon-bar"></span> <span
						class="icon-bar"></span>
				</button>
				<a class="navbar-brand" href="../">Stocks</a>
			</div>
			<nav class="collapse navbar-collapse bs-navbar-collapse"
				role="navigation">
				<ul class="nav navbar-nav">
					<li class="active"><a href="../getting-started">Getting
							started</a></li>
					<li><a href="../css">CSS</a></li>
					<li><a href="../components">Components</a></li>
					<li><a href="../javascript">JavaScript</a></li>
					<li>
				</ul>
				<ul class="nav navbar-nav navbar-right">
					<li><a
						onclick="_gaq.push(['_trackEvent', 'Navbar', 'Community links', 'Expo']);"
						href="http://expo.getbootstrap.com">Expo</a></li>
					<li><a
						onclick="_gaq.push(['_trackEvent', 'Navbar', 'Community links', 'Blog']);"
						href="http://blog.getbootstrap.com">Blog</a></li>
				</ul>
			</nav>
		</div>
	</header>
	<!-- Fim do Menu do Cabeçalho -->




	<!-- Main da Página-->
	<div class="container bs-docs-container ">

		<!-- Barra de Busca-->
		<div id='searchs' style="max-width: 600px; margin: 0 auto 10px;">
			<div id="search" class="page-header input-group highlight">
				<input id="busca" type="text" name="nome" class="form-control"
					style="margin: 0 auto;"> <span class="input-group-btn">
					<button id="searchButton" class="btn btn-primary" type="button">
						&nbsp;&nbsp;&nbsp;&nbsp;<span class="glyphicon glyphicon-search"></span>&nbsp;&nbsp;&nbsp;&nbsp;
					</button>
					<button id="confMenu" class="btn btn-success" type="button">
						<span class="glyphicon glyphicon-th"></span>
					</button>
				</span>
			</div>
			<!-- Navegacao por botoes -->
			<div id="menu" class="page-header highlight " style="display: none;">

				<select class="selectpicker show-menu-arrow show-tick"
					data-header="Selecionar Grupo de A��es Por Setor, Subsetor e Segmento..."
					data-live-search="true" data-size="auto" data-width="90%"
					data-show-subtext="true">

					<optgroup label="Setor">
						<c:forEach items="${setoresList}" var="setores">
							<option data-subtext="Setor">${setores}</option>
						</c:forEach>
					</optgroup>
					<optgroup label="Sub Setor">
						<c:forEach items="${subSetoresList}" var="sub_setores">
							<option data-subtext="Sub-setor">${sub_setores}</option>
						</c:forEach>
					</optgroup>
					<optgroup label="Segmento">
						<c:forEach items="${segmentosList}" var="segmentos">
							<option data-subtext="Segmento">${segmentos}</option>
						</c:forEach>
					</optgroup>
				</select>

				<button id="confSearch" class="btn btn-info pull-right"
					type="button">
					<span class="glyphicon glyphicon-th"></span>
				</button>
			</div>
			<!-- fim de navegacao por botoes-->
			<div id="load" class="text-center" style="display: none;">
				<p class="lead">Loading...</p>
				<img src="<c:url value="/imgs/load.gif"/>">
			</div>
			
		</div>

		<!-- Fim da Barra de Busca-->





		<!-- Tabela de Resultados-->
		<div id="resultado" class="page-header"></div>
		<!-- Tabela de Resultados-->


		<!--botoes selecao do gráfico ou da notícia -->
		<div id="botoesMain" class="btn-group" style="display: none;">
			<button class="btn btn-primary active" id="btnContainer"
				type="button">
				<span class="glyphicon glyphicon-stats"></span>
			</button>
			<button class="btn btn-primary" id="btnTimeline" type="button">
				<span class="glyphicon glyphicon-list-alt"></span>
			</button>
		</div>
		<!-- fim botoes selecao do gráfico ou da notícia -->



		<!-- Analise de Grafico -->
		<div id="container"
			style="height: 500px; min-width: 500px; display: none;"></div>
		<!-- fim do grafico -->








		<!--Menu lateral direito -->
		<!-- div id="lateral" class="col-md-3">
				

			</div-->
		<!-- fim do Menu lateral direito-->



	</div>
	<!-- Fim do main da Página-->


	<footer class="bs-docs-footer" role="contentinfo">
		<div class="bs-docs-social">
			<p>
				Designed and built with all the love in the world by <a
					target="_blank" href="http://twitter.com/mdo">@zegildo</a>
			</p>
			<p>
			<p>
			<ul class="bs-docs-footer-links muted">
		</div>
	</footer>


</body>
<script type="text/javascript"
	src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.0/jquery.min.js"></script>
<script src="<c:url value="/js/bootstrap-select.js"/>"></script>
<script src="<c:url value="/js/highstock.js"/>"></script>
<script src="<c:url value="/js/modules/exporting.js"/>"></script>
<script src="<c:url value="/bootstrap-3.1.1/js/bootstrap.min.js"/>"></script>
<script src="<c:url value="/js/jquery.sparkline.min.js"/>"></script>
<script src="<c:url value="/js/Autocomplete.js"/>"></script>
<script src="<c:url value="/datepicker/js/bootstrap-datepicker.js"/>"></script>
<script src="<c:url value="/js/OurStocks.js"/>"></script>


</html>

