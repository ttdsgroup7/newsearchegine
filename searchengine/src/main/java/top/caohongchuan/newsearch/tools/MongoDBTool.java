//package top.caohongchuan.newsearch.tools;
//
//import com.mongodb.MongoClient;
//import com.mongodb.MongoCredential;
//import com.mongodb.ServerAddress;
//import com.mongodb.client.MongoDatabase;
//
//import java.util.Arrays;
//
//public class MongoDBTool {
//
//    private static MongoClient mongoClient;
//
//    public static MongoDatabase getMongoDBConnection(){
//        MongoCredential credential = MongoCredential.createCredential("ttds", "ttds_group7", "!ttds2021".toCharArray());
//        mongoClient = new MongoClient(new ServerAddress("gc2.caohongchuan.top", 27017), Arrays.asList(credential));
//        MongoDatabase mongoDatabase = mongoClient.getDatabase("ttds_group7");
//        return mongoDatabase;
//    }
//
//    public static void closeMongoDB(){
//        mongoClient.close();
//    }
//}
