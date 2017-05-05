package sentiment.analysis;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Freeling {

	public static void main(String args[]) throws InterruptedException{

		//ProcessBuilder pb = 

		try {  //analyzer -f pt.cfg <src//sentiment//analysis//in.txt
			//String comandos[] = {"analyzer"};
			String cmd = "analyzer -f /usr/share/freeling/config/new_pt.cfg </home/zegildo/in.txt";
			String[] cmdarr = new String[]{"/bin/bash", "-c", cmd}; 
			Process p = Runtime.getRuntime().exec(cmdarr);

			BufferedReader sai = new BufferedReader(new InputStreamReader(p.getInputStream()));  

			String line = null;  
			while ((line = sai.readLine()) != null) {  
				System.out.println(line);  
			}  
			sai.close();

			//TRATAR ERRO COM: InputStream erro = p.getErrorStream();


		} catch (IOException e) {  
			e.printStackTrace();  
		}  

	}

}
