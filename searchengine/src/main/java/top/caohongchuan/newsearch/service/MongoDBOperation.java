//package top.caohongchuan.newsearch.service;
//
//import com.alibaba.fastjson.JSON;
//import com.mongodb.client.result.UpdateResult;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.mongodb.core.MongoTemplate;
//import org.springframework.data.mongodb.core.query.Criteria;
//import org.springframework.stereotype.Component;
//
//import java.util.HashMap;
//
//@Component
//public class MongoDBOperation {
//
//    @Autowired
//    MongoTemplate mongoTemplate;
//
//    public HashMap<String, Integer> getTermCnt() {
//         return JSON.parseObject(mongoTemplate.getCollection("term_count").find().first().toJson(), HashMap.class);
//    }
//
//    public HashMap<String, Integer> getBiCnt(){
//        return JSON.parseObject(mongoTemplate.getCollection("bigram_count").find().first().toJson(), HashMap.class);
//    }
//}
