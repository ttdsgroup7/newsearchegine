package top.caohongchuan.newsrecommand.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import top.caohongchuan.commonutil.datatypes.NewsItem;
import top.caohongchuan.commonutil.datatypes.RecommendOneNews;
import top.caohongchuan.commonutil.datatypes.UserItem;

import java.sql.Timestamp;
import java.util.List;

@Mapper
public interface ContentBasedDao {

    @Select("select id from users")
    List<Integer> queryAllUserId();

    @Select("select user_id,news_id from newslogs where view_time>#{timestamp}")
    List<RecommendOneNews> queryNewsByTime(@Param("timestamp") Timestamp timestamp);

    @Select("select id from news where publish_date>#{timestamp}")
    List<Long> queryNewsIdByTime(@Param("timestamp") Timestamp timestamp);

    @Select({
            "<script> ",
            "select id,pref_list from users ",
            "<where> ",
            "id in ",
            "<foreach item='item' index='index' collection='userList' open='(' separator=',' close=')'> ",
            "#{item}",
            "</foreach>",
            "</where>",
            "</script>"
    })
    List<UserItem> queryUserPref(@Param("userList") List<Integer> userList);

    @Select({
            "<script>" +
            "select id,theme from news" +
            "<where>" +
            "id in " +
            "<foreach item='item' index='index' collection='newsid' open='(' separator=',' close=')'> ",
            "#{item}" +
            "</foreach>" +
            "</where>" +
            "</script>"
    })
    List<NewsItem> queryNewsByNewId(@Param("newsid") List<Long> newsid);

    @Update({"<script>" +
            "update users set pref_list=case" +
            "<foreach item='item' collection='userprefs' index='index' open='' close='' separator=' '> " +
            "when id=#{item.id} then #{item.pref_list}" +
            "</foreach>" +
            "end" +
            "<where>" +
            "id in " +
            "<foreach item='item' index='index' collection='userprefs' open='(' separator=',' close=')'> " +
            "#{item.id}" +
            "</foreach>" +
            "</where>" +
            "</script>"})
    void updateUserPrefList(@Param("userprefs") List<UserItem> userpref);

}
