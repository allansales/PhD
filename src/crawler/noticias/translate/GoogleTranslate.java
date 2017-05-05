package crawler.noticias.translate;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.Scanner;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.Bytes;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;


public class GoogleTranslate {

	public static void main(String[] args) throws Exception {

		getChunkNewsFromIdList();

		//getChunkNewsToTranslateFromIndice();

	}

	private static void getChunkNewsFromIdList() throws Exception{

		DBCollection mongoClient = startMongo();
		String path = "/home/zegildo/";
		Scanner scanner = new Scanner(new FileReader(path+"faltantesP5.txt")).useDelimiter("\\n");
		
		FileWriter arq = new FileWriter("noticiasP5.txt");
		PrintWriter grava = new PrintWriter(arq);
		
		int count = 0;

		while (scanner.hasNext()) {
			String id = scanner.next();
			BasicDBObject doc = (BasicDBObject) mongoClient.findOne(new ObjectId(id));
			
			id = doc.getString("_id");

			String titulo = doc.getString("titulo");
			String title = executeCommand(titulo);


			String conteudo = doc.getString("conteudo");
			String content = executeCommand(conteudo);;

			grava.println(id);
			grava.println();
			grava.println(title);
			grava.println();
			grava.println(content);
			grava.println();
			grava.flush();
			//novoDoc.append("title", title);
			//novoDoc.append("content", content);
			//mongoClient.update(new BasicDBObject().append("_id", new ObjectId(id)), novoDoc);

			System.out.println(++count);
		}



	}

	private static void getChunkNewsToTranslateFromIndice()
			throws UnknownHostException, IOException {
		int inicio = 375001;

		DBCollection mongoClient = startMongo();
		//Para evitar o MongoException$CursorNotFound ).addOption(Bytes.QUERYOPTION_NOTIMEOUT);
		DBCursor cursor = mongoClient.find().addOption(Bytes.QUERYOPTION_NOTIMEOUT).skip(inicio).limit(25000);

		FileWriter arq = new FileWriter("noticias"+inicio+"-25000.txt");
		PrintWriter grava = new PrintWriter(arq);
		int count = inicio;
		try {
			while(cursor.hasNext()) {

				BasicDBObject doc = (BasicDBObject)cursor.next();
				//BasicDBObject novoDoc = new BasicDBObject(doc);

				String id = doc.getString("_id");

				String titulo = doc.getString("titulo");
				String title = executeCommand(titulo);


				String conteudo = doc.getString("conteudo");
				String content = executeCommand(conteudo);;

				grava.println(id);
				grava.println();
				grava.println(title);
				grava.println();
				grava.println(content);
				grava.println();
				grava.flush();
				//novoDoc.append("title", title);
				//novoDoc.append("content", content);
				//mongoClient.update(new BasicDBObject().append("_id", new ObjectId(id)), novoDoc);

				System.out.println(++count);

			}
		}catch (Exception e){
			e.printStackTrace();
		} finally {
			//grava.close();
			//arq.close();
			cursor.close();
		}
	}

	private static DBCollection startMongo() throws UnknownHostException {
		MongoClient client = new MongoClient();
		DB db = client.getDB( "stocks" );
		DBCollection mongoClient = db.getCollection("informacoesGerais");
		return mongoClient;
	}

	private static String executeCommand(String command){

		String partes[] = command.split("\\.");

		String total = "";
		int fim = 0;

		while(fim < partes.length){

			String parte = partes[fim].trim();
			parte = parte.replaceAll("\"", "");
			parte = parte.replaceAll("'", "");
			parte = parte.replaceAll("\t", "");
			parte = parte.replaceAll("\n", "");

			if(parte.matches(".*\\w.*")){
				total += solicitaGoogle(parte)+".";

				/*while((bulk.length() < limit) && (fim < partes.length)){
				bulk += partes[fim]+".";
				fim++;
			}*/

			}
			fim++;
		}

		return total;
	}


	private static String solicitaGoogle(String bulk){

		try{

			bulk = URLEncoder.encode(bulk, "UTF-8");
		}catch(Exception e){
			e.printStackTrace();
		}

		String command = "http://translate.google.com.br/translate_a/single?client=t&sl=pt&tl=en&hl=pt-BR&dt=bd&dt=ex&dt=ld&dt=md&dt=qca&dt=rw&dt=rm&dt=ss&dt=t&dt=at&ie=UTF-8&oe=UTF-8&source=btn&ssel=0&tsel=0&kc=0&tk=521053|725896&q="+bulk;

		String linha = "";   
		String total = "";

		try{

			URL url = new   URL(command);  
			URLConnection conn = url.openConnection();  
			conn.addRequestProperty("User-Agent", "Mozilla/5.0");

			InputStreamReader in = new InputStreamReader(conn.getInputStream(), Charset.forName("UTF-8"));  
			BufferedReader reader = new BufferedReader(in);  
			while ((linha = reader.readLine()) != null) {       
				total+=linha + "\n";      
			}    
			total = organizaTraducao(total);
			reader.close();  

		} catch (Exception e) {   
			e.printStackTrace();  
		}

		return total;

	}


	private static String organizaTraducao(String retornoGoogle){
		String total = "";
		try{
			total = retornoGoogle.substring(2,retornoGoogle.indexOf("]],")+1);
		}catch(Exception e){
			System.out.println(retornoGoogle);
			e.printStackTrace();
		}
		String partes[] = total.split("],");

		String inglesTotal = "";

		for (String parte : partes) {
			String traducao[] = parte.split("\",\"");
			inglesTotal+=traducao[0].substring(2);
		}

		return inglesTotal;
	}

}
