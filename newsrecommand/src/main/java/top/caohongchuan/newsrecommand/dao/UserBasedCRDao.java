package top.caohongchuan.newsrecommand.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import top.caohongchuan.commonutil.datatypes.NewsLogsItem;

import java.sql.Timestamp;
import java.util.List;

@Mapper
public interface UserBasedCRDao {

    @Select({"<script>" +
            "select user_id,news_id,prefer_degree from newslogs " +
            "<where> " +
            "user_id in " +
            "<foreach item='item' index='index' collection='useridList' open='(' separator=',' close=')'> " +
            "#{item}" +
            "</foreach>" +
            " and view_time>#{timestamp}" +
            "</where>" +
            "</script>"})
    List<NewsLogsItem> queryNewsLogsByUserId(@Param("useridList") List<Integer> useridList, @Param("timestamp") Timestamp timestamp);
}
