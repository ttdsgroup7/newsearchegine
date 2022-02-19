package top.caohongchuan.newsrecommand.algorithms;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import top.caohongchuan.commonutil.datatypes.RecommendationItem;
import top.caohongchuan.commonutil.datatypes.UserItem;
import top.caohongchuan.newsrecommand.dao.ContentBasedDao;
import top.caohongchuan.newsrecommand.dao.RecommendationsDao;
import top.caohongchuan.newsrecommand.dao.UserDao;

import java.sql.Timestamp;
import java.util.*;

/**
 * @author qianxinyao
 * @email tomqianmaple@gmail.com
 * @github https://github.com/bluemapleman
 * @date 2016年11月21日 提供推荐算法通用的一些方法
 */

@Slf4j
@Service
@PropertySource("classpath:application.yml")
public class RecommendKit {

    @Value("${newsrecommend.activeDays}")
    int activeDay;
    @Value("${newsrecommend.beforeDays}")
    int beforeDays;

    @Autowired
    RecommendationsDao recommendationsDao;
    @Autowired
    ContentBasedDao contentBasedDao;
    @Autowired
    UserDao userDao;


    /**
     * @return the inRecDate 返回时效时间的"year-month-day"的格式表示，方便数据库的查询
     */
    public static Timestamp getInRecDate(int beforeDays) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, beforeDays);
        return new Timestamp(calendar.getTime().getTime());
    }

    /**
     * 过滤方法filterOutDateNews() 过滤掉失去时效性的新闻（由beforeDays属性控制）
     */
//    public static void filterOutDateNews(Collection<Long> col, Long userId) {
//        try {
//            String newsids = getInQueryString(col.iterator());
//            if (!newsids.equals("()")) {
//                List<News> newsList = News.dao.find("select id,news_time from news where id in " + newsids);
//                for (News news : newsList) {
//                    if (news.getNewsTime().before(getInRecTimestamp(beforeDays))) {
//                        col.remove(news.getId());
//                    }
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    /**
     * 过滤方法filterBrowsedNews() 过滤掉已经用户已经看过的新闻
     */
    public void filterBrowsedNews(Set<Long> col, int userId) {
        try {
            List<Long> newslogsList = recommendationsDao.queryBrowsedNews(userId);
            col.removeAll(newslogsList);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 过滤方法filterReccedNews() 过滤掉已经推荐过的新闻（在recommend表中查找）
     */
    public void filterReccedNews(Set<Long> col, Integer userId) {
        try {
            //但凡近期已经给用户推荐过的新闻，都过滤掉
            List<Long> recommendationList = recommendationsDao.queryRecUsedNews(userId, getInRecDate(0));
            col.removeAll(recommendationList);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 获取所有用户的Id列表
     *
     * @return
     */
    public List<Integer> getUserList() {
        List<Integer> users = contentBasedDao.queryAllUserId();
        return users;
    }

    /**
     * Acquire a list of active users
     * "Active" means who read news recently ('recent' determined by method getInRecDate(), default in a month)
     *
     * @return
     */
    public List<Integer> getActiveUsers() {
        try {
            List<Integer> userIDList = userDao.queryUserIdByTime(getInRecDate(activeDay));
            return userIDList;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }


    public int getbeforeDays() {
        return beforeDays;
    }

    public void setbeforeDays(int beforeDays) {
        this.beforeDays = beforeDays;
    }


    /**
     * Obtain user preference list
     *
     * @return (userid, ( theme, ( word, tfidf)))
     */
    public HashMap<Integer, HashMap<String, HashMap<Long, Double>>> getUserPrefListMap(List<Integer> userList) {
        HashMap<Integer, HashMap<String, HashMap<Long, Double>>> userPrefListMap = null;
        try {
            List<UserItem> userItems = contentBasedDao.queryUserPref(userList);
            userPrefListMap = new HashMap<>();
            for (UserItem userItem : userItems) {
                userPrefListMap.put(userItem.getId(), JsonKit.jsonPrefListtoMap(userItem.getPref_list()));
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return userPrefListMap;
    }

    /**
     * 用以select语句中使用in (n1，n2,n3...)范围查询的字符串拼接
     *
     * @param ite 待查询对象集合的迭代器
     * @return 若迭代集合不为空:"(n1,n2,n3)"，若为空："()"
     */
    public static <T> String getInQueryString(Iterator<T> ite) {
        String inQuery = "(";
        while (ite.hasNext()) {
            inQuery += ite.next() + ",";
        }
        if (inQuery.length() > 1) {
            inQuery = inQuery.substring(0, inQuery.length() - 1);
        }
        inQuery += ")";
        return inQuery;
    }

    public static <T> String getInQueryStringWithSingleQuote(Iterator<T> ite) {
        String inQuery = "(";
        while (ite.hasNext()) {
            inQuery += "'" + ite.next() + "',";
        }
        if (inQuery.length() > 1) {
            inQuery = inQuery.substring(0, inQuery.length() - 1);
        }
        inQuery += ")";
        return inQuery;
    }

    /**
     * Add recommend result into recommend table
     *
     * @param reList the recommend result list
     */
    public void insertRecommend(List<RecommendationItem> reList) {
        if (reList == null || reList.size() == 0) {
            return;
        }
        try {
            recommendationsDao.insertRecommend(reList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 去除数量上超过为算法设置的推荐结果上限值的推荐结果
     *
     * @param set
     * @param N
     * @return
     */
    public static void removeOverNews(Set<Long> set, int N) {
        int i = 0;
        Iterator<Long> ite = set.iterator();
        while (ite.hasNext()) {
            if (i >= N) {
                ite.remove();
                ite.next();
            } else {
                ite.next();
            }
            i++;
        }
    }
}
