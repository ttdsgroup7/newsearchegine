package top.caohongchuan.newsearch.tools;

import org.apache.spark.ml.feature.StopWordsRemover;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;

/**
 * preprocess word
 *
 * @author Hongchuan CAO
 * @version 1.0
 */
@Component
public class WordProcess {

    HashSet<String> stopwords;

    public String dealWord(String word) {
        getStopWord();
        if (stopwords.contains(word)) {
            return "STOPWORD";
        }
//        SnowballStemmer snowballStemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
//        String wordStem = snowballStemmer.stem(word).toString();
        return word;
    }

    public void getStopWord() {
        if (stopwords == null) {
            stopwords = new HashSet<String>(List.of(new StopWordsRemover().getStopWords()));
        }
    }
}
