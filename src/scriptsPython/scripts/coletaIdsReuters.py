
import requests  
import sys
from lxml import html  
"""
Script feito para funcionar em juncao com o comando *parallel -X*

python coletaIdsReuters.py mesDiaAno
						------------
						   argv[1]

parallel -X python coletaIdsReuters.py {2}{3}{1} &> reutersIdsNovos.txt ::: {2007..2015} ::: {01..12} ::: {01..31} &

"""
if __name__ == "__main__":
	
	partesDaUrlComId = sys.argv[1:]
	
	for parteDaURLComId in partesDaUrlComId:

		URL = 'http://br.reuters.com/news/archive/businessNews?date=%s'%(parteDaURLComId)
		response = requests.get(URL)  
		body = html.fromstring(response.text)
		print '\n'.join(body.xpath('//div[@class="module"]/div/a/@href'))
		
	#------------------------------------------------------------------#
	# Apos a execucao desse comando eh possivel de serem encontrados   #
	# links repetidos nesse caso, temos que remove-los utilizando      #
	# o seguinte comando:                                              #
	#                                                                  #
	# $>cat nomeDoArquivo.txt | sort | uniq -u > novasaida.txt         #
	#																   #
	#------------------------------------------------------------------#
	