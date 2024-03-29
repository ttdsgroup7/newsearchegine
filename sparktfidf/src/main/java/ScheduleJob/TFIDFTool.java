package ScheduleJob;

import lombok.extern.slf4j.Slf4j;
import org.apache.spark.ml.feature.*;
import org.apache.spark.ml.linalg.SparseVector;
import org.apache.spark.mllib.feature.Stemmer;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.tartarus.snowball.SnowballStemmer;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Pipeline;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Achieve TFIDF algorithm using spark
 * Read datasets from MySQL and save word index and tfidf in redis
 *
 * @author Hongchuan CAO
 * @version 1.0
 */
@Slf4j
public class TFIDFTool {

    public static void calTFIDF() {
        // connects to spark server
        SparkSession spark = SparkSession
                .builder()
                .master("spark://ubuntuserver1:7077")
//                .config("spark.executor.memory", "2g")
//                .config("spark.driver.memory", "2g")
//                .config("spark.memory.offHeap.enabled",true)
//                .config("spark.memory.offHeap.size","2g")
                .appName("TFIDF")
                .getOrCreate();
        // reads news content from mysql
        Dataset<Row> jdbcDF = spark.read()
                .format("jdbc")
                .option("driver", "com.mysql.cj.jdbc.Driver")
                .option("url", "jdbc:mysql://34.89.114.242:3306/TTDS_group7")
                .option("dbtable", "news")
                .option("user", "root")
                .option("password", "!ttds2021")
                .load();

        // docs (id, content)
        Dataset<Row> docs = jdbcDF.select("id", "content");

        // split sentence into words
        RegexTokenizer tokenizer = new RegexTokenizer()
                .setInputCol("content")
                .setOutputCol("rowWords")
                .setPattern("[a-z]+")
                .setGaps(false);
        // docs (id, content, rowWords)
        Dataset<Row> rawWordsData = tokenizer.transform(docs);

        // remove stop words
        StopWordsRemover stopWordsRemover = new StopWordsRemover().setInputCol("rowWords").setOutputCol("words");
        String[] stopWords = stopWordsRemover.getStopWords();
        // docs (id, content, rowWords, words)
        Dataset<Row> wordsData = stopWordsRemover.transform(rawWordsData);

        // stem words
//        Stemmer stemmed = new Stemmer().setInputCol("words").setOutputCol("stemmed").setLanguage("English");
//        Dataset<Row> stemmedWord = stemmed.transform(wordsData);


        // calculate frequency of words
        CountVectorizerModel cvModel = new CountVectorizer()
                .setInputCol("words")
                .setOutputCol("tf")
                .fit(wordsData);
        // docs (id, content, rowWords, words, tf)
        Dataset<Row> wordsTF = cvModel.transform(wordsData);

        // ========================================= save word -> index to redis ====================================
        // save word2index in redis (word => wordIndex)
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(50);
        poolConfig.setMinIdle(10);
        poolConfig.setMaxIdle(30);
        JedisPool pool = new JedisPool(poolConfig, "gc.caohongchuan.top", 6379, 10000, "!ttds2021");

        String[] word2index = cvModel.vocabulary();
        Map<String, String> index2word = new HashMap<>();
        for (int i = 0; i < word2index.length; i++) {
            index2word.put(word2index[i], String.valueOf(i));
        }

        try {
            Jedis jedis = pool.getResource();
            jedis.select(0);
            jedis.flushAll();
            jedis.hmset("tfidf-2022", index2word);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // ========================================================================================================


        Map<String, Map<String, String>> wordstf = new HashMap<>();
        Dataset<Row> wordtfvalue = wordsTF.select("id", "tf");
        List<Row> tfList = wordtfvalue.collectAsList();
        for (Row row : tfList) {
            String docId = String.valueOf(row.getLong(0));
            SparseVector tfidfStr = (SparseVector) row.get(1);
            int[] indices = tfidfStr.indices();
            double[] tfValues = tfidfStr.values();
            for (int i = 0; i < indices.length; i++) {
                String wordId = String.valueOf(indices[i]);
                String tfValue = String.valueOf(tfValues[i]);
                // update word2doc2value
                if (!wordstf.containsKey(wordId)) {
                    wordstf.put(wordId, new HashMap<>());
                }
                wordstf.get(wordId).put(docId, tfValue);
            }
        }
        // save tfidf in redis
        try {
            Jedis jedis = pool.getResource();
            jedis.select(2);
            Pipeline pipeline = jedis.pipelined();
            for (Map.Entry<String, Map<String, String>> entry : wordstf.entrySet()) {
                String word = entry.getKey();
                pipeline.hmset(String.valueOf(word), entry.getValue());
            }
            pipeline.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // calculate document frequency and tfidf
        IDF idf = new IDF().setInputCol("tf").setOutputCol("tfidf");
        IDFModel idfModel = idf.fit(wordsTF);
        Dataset<Row> tfidf = idfModel.transform(wordsTF);

        // ================================== save tfidf value in redis =======================================
        // Format tfidf into Map
        Map<String, Map<String, String>> word2doc2value = new HashMap<>();
        Map<String, Map<String, String>> doc2word2value = new HashMap<>();

        Dataset<Row> tfidfUseful = tfidf.select("id", "tfidf");

        List<Row> tfidfList = tfidfUseful.collectAsList();
        for (Row row : tfidfList) {
            String docId = String.valueOf(row.getLong(0));
            doc2word2value.put(docId, new HashMap<>());
            SparseVector tfidfStr = (SparseVector) row.get(1);
            int[] indices = tfidfStr.indices();
            double[] tfidfValues = tfidfStr.values();
            for (int i = 0; i < indices.length; i++) {
                String wordId = String.valueOf(indices[i]);
                String tfidfId = String.valueOf(tfidfValues[i]);
                // update doc2word2value
                doc2word2value.get(docId).put(wordId, tfidfId);
                // update word2doc2value
                if (!word2doc2value.containsKey(wordId)) {
                    word2doc2value.put(wordId, new HashMap<>());

                }
                word2doc2value.get(wordId).put(docId, tfidfId);
            }
        }

        // save tfidf in redis
        try {
            Jedis jedis = pool.getResource();
            jedis.select(0);
            Pipeline pipeline = jedis.pipelined();
            for (Map.Entry<String, Map<String, String>> entry : word2doc2value.entrySet()) {
                String word = entry.getKey();
                pipeline.hmset(String.valueOf(word), entry.getValue());
            }
            pipeline.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            Jedis jedis = pool.getResource();
            jedis.select(1);
            Pipeline pipeline = jedis.pipelined();
            for (Map.Entry<String, Map<String, String>> entry : doc2word2value.entrySet()) {
                String docId = entry.getKey();
                pipeline.hmset(docId, entry.getValue());
            }
            pipeline.close();
            jedis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // =====================================================================================================
    }

    public static void main(String[] args) {
        TFIDFTool.calTFIDF();
    }
}
