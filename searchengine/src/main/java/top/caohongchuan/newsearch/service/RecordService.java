package top.caohongchuan.newsearch.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.caohongchuan.commonutil.datatypes.NewsLogsItem;
import top.caohongchuan.commonutil.receiveType.UpdateRecord;
import top.caohongchuan.newsearch.dao.NewsLogsDao;
import top.caohongchuan.newsearch.dao.UsersDao;

import java.util.List;

@Service
public class RecordService {

    @Autowired
    UsersDao usersDao;
    @Autowired
    NewsLogsDao newsLogsDao;

    public void updateRecord(UpdateRecord newsLogsItemList) {
        int userId = usersDao.queryUserIdByUserName(newsLogsItemList.getUsername());
        List<NewsLogsItem> newsLogs = newsLogsItemList.getNewsLogsItemList();
        for (int i = 0; i < newsLogs.size(); i++) {
            newsLogs.get(i).setUser_id(userId);
        }
        newsLogsDao.updateRecord(newsLogs);
    }
}
