<!DOCTYPE html>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html lang="en">
  <head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="description" content="">
    <meta name="author" content="">

    <title>StockNews</title>

    <!-- Bootstrap core CSS -->
    <link href="<c:url value="/bootstrap-3.1.1/css/bootstrap.min.css"/>" rel="stylesheet">

    <!-- Custom styles for this template -->
    <link href="<c:url value="/css/novoEstilo.css"/>" rel="stylesheet">

  </head>

  <body>

    <div class="container">
      <div class="header">
        <ul class="nav nav-pills pull-right">
          <li class="active"><a href="#">Home</a></li>
          <li><a href="#">About</a></li>
          <li><a href="#">Contact</a></li>
        </ul>
        <h3 class="text-muted">Stock</h3>
      </div>

      <div class="jumbotron">
        <h1>StockHighNews</h1>
        <p class="lead">Obtenha análise de informações de notícias, blogs e micro-blogs sobre a bolsa de valores BM&FBOVESPA.</p>
        <p><a class="btn btn-lg btn-success" href="<c:url value="/empresas/stocks"/>" role="button">StocksMe</a></p>
      </div>

      <div class="footer">
        <p>&copy; StockNews 2014</p>
      </div>

    </div> <!-- /container -->


    <!-- Bootstrap core JavaScript
    ================================================== -->
    <!-- Placed at the end of the document so the pages load faster -->
  </body>
</html>
