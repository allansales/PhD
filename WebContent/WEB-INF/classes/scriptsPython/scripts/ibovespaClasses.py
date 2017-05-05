import pandas as pd
import numpy as np

ibovespa = pd.read_csv('/home/zegildo/workspace/PhD/src/scriptsPython/indices/IndiceIbovespa.csv') 
variacoes = [float(x.replace(",",".")) for x in ibovespa.variacao.values]

print "Max:", np.amax(variacoes)
print "Min:",np.amin(variacoes)
print "Media:",np.mean(variacoes)
print "Mediana:", np.median(variacoes)
print "Desvio Padrao:",np.std(variacoes)

total=irr=alt=bax = 0.0

classes = []
for c in variacoes:
	total+=1
	if c <= 0.5 and c >= -0.5 :
		classes.append("=")
		irr+=1
	elif (c > 0.5):
		classes.append("+")
		alt+=1
	else:
		classes.append("-")
		bax+=1

print "bax: ",bax/total
print "alt: ",alt/total
print "irr: ",irr/total

ibovespa['classes'] = classes
#copia as informacoes do dataframe ibovespa sem o index_label
ibovespa.to_csv('ibovespa.csv', sep=',', index=False)