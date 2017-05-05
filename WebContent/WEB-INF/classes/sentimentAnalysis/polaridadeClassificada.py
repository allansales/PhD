with open('files/noticias.csv','r') as f,  open('files/news.txt','r') as n:
	
	for x, y in zip(f, n):
		#Utilizado para gerar o arquivo analiseConformidadeSaida.txt
		#print y.split(",")[0],"#" ,x.split(",")[0],x[-2]
		print x[-2]