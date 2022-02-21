package top.caohongchuan.newsearch.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Repository;

import java.util.*;

/**
 * Obtain data from redis
 * @author Hongchuan CAO
 * @version 1.0
 */
@Repository
public class TFIDF {

    @Autowired
    @Qualifier("redis1StringRedisTemplate")
    private StringRedisTemplate stringRedis1Template;

    /**
     * obtain word's tfidf value from redis
     * @param word
     * @return tfidf map word's docid and value
     */
    public HashMap<String, String> getTFIDF(String word){
        HashOperations<String, String, String> opsForHash = stringRedis1Template.opsForHash();
        String wordId = opsForHash.get("tfidf-2022", word);
        if (wordId == null) {
            return new HashMap<>();
        }
        Map<String, String> docid = opsForHash.entries(wordId);
        return (HashMap<String, String>)docid;
    }


}
