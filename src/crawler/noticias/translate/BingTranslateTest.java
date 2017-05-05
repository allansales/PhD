package crawler.noticias.translate;
import com.memetix.mst.language.Language;
import com.memetix.mst.translate.Translate;
import com.mongodb.BasicDBObject;
import com.mongodb.Bytes;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;


public class BingTranslateTest {
	
	final static int limit = 8000;

	public static void main(String[] args) throws Exception {

		Translate.setClientId("StockZegildoApp");
		Translate.setClientSecret("R6MDbdUFjtWPa7JMC5DoVu1DxrCzILQU5VgjTKDKqcQ=");

		MongoClient client = new MongoClient();
		DB db = client.getDB( "stocks" );

		DBCollection mongoClient = db.getCollection("informacoesGerais");
		//Para evitar o MongoException$CursorNotFound ).addOption(Bytes.QUERYOPTION_NOTIMEOUT);
		DBCursor cursor = mongoClient.find().addOption(Bytes.QUERYOPTION_NOTIMEOUT);

		int count = 0;
		try {
			while(cursor.hasNext()) {

				BasicDBObject doc = (BasicDBObject)cursor.next();
				String titulo = doc.getString("titulo");
				String title = Translate.execute(titulo, Language.PORTUGUESE, Language.ENGLISH);

				String conteudo = doc.getString("conteudo");
				String content = "";
				
				if(conteudo.length() > limit){
					content = limitReached(conteudo);
				}else{
					content = Translate.execute(conteudo, Language.PORTUGUESE, Language.ENGLISH);
				}

				doc.append("title", title);
				doc.append("content", content);
				mongoClient.save(doc);
				System.out.println(++count);
			}
		} finally {
			cursor.close();
		}

	}

	private static String limitReached(String conteudo) throws Exception {

		String partes[] = conteudo.split("\\.");
		
		String total = "";
		int fim = 0;
		
		while(fim < partes.length){
			String bulk = "";
			
			while((bulk.length() < limit) && (fim < partes.length)){
				bulk += partes[fim]+".";
				fim++;
			}
			
			total += Translate.execute(bulk, Language.PORTUGUESE, Language.ENGLISH);
		}
		return total;
	}
}
