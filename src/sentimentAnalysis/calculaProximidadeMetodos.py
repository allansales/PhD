"""
Script que avalia o desempenho de metodos em relacao a um gabarito

"""
with open('files/methodsResultadosSemTexto.csv','r') as f:
	
	metodos = f.next().split(",")
	valores_c = [0] * (len(metodos)-1)
	valores_e = [0] * (len(metodos)-1)

	for line in f:
		valores = map(int,line.split(","))
		gabarito = valores[0]
		for i in range(1,len(valores)):
			if(gabarito == valores[i]):
				valores_c[i-1]+=1
			else:
				valores_e[i-1]+=1

	porcentagens = [round(valor/165.0,2) for valor in valores_c]

	for x,y,z,w in zip(metodos[1:],valores_c,valores_e,porcentagens):
		print x,":"
		print "\t Porcentagem de acerto: ",w
		print "\t Acertos: ",y
		print "\t Erros: ",z, "\n"

"""
RESULTADO:

SentiWordNet :
	 Porcentagem de acerto:  0.59
	 Acertos:  97
	 Erros:  68 

SenticNet :
	 Porcentagem de acerto:  0.58
	 Acertos:  95
	 Erros:  70 

Emoticons :
	 Porcentagem de acerto:  0.05
	 Acertos:  8
	 Erros:  157 

Panas-t :
	 Porcentagem de acerto:  0.13
	 Acertos:  22
	 Erros:  143 

Sasa :
	 Porcentagem de acerto:  0.16
	 Acertos:  26
	 Erros:  139 

Happiness Index :
	 Porcentagem de acerto:  0.59
	 Acertos:  97
	 Erros:  68 

Sentistrength :
	 Porcentagem de acerto:  0.13
	 Acertos:  22
	 Erros:  143 

Emolex :
	 Porcentagem de acerto:  0.62
	 Acertos:  102
	 Erros:  63 

NRC Emotion :
	 Porcentagem de acerto:  0.33
	 Acertos:  54
	 Erros:  111 

Opnion Lexicon :
	 Porcentagem de acerto:  0.6
	 Acertos:  99
	 Erros:  66 

Emoticon Distant Supervisor :
	 Porcentagem de acerto:  0.56
	 Acertos:  93
	 Erros:  72 

SO-CAL :
	 Porcentagem de acerto:  0.73
	 Acertos:  121
	 Erros:  44 

Pattern.En :
	 Porcentagem de acerto:  0.59
	 Acertos:  97
	 Erros:  68 

Umigon :
	 Porcentagem de acerto:  0.45
	 Acertos:  75
	 Erros:  90 

AFINN :
	 Porcentagem de acerto:  0.68
	 Acertos:  113
	 Erros:  52 

Opnion Finder :
	 Porcentagem de acerto:  0.36
	 Acertos:  59
	 Erros:  106 

Vader :
	 Porcentagem de acerto:  0.73
	 Acertos:  120
	 Erros:  45 

Sentiment 140 :
	 Porcentagem de acerto:  0.63
	 Acertos:  104
	 Erros:  61 

Combined Method:
	 Porcentagem de acerto:  0.61
	 Acertos:  101
	 Erros:  64 


"""