package sentiment.db.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import rita.RiWordNet;
import net.didion.jwnl.JWNLException;
import net.didion.jwnl.data.IndexWord;
import net.didion.jwnl.data.IndexWordSet;
import net.didion.jwnl.data.POS;
import net.didion.jwnl.data.Synset;
import net.didion.jwnl.data.Word;
import net.didion.jwnl.dictionary.Dictionary;


public class WordNet {

	private static boolean ehFim(Word[] lemas, String fim){
		for (Word word : lemas) {
			String lema = word.getLemma();
			if(lema.equals(fim)){
				return true;
			}
		}	
		return false;
	}

	public static List<String> encontraCaminhoProfundidade(String inicio, String fim, String caminho, List<String> expandidos, Dictionary dic, List<String> paths) throws JWNLException{

		IndexWordSet WORDSET1 = dic.lookupAllIndexWords(inicio);
		POS p = WordnetPos.getWordnetPOSForKey(RiWordnet.ADJ);
		IndexWord start = WORDSET1.getIndexWord(p);
		expandidos.add(inicio);
		caminho+=inicio+"-";
		int senseCount1 = start.getSenseCount();

		for (int i = 1; i <= senseCount1; i++) {
			Synset s = start.getSense(i);
			Word w[] = s.getWords();
			for (Word word : w) {
				String lema = word.getLemma();
				if(expandidos.contains(lema)){
					continue;
				}else if(ehFim(w, fim)){
					paths.add(caminho+fim);
					continue;
				}else{
					encontraCaminhoProfundidade(lema, fim,caminho,expandidos, dic, paths);
				}
			}
		}
		return paths;
	}

	private static Synset[] buscarSentidos(String lema, Dictionary dic) throws JWNLException{

		IndexWordSet WORDSET1 = dic.lookupAllIndexWords(lema);
		POS p = WordnetPos.getWordnetPOSForKey(RiWordnet.ADJ);
		IndexWord start = WORDSET1.getIndexWord(p);
		if(start != null){
			return start.getSenses();
		}		 
		return null;
	}

	public static void encontraCaminhosGeral(String noRaiz, Dictionary dic) throws JWNLException{

		HashMap<String, Integer> distancias = new HashMap<String, Integer>();
		List<String> filaDeNos = new ArrayList<String>();
		filaDeNos.add(noRaiz);
		distancias.put(noRaiz, 0);

		while(!filaDeNos.isEmpty()){
			
			String noDaVez = filaDeNos.remove(0);
			int profundidade = distancias.get(noDaVez);
			
			Synset sense[] = buscarSentidos(noDaVez, dic);
			
			for (Synset sen : sense) {
				for (Word w : sen.getWords()) {
					String lema = w.getLemma();
					if(lema.equals("bazaar")){
						System.out.println("igual");
						break;
					}
					if(lema.contains("(")){
						lema = lema.substring(0, lema.indexOf("("));
					}
					if((distancias.get(lema)== null)){
						filaDeNos.add(lema);
						distancias.put(lema, profundidade+1);
					}
				}
			}
		}
		
		int max = 0;
		for (String adjetivo: distancias.keySet()) {
			int valor = distancias.get(adjetivo);
			 if(valor > max){
				 max = valor;
			 }
			 
			System.out.println(adjetivo+","+valor);
		}
		
		System.out.println("Maior valor de expansao:"+max);
	}
	public static String encontraCaminho(String inicio, String fim, Dictionary dic) throws JWNLException{
		boolean encontrado = false;
		Synset sense[] = buscarSentidos(inicio, dic);
		Set<String> expansaoAtual = new java.util.HashSet<String>();
		Set<String> novaExpansao = new java.util.HashSet<String>();

		for (Synset sen : sense) {
			for (Word w : sen.getWords()) {
				String lema = w.getLemma();
				if(lema.equals(fim)){
					return inicio+"_"+fim;
				}else if(!inicio.equals(lema)){
					expansaoAtual.add(inicio+"_"+lema);
				}
			}		
		}	

		while(!encontrado){
			for (String caminho : expansaoAtual) {
				String termo = caminho.substring(caminho.lastIndexOf("_")+1, caminho.length());
				sense = buscarSentidos(termo, dic);
				if(sense != null){
					for (Synset synset : sense) {
						for (Word w : synset.getWords()) {
							String lema = w.getLemma();
							if(lema.equals(fim)){
								return caminho+"_"+fim;
							}else if(!caminho.contains(lema)){
								novaExpansao.add(caminho+"_"+lema);
							}
						}	
					}
				}

			}
			expansaoAtual.clear();
			expansaoAtual.addAll(novaExpansao);
			novaExpansao.clear();
		}


		return null;

	}

	public static void main(String args[]) throws JWNLException{
		RiWordNet wordnet = new RiWordNet("/WordNet3.1");

		Dictionary dic = wordnet.getDictionary();

		//System.out.println(encontraCaminho("good", "bad", dic));
		encontraCaminhosGeral("good", dic);

	}
}
