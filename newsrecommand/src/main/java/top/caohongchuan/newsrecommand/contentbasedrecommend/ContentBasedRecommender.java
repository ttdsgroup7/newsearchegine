package top.caohongchuan.newsrecommand.contentbasedrecommend;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import top.caohongchuan.commonutil.datatypes.RecommendationItem;
import top.caohongchuan.newsrecommand.algorithms.RecommendAlgorithm;
import top.caohongchuan.newsrecommand.algorithms.RecommendKit;
import top.caohongchuan.newsrecommand.dao.ContentBasedDao;

import java.util.*;


/**
 * Procedure:
 * 1. getTFIDF() method to obtain their key words list.
 * 2. Then the system go over every user and calculate the similarity between
 * every news's key words list with user's preference list.
 * 3. After that, rank the news according to the similarities and recommend them to users.
 *
 * @author Hongchuan CAO
 */
@Service
@Slf4j
@PropertySource("classpath:application.yml")
public class ContentBasedRecommender implements RecommendAlgorithm {

    @Value("${newsrecommend.KEY_WORDS_NUM}")
    private int KEY_WORDS_NUM;
    @Value("${newsrecommend.NEWS_RECOMMEND_NUM}")
    private int NEWS_RECOMMEND_NUM;

    @Autowired
    UserPrefRefresher userPrefRefresher;
    @Autowired
    RecommendKit recommendKit;
    @Autowired
    ContentBasedDao contentBasedDao;
    @Autowired
    StringRedisTemplate stringRedisTemplate;


    @Override
    public void recommend(List<Integer> users) {
        log.info("CB start at " + new Date());
        try {
            // update tfidf value in user preference
            userPrefRefresher.refresh(users);

            // 用户喜好关键词列表
            HashMap<Integer, HashMap<String, HashMap<Long, Double>>> userPrefListMap = recommendKit
                    .getUserPrefListMap(users);
            List<Long> newsList = contentBasedDao.queryNewsIdByTime(RecommendKit.getInRecDate(-1));

            // (newid, theme)
            HashMap<Long, String> newsThemeMap = userPrefRefresher.getThemeTFIDFMap(newsList);

            ArrayList<RecommendationItem> userNewsList = new ArrayList<>();
            for (Integer userId : users) {
                Map<Long, Double> tempMatchMap = new HashMap<>();
                for (Long newsId : newsList) {
                    String theme = newsThemeMap.get(newsId);
                    if (null != userPrefListMap.get(userId).get(theme)) {
                        Map<Object, Object> newsWordsMap = stringRedisTemplate.opsForHash().entries(String.valueOf(newsId));
                        double matchValue = getMatchValue(userPrefListMap.get(userId).get(theme), newsWordsMap);
                        if (matchValue > 0) {
                            tempMatchMap.put(newsId, matchValue);
                        }
                    }
                }

                if (!tempMatchMap.isEmpty()) {
                    tempMatchMap = sortMapByValue(tempMatchMap);
                    Set<Long> toBeRecommended = tempMatchMap.keySet();
                    //过滤掉已经推荐过的新闻
                    recommendKit.filterReccedNews(toBeRecommended, userId);
                    //过滤掉用户已经看过的新闻
                    recommendKit.filterBrowsedNews(toBeRecommended, userId);
                    //如果可推荐新闻数目超过了系统默认为CB算法设置的单日推荐上限数（N），则去掉一部分多余的可推荐新闻，剩下的NEWS_RECOMMEND_NUM个新闻才进行推荐
                    if (toBeRecommended.size() > NEWS_RECOMMEND_NUM) {
                        RecommendKit.removeOverNews(toBeRecommended, NEWS_RECOMMEND_NUM);
                    }

                    // add enumerate(user_id, news_id) into RecommendationItem List
                    for (Long newsid : toBeRecommended) {
                        RecommendationItem re = new RecommendationItem();
                        re.setUser_id(userId);
                        re.setNews_id(newsid);
                        re.setDerive_algorithm(RecommendAlgorithm.CB);
                        userNewsList.add(re);
                    }
                }
            }
            recommendKit.insertRecommend(userNewsList);
            log.info("CB finished at " + new Date());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获得用户的关键词列表和新闻关键词列表的匹配程度
     * @param map
     * @param docWordList
     * @return
     */
    private double getMatchValue(HashMap<Long, Double> map, Map<Object, Object> docWordList) {
        double matchValue = 0;
        for (Map.Entry<Object, Object> keyword : docWordList.entrySet()) {
            Long wordIndex = Long.parseLong(String.valueOf(keyword.getKey()));
            Double tfidfValue = Double.parseDouble(String.valueOf(keyword.getValue()));
            if (map.containsKey(wordIndex)) {
                matchValue += tfidfValue + map.get(wordIndex);
            }
        }
        return matchValue;
    }

    /**
     * 使用 Map按value进行排序
     *
     * @param oriMap
     * @return
     */
    public static LinkedHashMap<Long, Double> sortMapByValue(Map<Long, Double> oriMap) {
        if (oriMap == null || oriMap.isEmpty()) {
            return null;
        }

        List<Map.Entry<Long, Double>> entryList = new ArrayList<Map.Entry<Long, Double>>(oriMap.entrySet());
        Collections.sort(entryList, new MapValueComparator());

        LinkedHashMap<Long, Double> sortedMap = new LinkedHashMap<>();
        Iterator<Map.Entry<Long, Double>> iter = entryList.iterator();
        Map.Entry<Long, Double> tmpEntry = null;
        while (iter.hasNext()) {
            tmpEntry = iter.next();
            sortedMap.put(tmpEntry.getKey(), tmpEntry.getValue());
        }
        return sortedMap;
    }
}
