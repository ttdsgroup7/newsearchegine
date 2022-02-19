package top.caohongchuan.newsearch.service;

import org.apache.commons.net.ntp.TimeStamp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.caohongchuan.commonutil.ExceptionTypes.UserNameExitedException;
import top.caohongchuan.commonutil.datatypes.UserItem;
import top.caohongchuan.newsearch.dao.UsersDao;

import java.sql.Timestamp;

@Service
public class UserLoginService {

    @Autowired
    UsersDao usersDao;

    public void userRegister(String username, String password) {
        Integer userId = usersDao.queryUserIdByUserName(username);
        if (userId == null) {
            usersDao.insertUser(username, password);
        }else{
            throw new UserNameExitedException();
        }
    }
}
