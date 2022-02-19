package top.caohongchuan.newsearch.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import top.caohongchuan.commonutil.datatypes.NewsFrequency;
import top.caohongchuan.commonutil.datatypes.RecommendationItem;
import top.caohongchuan.commonutil.datatypes.UserNewsNum;

import java.sql.Timestamp;
import java.util.List;

@Mapper
public interface RecommendationsDao {

    @Select("select news_id from recommendations where user_id=#{userId} and derive_time>#{timestamp}")
    List<String> queryRecUsedNews(@Param("userId") int userId, @Param("timestamp") Timestamp timestamp);
}
