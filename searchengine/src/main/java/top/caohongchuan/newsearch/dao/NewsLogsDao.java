package top.caohongchuan.newsearch.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import top.caohongchuan.commonutil.datatypes.NewsLogsItem;

import java.util.List;

@Mapper
public interface NewsLogsDao {

    @Insert("<script> insert into newslogs(user_id, news_id, view_time, prefer_degree)" +
            "values " +
            "<foreach item='item' index='index' collection='newsLogsItemList' separator=','> " +
            "(#{item.user_id}, #{item.news_id}, #{item.view_time}, #{item.prefer_degree})" +
            "</foreach>" +
            "</script>")
    void updateRecord(@Param("newsLogsItemList") List<NewsLogsItem> newsLogsItemList);
}
