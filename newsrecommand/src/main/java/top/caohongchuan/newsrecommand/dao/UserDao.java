package top.caohongchuan.newsrecommand.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

/**
 * query related data from users table
 * @author Hongchuan CAO
 */
@Mapper
public interface UserDao {

    @Select("select id from users where latest_log_time>#{date_specify}")
    List<Integer> queryUserIdByTime(@Param("date_specify") Timestamp date_specify);

}
