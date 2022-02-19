package top.caohongchuan.newsearch.tools;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

public class MongoDBTool {

    private static MongoClient mongoClient;

    public static MongoDatabase getMongoDBConnection(){
        mongoClient = new MongoClient("34.142.112.132", 27017);
        MongoDatabase mongoDatabase = mongoClient.getDatabase("TTDS_group7");
        return mongoDatabase;
    }

    public static void closeMongoDB(){
        mongoClient.close();
    }
}
