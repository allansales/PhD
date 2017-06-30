package crawler.noticias.translate;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.nio.charset.Charset;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;


public class GoogleTranslateOnline {

	public static void main(String[] args) throws Exception {

		getChunkNewsFromIdList();

		//getChunkNewsToTranslateFromIndice();

	}

	private static void getChunkNewsFromIdList() throws Exception{

		DBCollection collection = startMongo();

		DBCursor documentos = collection.find(new BasicDBObject());

		for (DBObject documento : documentos) {
			
			BasicDBObject doc = (BasicDBObject) documento;
			String id = doc.getString("_id");
			System.out.println(id);
				
			String titulo = doc.getString("titulo");
			String title = executeCommand(titulo);
			System.out.println(titulo);
			System.out.println(title);

			String subTitulo= doc.getString("subTitulo");
			String subTitle = executeCommand(subTitulo);
			System.out.println(subTitulo);
			System.out.println(subTitle);
				
			String conteudo = doc.getString("conteudo");
			String content = executeCommand(conteudo);
			System.out.println(conteudo);
			System.out.println(content);
	 
			BasicDBObject novaNoticia = adicionaCamposTraduzidos(doc, title, subTitle, content);
			
			collection.update(doc, new BasicDBObject("$set", novaNoticia));

		}			

	}

	private static BasicDBObject adicionaCamposTraduzidos(BasicDBObject doc, String title, String subTitle, String content) {
		BasicDBObject novaNoticia = new BasicDBObject();
		novaNoticia.put("timestamp", doc.getString("timestamp"));
		novaNoticia.put("subFonte", doc.getString("subFonte"));
		novaNoticia.put("titulo", doc.getString("titulo"));
		novaNoticia.put("subTitulo", doc.getString("subTitulo"));
		novaNoticia.put("conteudo", doc.getString("conteudo"));
		novaNoticia.put("emissor", doc.getString("emissor"));
		novaNoticia.put("url", doc.getString("url"));
		novaNoticia.put("repercussao", doc.getString("repercussao"));
		novaNoticia.put("idNoticia", doc.getString("idNoticia"));
		novaNoticia.put("title", title);
		novaNoticia.put("subTitle", subTitle);
		novaNoticia.put("content", content);
		return novaNoticia;
	}

//	private static void getChunkNewsToTranslateFromIndice()
//			throws UnknownHostException, IOException {
//		int inicio = 375001;
//
//		DBCollection collection = startMongo();
//		//Para evitar o MongoException$CursorNotFound ).addOption(Bytes.QUERYOPTION_NOTIMEOUT);
//		DBCursor cursor = collection.find().addOption(Bytes.QUERYOPTION_NOTIMEOUT).skip(inicio).limit(25000);
//
//		FileWriter arq = new FileWriter("noticias"+inicio+"-25000.txt");
//		PrintWriter grava = new PrintWriter(arq);
//		int count = inicio;
//		try {
//			while(cursor.hasNext()) {
//
//				BasicDBObject doc = (BasicDBObject)cursor.next();
//				//BasicDBObject novoDoc = new BasicDBObject(doc);
//
//				String id = doc.getString("_id");
//
//				String titulo = doc.getString("titulo");
//				String title = executeCommand(titulo);
//
//
//				String conteudo = doc.getString("conteudo");
//				String content = executeCommand(conteudo);;
//
//				grava.println(id);
//				grava.println();
//				grava.println(title);
//				grava.println();
//				grava.println(content);
//				grava.println();
//				grava.flush();
//				//novoDoc.append("title", title);
//				//novoDoc.append("content", content);
//				//collection.update(new BasicDBObject().append("_id", new ObjectId(id)), novoDoc);
//
//				System.out.println(++count);
//
//			}
//		}catch (Exception e){
//			e.printStackTrace();
//		} finally {
//			//grava.close();
//			//arq.close();
//			cursor.close();
//		}
//	}

	private static DBCollection startMongo() throws UnknownHostException {
		MongoClient client = new MongoClient();
		DB db = client.getDB("stocks");
		DBCollection collection = db.getCollection("tempNoticias");
		return collection;
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

		String command = "https://translate.googleapis.com/translate_a/single?client=gtx&sl=" + "pt" + "&tl=" + "en" + "&dt=t&q=" + bulk;
		String linha = "";   
		String total = "";
		
		try{
			URL url = new URL(command);
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
