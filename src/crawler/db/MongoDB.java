package crawler.db;

import java.net.UnknownHostException;

import com.mongodb.DB;
import com.mongodb.MongoClient;

public class MongoDB {


	private static DB mongoDB;

	private MongoDB() {
	}

	public static synchronized DB getInstance() throws UnknownHostException {
		if (mongoDB == null){
			MongoClient client = new MongoClient();
			mongoDB = client.getDB("stocks");
		}
		return mongoDB;
	}

}
