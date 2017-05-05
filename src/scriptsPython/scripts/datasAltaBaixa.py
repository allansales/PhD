import pandas as pd
import numpy as np
import os
dir = os.path.dirname(__file__)

ibovespa = pd.read_csv(dir+'/../indices/IndiceIbovespa.csv') 
datasQuentes = []
datasFrias = []
for index, row in ibovespa.iterrows():
	if float(row.variacao.replace(",",".")) > 0:
		datasQuentes.append(row.data)
	else:
		datasFrias.append(row.data)

print datasQuentes
print datasFrias
