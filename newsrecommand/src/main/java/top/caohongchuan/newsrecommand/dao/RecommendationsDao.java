package top.caohongchuan.newsrecommand.dao;

import org.apache.ibatis.annotations.*;
import top.caohongchuan.commonutil.datatypes.NewsFrequency;
import top.caohongchuan.commonutil.datatypes.RecommendationItem;
import top.caohongchuan.commonutil.datatypes.UserNewsNum;

import java.sql.Timestamp;
import java.util.List;

@Mapper
public interface RecommendationsDao {

    @Select("<script> select user_id, count(*) as recnums from recommendations" +
            "<where> derive_time>#{timestamp} and user_id in " +
            "<foreach item='item' index='index' collection='userList' open='(' separator=',' close=')'> " +
            "#{item} " +
            "</foreach>" +
            "</where>" +
            "group by user_id" +
            "</script>")
    List<UserNewsNum> queryResNumForUser(@Param("userList") List<Integer> userList, @Param("timestamp") Timestamp timestamp);

    @Select("select news_id from recommendations where user_id=#{userId} and derive_time>#{timestamp}")
    List<Long> queryRecUsedNews(@Param("userId") int userId, @Param("timestamp") Timestamp timestamp);

    @Select("select news_id from newslogs where user_id=#{userId}")
    List<Long> queryBrowsedNews(@Param("userId") int user_id);

    @Insert({"<script> " +
            "insert into recommendations (user_id,news_id,derive_algorithm) " +
            "values " +
            "<foreach item='item' index='index' collection='reList' open='' separator=',' close=''> " +
            "(#{item.user_id},#{item.news_id},#{item.derive_algorithm})" +
            "</foreach>" +
            "</script>"})
    void insertRecommend(@Param("reList") List<RecommendationItem> reList);

    @Select("select news_id,count(*) as visitNums from newslogs where view_time>#{timestamp} group by news_id order by visitNums,news_id desc limit 0,#{limitNum}")
    List<NewsFrequency> queryhotNewsList(@Param("limitNum") int limitNum, @Param("timestamp") Timestamp timestamp);

    @Delete("delete from recommendations where derive_time<#{timestamp}")
    void deleteExpiry(@Param("timestamp") Timestamp timestamp);
}