package top.caohongchuan.newsearch.service;

import com.alibaba.fastjson.JSON;
import com.mongodb.client.MongoDatabase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.caohongchuan.newsearch.tools.MongoDBTool;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

@Service
public class CorrectionService {

//    @Autowired
//    private MongoDBOperation mongoDBOperation;

    public static HashMap<String, Integer> term_cnt;
    public static HashMap<String, Integer> bi_cnt;
    public static ArrayList<Character> alphabet = new ArrayList<>();
    public static HashMap<String, HashMap<String, Double>> spell_error = new HashMap<>();
    public static Integer total;

    public CorrectionService() {
        MongoDatabase mongoTemplate = MongoDBTool.getMongoDBConnection();
        term_cnt = JSON.parseObject(mongoTemplate.getCollection("term_count").find().first().toJson(), HashMap.class);
        bi_cnt = JSON.parseObject(mongoTemplate.getCollection("bigram_count").find().first().toJson(), HashMap.class);
        MongoDBTool.closeMongoDB();

//        term_cnt = mongoDBOperation.getTermCnt();
//        bi_cnt = mongoDBOperation.getBiCnt();

        //http://norvig.com/ngrams/spell-errors.txt
        try {
            URL url = new URL("http://norvig.com/ngrams/spell-errors.txt");
            BufferedReader readfile = new BufferedReader(new InputStreamReader(url.openStream()));
            String i;
            while ((i = readfile.readLine()) != null) {
                String correct = i.split(":")[0].strip();
                String[] error = i.split(":")[1].split(",");
                Double prob = 1.0 / error.length;
                HashMap<String, Double> tmp = new HashMap<>();
                for (String j : error
                ) {
                    tmp.put(j.strip(), prob);
                }
                spell_error.put(correct, tmp);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        char a = 'a';
        for (int i = 0; i < 26; i++) {
            alphabet.add(a);
            a++;
        }

        total = term_cnt.size();
    }

    static class Pair<T> {
        T first;
        T second;

        public Pair(T first, T second) {
            this.first = first;
            this.second = second;
        }
    }

    // edit distance: insert, delete, replace
    public HashSet<String> editDis1(String word) {
        HashSet<String> res = new HashSet<String>();
        ArrayList<Pair<String>> split = new ArrayList<Pair<String>>();

        // "abc"=> ('',abc),(a,bc),(ab,c),(abc,'')
        for (int i = 0; i < word.length(); i++) {
            split.add(new Pair<String>(word.substring(0, i), word.substring(i)));
        }

        // Transpose
        for (Pair<String> i : split) {
            if (i.second.length() > 1) {
                // ignore words not exist
                if (term_cnt.containsKey(i.first + i.second.charAt(1) + i.second.charAt(0) + i.second.substring(1))) {
                    res.add(i.first + i.second.charAt(1) + i.second.charAt(0) + i.second.substring(1));
                }
            }
        }

        // insert,replace
        for (Pair<String> i : split
        ) {
            // add 26 alphabets
            for (char j : alphabet) {
                //insert
                if (term_cnt.containsKey(i.first + j + i.second)) {
                    res.add(i.first + j + i.second);
                }
                // replace
                if (i.second.length() > 0) {
                    if (term_cnt.containsKey(i.first + j + i.second.substring(1))) {
                        res.add(i.first + j + i.second.substring(1));
                    }
                }
            }
        }

        //delete
        for (Pair<String> i : split
        ) {
            if (i.second.length() > 0) {
                if (term_cnt.containsKey(i.first + i.second.substring(1))) {
                    res.add(i.first + i.second.substring(1));
                }
            }
        }

        return res;
    }

    public String calculate(HashSet<String> candidates, String mistake, Integer index, String[] query) {
        String res = mistake;
        double max_prob = -1000;
        for (String candidate : candidates
        ) {
            double cur_prob = 0.0;
            if (spell_error.get(candidate) != null && spell_error.get(candidate).get(mistake) != null) {
                cur_prob += Math.log(spell_error.get(candidate).get(mistake));
            } else {
                // not in common spelling error, get a small weight
                cur_prob += Math.log(0.001);
            }
            if (index > 0) {
                String previous = query[index - 1] + " " + candidate;
                if (bi_cnt.get(previous) != null && term_cnt.get(query[index - 1]) != null) {
                    cur_prob += Math.log((bi_cnt.get(previous) + 1.0) / (term_cnt.get(query[index - 1]) + total));
                } else {
                    cur_prob += Math.log(1.0 / total);
                }
            }
            if (index < query.length - 1) {
                String latter = candidate + " " + query[index + 1];
                if (bi_cnt.get(latter) != null && term_cnt.get(candidate) != null) {
                    cur_prob = Math.log((bi_cnt.get(latter) + 1.0) / (term_cnt.get(candidate) + total));
                } else {
                    cur_prob += Math.log(1.0 / total);
                }
            }
            // reduce weight of unigram
            cur_prob += Math.log(term_cnt.get(candidate) * 1.0 / total) / 10;

            if (cur_prob > max_prob) {
                max_prob = cur_prob;
                res = candidate;
            }
        }
        return res;
    }


}
