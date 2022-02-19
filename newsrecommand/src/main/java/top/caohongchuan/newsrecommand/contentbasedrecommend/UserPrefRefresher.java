package top.caohongchuan.newsrecommand.contentbasedrecommend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import top.caohongchuan.commonutil.datatypes.BrowsedHistory;
import top.caohongchuan.commonutil.datatypes.NewsItem;
import top.caohongchuan.commonutil.datatypes.RecommendOneNews;
import top.caohongchuan.commonutil.datatypes.UserItem;
import top.caohongchuan.newsrecommand.algorithms.JsonKit;
import top.caohongchuan.newsrecommand.algorithms.RecommendKit;
import top.caohongchuan.newsrecommand.dao.ContentBasedDao;

import java.util.*;

/**
 * @author qianxinyao
 * @email tomqianmaple@gmail.com
 * @github https://github.com/bluemapleman
 * @date 2016年11月3日
 * 每次用户浏览新的新闻时，用以更新用户的喜好关键词列表
 */

@Service
@PropertySource("classpath:application.yml")
public class UserPrefRefresher {

    @Value("${newsrecommend.KEY_WORDS_NUM}")
    private int KEY_WORDS_NUM;

    @Value("${newsrecommend.DEC_COEE}")
    private double DEC_COEE;

    @Autowired
    ContentBasedDao contentBasedDao;
    @Autowired
    RecommendKit recommendKit;
    @Autowired
    StringRedisTemplate stringRedisTemplate;

    public void refresh() {
        refresh(recommendKit.getUserList());
    }

    /**
     * Update tfidf value in user preference
     * Decrease exited word and add new word
     *
     * @param userIdsCol
     */
    @SuppressWarnings("unchecked")
    public void refresh(List<Integer> userIdsCol) {
        //首先对用户的喜好关键词列表进行衰减更新
        autoDecRefresh(userIdsCol);

        //userBrowsexMap:(userid -> [newsid])
        BrowsedHistory browsedHistory = getBrowsedHistoryMap();
        HashMap<Integer, ArrayList<Long>> userBrowsedMap = browsedHistory.getUserNews();
        Set<Long> newsRelated = browsedHistory.getNewsSet();
        // no record, return
        if (userBrowsedMap.size() == 0) {
            return;
        }

        //userPrefListMap:(userid -> (theme -> (word -> tfidf)))
        HashMap<Integer, HashMap<String, HashMap<Long, Double>>> userPrefListMap =
                recommendKit.getUserPrefListMap(new ArrayList<Integer>(userBrowsedMap.keySet()));

        HashMap<Long, String> themeTFIDFMap = getThemeTFIDFMap(new ArrayList<>(newsRelated));

        //开始遍历用户浏览记录，更新用户喜好关键词列表
        //对每个用户（外层循环），循环他所看过的每条新闻（内层循环），对每个新闻，更新它的关键词列表到用户的对应模块中
        for (Map.Entry<Integer, ArrayList<Long>> entry : userBrowsedMap.entrySet()) {
            Integer userId = entry.getKey();
            ArrayList<Long> newsList = entry.getValue();
            for (Long news : newsList) {
                String theme = themeTFIDFMap.get(news);
                //获得对应模块的（关键词：喜好）map
                if(!userPrefListMap.get(userId).containsKey(theme)){
                    HashMap<Long, Double> wordMap = new HashMap<>();
                    userPrefListMap.get(userId).put(theme, wordMap);
                }
                HashMap<Long, Double> rateMap = userPrefListMap.get(userId).get(theme);

                //获得新闻的（关键词：TFIDF值）map
                Map<Object, Object> keywordList = stringRedisTemplate.opsForHash().entries(String.valueOf(news));
                for (Map.Entry<Object, Object> entry1 : keywordList.entrySet()) {
                    Long wordIndex = Long.parseLong(String.valueOf(entry1.getKey()));
                    Double tfidfValue = Double.parseDouble(String.valueOf(entry1.getValue()));
                    if (rateMap.containsKey(wordIndex)) {
                        rateMap.put(wordIndex, rateMap.get(wordIndex) + tfidfValue);
                    } else {
                        rateMap.put(wordIndex, tfidfValue);
                    }
                }
            }
        }

        // save user_id and pref_list in userList then update them in database
        List<UserItem> userItemList = new ArrayList<>();
        for (Map.Entry<Integer, HashMap<String, HashMap<Long, Double>>> entry : userPrefListMap.entrySet()) {
            UserItem userItem = new UserItem();
            userItem.setId(entry.getKey());
            userItem.setPref_list(JsonKit.jsonPrefToString(entry.getValue()));
            userItemList.add(userItem);
        }

        // update database
        contentBasedDao.updateUserPrefList(userItemList);

    }

