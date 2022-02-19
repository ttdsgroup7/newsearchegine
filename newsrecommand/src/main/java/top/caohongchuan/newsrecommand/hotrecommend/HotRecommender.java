package top.caohongchuan.newsrecommand.hotrecommend;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import top.caohongchuan.commonutil.ExceptionTypes.BizException;
import top.caohongchuan.commonutil.datatypes.NewsFrequency;
import top.caohongchuan.commonutil.datatypes.RecommendationItem;
import top.caohongchuan.commonutil.datatypes.UserNewsNum;
import top.caohongchuan.newsrecommand.algorithms.RecommendAlgorithm;
import top.caohongchuan.newsrecommand.algorithms.RecommendKit;
import top.caohongchuan.newsrecommand.dao.RecommendationsDao;

import java.sql.Timestamp;
import java.util.*;

/**
 * @author qianxinyao
 * @email tomqianmaple@gmail.com
 * @github https://github.com/bluemapleman
 */
@Slf4j
@Service
@PropertySource("classpath:application.yml")
public class HotRecommender implements RecommendAlgorithm {

    @Autowired
    RecommendationsDao recommendationsDao;
    @Autowired
    RecommendKit recommendKit;


    @Value("${newsrecommend.beforeDays}")
    int beforeDays;
    @Value("${newsrecommend.TOTAL_REC_NUM}")
    int TOTAL_REC_NUM;
    @Value("${newsrecommend.HotNewsLimit}")
    int HOT_NEWS_LIMIT;

    private ArrayList<Long> topHotNewsList = new ArrayList<>();

    /**
     * obtain hotnews from newslogs table
     */
    public void formTodayTopHotNewsList() {
        topHotNewsList.clear();
        ArrayList<Long> hotNewsTobeReccommended = new ArrayList<>();
        try {
            List<NewsFrequency> newslogsList = recommendationsDao.queryhotNewsList(this.HOT_NEWS_LIMIT, RecommendKit.getInRecDate(this.beforeDays));
            for (NewsFrequency newslog : newslogsList) {
                hotNewsTobeReccommended.add(newslog.getNews_id());
            }
            topHotNewsList = hotNewsTobeReccommended;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * update recommendation from userId list
     *
     * @param users UserId list
     */
    @Override
    public void recommend(List<Integer> users) {
        log.info("HR start at " + new Date());
        this.formTodayTopHotNewsList();
        Timestamp timestamp = RecommendKit.getInRecDate(beforeDays); //getCertainTimestamp(0, 0, 0);

        try {
            //获得已经预备为当前用户推荐的新闻若数目不足达不到单次的最低推荐数目要求，则用热点新闻补充
            List<UserNewsNum> tmpRecNumsList = recommendationsDao.queryResNumForUser(users, timestamp);
            ArrayList<RecommendationItem> reList = new ArrayList<>();

            for (UserNewsNum usersNews : tmpRecNumsList) {
                int userId = usersNews.getUser_id();
                int tmpRecNums = usersNews.getRecnums();
                // calculate the number of news that should be added (delta)
                int delta = Math.max(0, TOTAL_REC_NUM - tmpRecNums);

                // obtain the hot news
                Set<Long> toBeRecommended = new HashSet<>();
                if (delta > 0) {
                    int j = Math.min(topHotNewsList.size(), delta);
                    while (j-- > 0) {
                        toBeRecommended.add(topHotNewsList.get(j));
                    }
                }
                // filter news
                recommendKit.filterBrowsedNews(toBeRecommended, userId);
                recommendKit.filterReccedNews(toBeRecommended, userId);

                for (Long newsid : toBeRecommended) {
                    RecommendationItem re = new RecommendationItem();
                    re.setUser_id(userId);
                    re.setNews_id(newsid);
                    re.setDerive_algorithm(RecommendAlgorithm.HR);
                    reList.add(re);
                }
            }
            //insert and update recommendation table
            recommendationsDao.insertRecommend(reList);
        } catch (Exception e) {
            throw new BizException();
        }
        log.info("HR end at " + new Date());
    }

    public List<Long> getTopHotNewsList() {
        return topHotNewsList;
    }

    public int getTopHopNewsListSize() {
        return topHotNewsList.size();
    }

    private Timestamp getCertainTimestamp(int hour, int minute, int second) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, second);
        return new Timestamp(calendar.getTime().getTime());
    }
}
