package top.caohongchuan.newsearch.tools;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import top.caohongchuan.newsearch.dao.TFIDF;

import java.util.*;

/**
 * Obtain news results according to post expression and data from mysql and redis
 *
 * @author Hongchuan CAO
 * @version 1.0
 */
@Component
public class SearchEngine {

    @Autowired
    TFIDF tfidf;

    class NewsResult {
        public boolean apply;
        public boolean not;
        public String word;
        public HashMap<String, Double> newsid;

        public NewsResult(boolean apply, boolean not) {
            this.apply = apply;
            this.not = not;
        }

        public NewsResult(boolean apply, boolean not, String word) {
            this.apply = apply;
            this.not = not;
            this.word = word;
        }
    }

    /**
     * Obtain docid from post expression
     *
     * @param postexpr
     * @return
     */
    public ArrayList<String> docIdFromPostExpr(ArrayList<String> postexpr) {
        LinkedList<NewsResult> stack = new LinkedList<>();
        try {
            for (String expr : postexpr) {
                if (expr.equals("OR")) {
                    NewsResult word2 = stack.removeLast();
                    NewsResult word1 = stack.removeLast();
                    stack.addLast(this.orOper(word1, word2));
                } else if (expr.equals("AND")) {
                    NewsResult word2 = stack.removeLast();
                    NewsResult word1 = stack.removeLast();
                    stack.addLast(this.andOper(word1, word2));
                } else if (expr.equals("NOT")) {
                    NewsResult word1 = stack.removeLast();
                    stack.addLast(this.notOper(word1));
                } else if (expr.length() >= 6 && expr.substring(0, 6).equals("WINDOW")) {
                    stack.addLast(this.windowOper(expr));
                } else {
                    stack.addLast(new NewsResult(false, false, expr));
                }
            }
        } catch (NoSuchElementException error) {
            throw error;
        }

        if (stack.size() == 1) {
            NewsResult newsResult = stack.getLast();

            // if only one word
            if (!newsResult.apply) {
                if (newsResult.word.equals("STOPWORD")) {
                    return new ArrayList<String>();
                }
                newsResult.newsid = StrToDoutil.strToDouble(tfidf.getTFIDF(newsResult.word));
                newsResult.apply = true;
            }

            HashMap<String, Double> res = newsResult.newsid;
            // sort news by its value
            List<Map.Entry<String, Double>> reslist = new ArrayList<Map.Entry<String, Double>>(res.entrySet());
            reslist.sort(new Comparator<Map.Entry<String, Double>>() {
                @Override
                public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2) {
                    return o2.getValue().compareTo(o1.getValue());
                }
            });

            ArrayList<String> newsresponse = new ArrayList<>();
            for (Map.Entry<String, Double> mapping : reslist) {
                newsresponse.add(mapping.getKey());
            }
            return newsresponse;
        } else {
            return new ArrayList<String>();
        }

    }

    public NewsResult orOper(NewsResult word1, NewsResult word2) {
        HashMap<String, Double> wordtodoc1 = null;
        HashMap<String, Double> wordtodoc2 = null;

        // obtain words' docid list
        if (!word1.apply) {
            if (!word1.word.equals("STOPWORD")) {
                wordtodoc1 = StrToDoutil.strToDouble(tfidf.getTFIDF(word1.word));
            }
        } else {
            wordtodoc1 = word1.newsid;
        }

        if (!word2.apply) {
            if (!word2.word.equals("STOPWORD")) {
                wordtodoc2 = StrToDoutil.strToDouble(tfidf.getTFIDF(word2.word));
            }
        } else {
            wordtodoc2 = word2.newsid;
        }

        // both are stopword
        if (wordtodoc1 == null && wordtodoc2 == null) {
            NewsResult result = new NewsResult(false, false);
            result.word = "STOPWORD";
            return result;
        }
        // One of them are stopword
        NewsResult result = new NewsResult(true, false);
        if (wordtodoc1 == null) {
            result.newsid = wordtodoc2;
            return result;
        }
        if (wordtodoc2 == null) {
            result.newsid = wordtodoc1;
            return result;
        }


        // union two set
        Set<String> keyset1 = wordtodoc1.keySet();
        Set<String> keyset2 = wordtodoc2.keySet();
        Set<String> mySet = new HashSet<String>();
        mySet.addAll(keyset1);
        mySet.addAll(keyset2);
        HashMap<String, Double> docToScore = new HashMap<>();
        for (String key : mySet) {
            if (wordtodoc1.containsKey(key) && wordtodoc2.containsKey(key)) {
                docToScore.put(key, wordtodoc1.get(key) + wordtodoc2.get(key));
            } else if (wordtodoc2.containsKey(key)) {
                docToScore.put(key, wordtodoc2.get(key));
            } else if (wordtodoc1.containsKey(key)) {
                docToScore.put(key, wordtodoc1.get(key));
            } else {
                continue;
            }
        }
        result.newsid = docToScore;
        return result;
    }

    public NewsResult andOper(NewsResult word1, NewsResult word2) {
        HashMap<String, Double> wordtodoc1 = null;
        HashMap<String, Double> wordtodoc2 = null;

        // obtain words' docid list
        if (!word1.apply) {
            if (!word1.word.equals("STOPWORD")) {
                wordtodoc1 = StrToDoutil.strToDouble(tfidf.getTFIDF(word1.word));
            }
        } else {
            wordtodoc1 = word1.newsid;
        }

        if (!word2.apply) {
            if (!word2.word.equals("STOPWORD")) {
                wordtodoc2 = StrToDoutil.strToDouble(tfidf.getTFIDF(word2.word));
            }
        } else {
            wordtodoc2 = word2.newsid;
        }

        // both are stopword
        if (wordtodoc1 == null && wordtodoc2 == null) {
            NewsResult result = new NewsResult(false, false);
            result.word = "STOPWORD";
            return result;
        }
        // One of them are stopword
        NewsResult result = new NewsResult(true, false);
        if (wordtodoc1 == null) {
            result.newsid = wordtodoc2;
            return result;
        }
        if (wordtodoc2 == null) {
            result.newsid = wordtodoc1;
            return result;
        }

        // intersection two set
        Set<String> keyset1 = wordtodoc1.keySet();
        Set<String> keyset2 = wordtodoc2.keySet();
        Set<String> mySet = new HashSet<String>();
        mySet.addAll(keyset1);
        mySet.retainAll(keyset2);
        HashMap<String, Double> docToScore = new HashMap<>();
        for (String key : mySet) {
            docToScore.put(key, wordtodoc1.get(key) + wordtodoc2.get(key));
        }
        result.newsid = docToScore;
        return result;
    }

    public NewsResult notOper(NewsResult word1) {
        HashMap<String, Double> wordtodoc1;
        if (!word1.apply) {
            wordtodoc1 = StrToDoutil.strToDouble(tfidf.getTFIDF(word1.word));
        } else {
            wordtodoc1 = word1.newsid;
        }

        NewsResult result = new NewsResult(true, true);
        result.newsid = wordtodoc1;
        return result;
    }

    public NewsResult windowOper(String word) {
        return null;
    }

}
