package top.caohongchuan.newsearch.dao;


import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.sql.Timestamp;

@Mapper
public interface UsersDao {

    @Select("select id from users where username=#{username}")
    Integer queryUserIdByUserName(@Param("username") String username);

    @Insert("insert into users (username, password, pref_list) value (#{username}, #{password}, '')")
    void insertUser(@Param("username") String username, @Param("password") String password);

    @Select("select password from users where username=#{username}")
    String queryUserPassWordByUserName(@Param("username") String username);
}
