<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Boas Vindas!</title>
</head>
<body>
	It works!! ${string}
	<div id="menu">
       <ul>
           <li><a href="<c:url value="/mundo/lista"/>">Lista Paises</a></li>
       </ul>
	</div>
</body>
</html>