import ScheduleJob.MongoDBTool;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.HashMap;
import java.util.Map;

public class Test {
    public static void main(String[] args) {
        redisTest();
    }

    public static void mongoTest(){
        Map<String, Map<String, String>> word2doc2value = new HashMap<>();
        Map<String, String> inter = new HashMap<>();
        inter.put("doc", "111");
        word2doc2value.put("word", inter);
        MongoDatabase mongoDatabase = MongoDBTool.getMongoDBConnection();
        MongoCollection<Map> collection = mongoDatabase.getCollection("tfidf", Map.class);
        collection.insertOne(word2doc2value);
        MongoDBTool.closeMongoDB();
    }

    public static void redisTest(){
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(50);
        poolConfig.setMinIdle(10);
        poolConfig.setMaxIdle(30);
        JedisPool pool = new JedisPool(poolConfig, "gc.caohongchuan.top", 6379, 10000, "!ttds2021");
        Jedis jedis = pool.getResource();
        jedis.select(0);
        jedis.flushAll();
        jedis.close();
    }
}