    /**
     * 所有用户的喜好关键词列表TFIDF值随时间进行自动衰减更新
     */
    public void autoDecRefresh() {
        autoDecRefresh(recommendKit.getUserList());
    }

    /**
     * Decrease tfidf with time going on (and delete some words with relative small number)
     *
     * @param userIdsCol
     */
    public void autoDecRefresh(List<Integer> userIdsCol) {
        try {
            // obtain user preference list
            List<UserItem> userItems = contentBasedDao.queryUserPref(userIdsCol);
            // store the keywords which should be removed
            ArrayList<Long> keywordToDelete = new ArrayList<>();
            //(userid -> (theme -> (word, tfidf)))
            HashMap<Integer, HashMap<String, HashMap<Long, Double>>> userPrefList = new HashMap<>();
            for (UserItem userItem : userItems) {
                HashMap<String, HashMap<Long, Double>> map = JsonKit.jsonPrefListtoMap(userItem.getPref_list());

                for (Map.Entry<String, HashMap<Long, Double>> entry : map.entrySet()) {
                    String theme = entry.getKey();
                    HashMap<Long, Double> themeMap = entry.getValue();
                    // update each word in a theme
                    if ((themeMap != null) && !themeMap.isEmpty()) {
                        for (Map.Entry<Long, Double> wordValue : themeMap.entrySet()) {
                            Long word = wordValue.getKey();
                            double tfidfValue = wordValue.getValue() * DEC_COEE;
                            if (tfidfValue < 0.01) {
                                keywordToDelete.add(word);
                            }
                            // update in CustomizedHashMap<String, Double>
                            themeMap.put(word, tfidfValue);
                        }
                    }
                    // delete word
                    for (Long removeWord : keywordToDelete) {
                        themeMap.remove(removeWord);
                    }
                    keywordToDelete.clear();
                    // update in (theme-> (wordid, tfidf))
                    map.put(theme, themeMap);
                    // add to userPrefList
                    userPrefList.put(userItem.getId(), map);
                }
            }

            // format user preference list
            List<UserItem> userItemList = new ArrayList<>();
            for (Map.Entry<Integer, HashMap<String, HashMap<Long, Double>>> entry : userPrefList.entrySet()) {
                UserItem userItem = new UserItem();
                userItem.setId(entry.getKey());
                userItem.setPref_list(JsonKit.jsonPrefToString(entry.getValue()));
                userItemList.add(userItem);
            }
            // update database
            if (userItemList.size() != 0) {
                contentBasedDao.updateUserPrefList(userItemList);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 提取出当天有浏览行为的用户及其各自所浏览过的新闻id列表
     *
     * @return
     */
    public BrowsedHistory getBrowsedHistoryMap() {
        HashMap<Integer, ArrayList<Long>> userBrowsedMap = new HashMap<>();
        Set<Long> newsIndexList = new HashSet<>();
        try {
            List<RecommendOneNews> newslogsList = contentBasedDao.queryNewsByTime(RecommendKit.getInRecDate(-2));
            for (RecommendOneNews newslogs : newslogsList) {
                newsIndexList.add(newslogs.getNews_id());
                if (userBrowsedMap.containsKey(newslogs.getUser_id())) {
                    userBrowsedMap.get(newslogs.getUser_id()).add(newslogs.getNews_id());
                } else {
                    userBrowsedMap.put(newslogs.getUser_id(), new ArrayList<Long>());
                    userBrowsedMap.get(newslogs.getUser_id()).add(newslogs.getNews_id());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        BrowsedHistory res = new BrowsedHistory();
        res.setUserNews(userBrowsedMap);
        res.setNewsSet(newsIndexList);
        return res;
    }


    /**
     * 获得浏览过的新闻的集合
     *
     * @return
     */
    public HashSet<Long> getBrowsedNewsSet() {
        HashMap<Integer, ArrayList<Long>> browsedMap = getBrowsedHistoryMap().getUserNews();
        HashSet<Long> newsIdSet = new HashSet<>();
        for (Map.Entry<Integer, ArrayList<Long>> entry : browsedMap.entrySet()) {
            newsIdSet.addAll(entry.getValue());
        }
        return newsIdSet;
    }


    /**
     * Get theme of each newsid
     *
     * @param newsid
     * @return
     */
    public HashMap<Long, String> getThemeTFIDFMap(List<Long> newsid) {
        HashMap<Long, String> themeTFIDFMap = new HashMap<>();
        if (newsid == null || newsid.size() == 0) {
            return themeTFIDFMap;
        }
        List<NewsItem> news = contentBasedDao.queryNewsByNewId(newsid);
        for (NewsItem newItem : news) {
            themeTFIDFMap.put(newItem.getId(), newItem.getTheme());
        }
        return themeTFIDFMap;
    }
}
