package top.caohongchuan.newsrecommand.UserBasedCollaborativeRecommender;

import lombok.extern.slf4j.Slf4j;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.FastByIDMap;
import org.apache.mahout.cf.taste.impl.model.GenericDataModel;
import org.apache.mahout.cf.taste.impl.model.GenericUserPreferenceArray;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.LogLikelihoodSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.PreferenceArray;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import top.caohongchuan.commonutil.datatypes.NewsLogsItem;
import top.caohongchuan.commonutil.datatypes.RecommendationItem;
import top.caohongchuan.newsrecommand.algorithms.RecommendAlgorithm;
import top.caohongchuan.newsrecommand.algorithms.RecommendKit;
import top.caohongchuan.newsrecommand.dao.RecommendationsDao;
import top.caohongchuan.newsrecommand.dao.UserBasedCRDao;

import java.util.*;

/**
 * User-Based Collaborative Recommendation
 *
 * @author Hongchuan CAO
 * @version 1.0
 */


@Slf4j
@Service
@PropertySource("classpath:application.yml")
public class MahoutUserBasedCollaborativeRecommender implements RecommendAlgorithm {

    /**
     * 对应计算相似度时的时效天数
     */
    @Value("${newsrecommend.CFValidDays}")
    int CFValidDays;
    /**
     * 给每个用户推荐的新闻的条数
     */
    @Value("${newsrecommend.CFRecNum}")
    int CFRecNum;

    @Autowired
    UserBasedCRDao userBasedCRDao;
    @Autowired
    RecommendationsDao recommendationsDao;

    /**
     * Process recommend for a group of users
     *
     * @param users username list
     */
    @Override
    public void recommend(List<Integer> users) {
        log.info("CF start at " + new Date());
        if (users.isEmpty()) {
            return;
        }

        try {

            // obtain the history (user_id => news_id with prefer_degree)
            List<NewsLogsItem> newslogList = userBasedCRDao.queryNewsLogsByUserId(users, RecommendKit.getInRecDate(CFValidDays));
            // Map<userId, Map<newsId, score>>
            HashMap<Integer, HashMap<Long, Double>> userToNewsPref = new HashMap<>();
            for (NewsLogsItem newsLogsItem : newslogList) {
                if (!userToNewsPref.containsKey(newsLogsItem.getUser_id())) {
                    userToNewsPref.put(newsLogsItem.getUser_id(), new HashMap<>());
                }
                userToNewsPref.get(newsLogsItem.getUser_id()).put(newsLogsItem.getNews_id(), newsLogsItem.getPrefer_degree());
            }

            // add newslogList into preferences
            FastByIDMap<PreferenceArray> preferences = new FastByIDMap<>();
            int userIndex = 0;
            for (Map.Entry<Integer, HashMap<Long, Double>> entry : userToNewsPref.entrySet()) {
                HashMap<Long, Double> prefValue = entry.getValue();
                PreferenceArray prefsForUser = new GenericUserPreferenceArray(prefValue.size());
                prefsForUser.setUserID(0, (long) entry.getKey());
                int itemIndex = 0;
                for (Map.Entry<Long, Double> pValue : prefValue.entrySet()) {
                    prefsForUser.setItemID(itemIndex, pValue.getKey());
                    prefsForUser.setValue(itemIndex, pValue.getValue().floatValue());
                    itemIndex++;
                }
                preferences.put(entry.getKey(), prefsForUser);
                userIndex++;
            }

            // apply into model
            DataModel dataModel = new GenericDataModel(preferences);
            UserSimilarity similarity = new LogLikelihoodSimilarity(dataModel);
            // NearestNeighborhood的数量有待考察
            UserNeighborhood neighborhood = new NearestNUserNeighborhood(3, similarity, dataModel);
            Recommender recommender = new GenericUserBasedRecommender(dataModel, neighborhood, similarity);

            // add recommend news in reList for each user
            ArrayList<RecommendationItem> reList = new ArrayList<>();
            for (Integer user : users) {
                Integer userid = user;
                List<RecommendedItem> recItems = recommender.recommend(userid, CFRecNum);

                Set<Long> hs = new HashSet<>();
                for (RecommendedItem recItem : recItems) {
                    hs.add(recItem.getItemID());
                }

                // 过滤掉已推荐新闻和已过期新闻
//                RecommendKit.filterOutDateNews(hs, userid);
//                recommendKit.filterReccedNews(hs, userid);

                // 无可推荐新闻
                if (hs.size() > CFRecNum) {
                    RecommendKit.removeOverNews(hs, CFRecNum);
                }

                for (long newsid : hs) {
                    RecommendationItem re = new RecommendationItem();
                    re.setUser_id(userid);
                    re.setNews_id(newsid);
                    re.setDerive_algorithm(RecommendAlgorithm.CF);
                    reList.add(re);
                }
            }
            // update recommend list in database
            if(!reList.isEmpty()){
                recommendationsDao.insertRecommend(reList);
            }
        } catch (TasteException e) {
            log.error("CB算法构造偏好对象失败！");
            e.printStackTrace();
        } catch (Exception e) {
            log.error("CB算法数据库操作失败！");
            e.printStackTrace();
        }

        log.info("CF finish at " + new Date());
    }

    public int getRecNums() {
        return CFRecNum;
    }

    public void setRecNums(int recNums) {
        CFRecNum = recNums;
    }
}
