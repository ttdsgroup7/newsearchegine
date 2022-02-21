package top.caohongchuan.newsrecommand.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.caohongchuan.newsrecommand.UserBasedCollaborativeRecommender.MahoutUserBasedCollaborativeRecommender;
import top.caohongchuan.newsrecommand.algorithms.RecommendKit;
import top.caohongchuan.newsrecommand.contentbasedrecommend.ContentBasedRecommender;
import top.caohongchuan.newsrecommand.dao.ContentBasedDao;
import top.caohongchuan.newsrecommand.dao.RecommendationsDao;
import top.caohongchuan.newsrecommand.hotrecommend.HotRecommender;

import java.util.Date;
import java.util.List;

/**
 * Recommend service
 *
 * @author Hongchuan CAO
 * @version 1.0
 */

@Slf4j
@Service
public class JobService {

    @Autowired
    HotRecommender hotRecommender;
    @Autowired
    ContentBasedRecommender contentBasedRecommender;
    @Autowired
    MahoutUserBasedCollaborativeRecommender mahoutUserBasedCollaborativeRecommender;

    @Autowired
    ContentBasedDao contentBasedDao;
    @Autowired
    RecommendKit recommendKit;
    @Autowired
    RecommendationsDao recommendationsDao;

    boolean enableCF, enableCB, enableHR;

    /**
     * @param enableCF Collaborative Filtering
     * @param enableCB Content-Based Recommendation
     * @param enableHR Hot News Recommendation
     */
    public void JobSetter(boolean enableCF, boolean enableCB, boolean enableHR) {
        this.enableCF = enableCF;
        this.enableCB = enableCB;
        this.enableHR = enableHR;
    }

    /**
     * 执行一次新闻推荐
     * 参数forActiveUsers表示是否只针对活跃用户进行新闻推荐，true为是，false为否。
     *
     * @param
     */
    private void executeInstantJob(List<Integer> userID) {
        //让热点新闻推荐器预先生成今日的热点新闻
        hotRecommender.formTodayTopHotNewsList();

        if (enableCF) {
            mahoutUserBasedCollaborativeRecommender.recommend(userID);
        }
        if (enableCB) {
            contentBasedRecommender.recommend(userID);
        }
        if (enableHR) {
            hotRecommender.recommend(userID);
        }

        log.info("Recommand Finishes at" + new Date());
    }

    /**
     * 执行一次新闻推荐
     * 参数forActiveUsers表示是否只针对活跃用户进行新闻推荐，true为是，false为否。
     *
     * @param
     */
    public void executeInstantJobForCertainUsers(List<Integer> goalUser) {
        executeInstantJob(goalUser);
    }

    /**
     * 为所用有用户执行一次新闻推荐
     */
    public void executeInstantJobForAllUsers() {
        executeInstantJob(contentBasedDao.queryAllUserId());
    }

    public void deleteExpiryRecommendation() {
        recommendationsDao.deleteExpiry(RecommendKit.getInRecDate(-1));
    }

    /**
     * 为活跃用户进行一次推荐。
     *
     * @param
     */
    public void executeInstantJobForActiveUsers() {
        executeInstantJob(recommendKit.getActiveUsers());
    }
}


