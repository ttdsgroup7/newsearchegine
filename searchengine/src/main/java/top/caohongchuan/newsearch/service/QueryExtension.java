package top.caohongchuan.newsearch.service;

import breeze.util.TopK;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import top.caohongchuan.newsearch.tools.StrToDoutil;
import top.caohongchuan.newsearch.tools.TopKTool;

import java.util.*;

@Service
public class QueryExtension {

    private int docNum = 2;
    private int wordNum = 2;

    @Autowired
    @Qualifier("redis1StringRedisTemplate")
    private StringRedisTemplate stringRedis1Template;
    @Autowired
    @Qualifier("redis2StringRedisTemplate")
    private StringRedisTemplate stringRedis2Template;

    public List<String> extension(List<String> docidsLimited, ArrayList<String> words) {
        Set<String> wordsIndex = new HashSet<>();
        HashOperations<String, String, String> opsForHash = stringRedis1Template.opsForHash();
        for (String word : words) {
            wordsIndex.add(opsForHash.get("tfidf-2022", word));
        }
        List<String> checkDoc = docidsLimited.subList(0, Math.min(docNum, docidsLimited.size()));
        // add additional word
        for (int i = 0; i < checkDoc.size(); i++) {
            Map<String, String> wordValue = opsForHash.entries(checkDoc.get(i));
            List<Map.Entry<String, Double>> addWordList = TopKTool.getTopK(wordNum, wordValue);
            for (int t = 0; t < wordNum; t++) {
                words.add(addWordList.get(t).getKey());
            }
        }
        List<String> wordsList = new ArrayList<>(wordsIndex);
        return this.tfidfQuery(wordsList);
    }

    public List<String> tfidfQuery(List<String> wordsList) {
        HashOperations<String, String, String> opsForHash = stringRedis1Template.opsForHash();
        HashMap<String, Double> wordtodoc1 = StrToDoutil.strToDouble(opsForHash.entries(wordsList.get(0)));

        Set<String> mySet = new HashSet<String>();
        Set<String> keyset1 = wordtodoc1.keySet();
        mySet.addAll(keyset1);
        for (int i = 1; i < wordsList.size(); i++) {
            HashMap<String, Double> wordtodoc2 = StrToDoutil.strToDouble(opsForHash.entries(wordsList.get(i)));
            Set<String> keyset2 = wordtodoc2.keySet();
            mySet.retainAll(keyset2);
        }

        HashMap<String, Double> docToScore = new HashMap<>();
        for (int i = 0; i < wordsList.size(); i++) {
            HashMap<String, Double> wordtodoc = StrToDoutil.strToDouble(opsForHash.entries(wordsList.get(i)));
            for(Map.Entry<String, Double> entry : wordtodoc.entrySet()){
                String docId = entry.getKey();
                if(mySet.contains(docId)){
                    if(!docToScore.containsKey(docId)){
                        docToScore.put(docId, 0.0);
                    }
                    docToScore.put(docId, docToScore.get(docId) + entry.getValue());
                }
            }
        }

        List<Map.Entry<String, Double>> docIdList = TopKTool.getTopKMap(100, docToScore);
        List<String> newsResponse = new ArrayList<>();
        for(Map.Entry<String, Double> docId : docIdList){
            newsResponse.add(docId.getKey());
        }
        return newsResponse;
    }
}
