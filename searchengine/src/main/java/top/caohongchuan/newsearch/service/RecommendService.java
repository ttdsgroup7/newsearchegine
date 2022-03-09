package top.caohongchuan.newsearch.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.caohongchuan.commonutil.ExceptionTypes.BizException;
import top.caohongchuan.commonutil.datatypes.NewsItem;
import top.caohongchuan.commonutil.datatypes.ResponseNewsResult;
import top.caohongchuan.commonutil.receiveType.UserInfo;
import top.caohongchuan.commonutil.returntypes.ResultCode;
import top.caohongchuan.newsearch.dao.NewsRetrieve;
import top.caohongchuan.newsearch.dao.RecommendationsDao;
import top.caohongchuan.newsearch.dao.UsersDao;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Return recommend news for each user
 * @auther Hongchuan CAO
 * @version 1.0
 */
@Service
public class RecommendService {

    @Autowired
    UsersDao usersDao;
    @Autowired
    RecommendationsDao recommendationsDao;
    @Autowired
    NewsRetrieve newsRetrieve;

    public ResponseNewsResult getRecommendNews(UserInfo userInfo){

        // check username and password
        String username = userInfo.getUsername();
        String password = userInfo.getPassword();
        String truePassword = usersDao.queryUserPassWordByUserName(username);
        if (!password.equals(truePassword)) {
            throw new BizException(ResultCode.NOPERMISSION.getResultCode(), "Wrong Password");
        }
        // obtain user id according to username
        int userId = usersDao.queryUserIdByUserName(username);
        // obtain recommend news from recommendations table
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -2);
        Timestamp preDate = new Timestamp(calendar.getTime().getTime());

        List<String> docsIdFromUBR = recommendationsDao.queryRecUsedNews(userId, preDate);
        // obtain news info from news table
        List<NewsItem> recommendNews=new ArrayList<>();

        PageHelper.startPage(1, 200);
        if(docsIdFromUBR != null && docsIdFromUBR.size() != 0){
            recommendNews = newsRetrieve.getNews(docsIdFromUBR);
        }
        ResponseNewsResult responseNewsResult = new ResponseNewsResult();
        responseNewsResult.setUserId(userId);
        PageInfo<NewsItem> info = new PageInfo<>(recommendNews);
        responseNewsResult.setNewsarray(info);
        return responseNewsResult;
    }
}
