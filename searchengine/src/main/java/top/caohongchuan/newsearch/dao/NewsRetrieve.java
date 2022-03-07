package top.caohongchuan.newsearch.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.data.repository.query.Param;
import top.caohongchuan.commonutil.datatypes.NewsItem;

import java.util.List;

@Mapper
public interface NewsRetrieve {

    @Select({
            "<script> "+
            "select id, publish_date, head_line, news_abstract, country, image, theme, url from news"+
            "<where> "+
            "id in"+
            "<foreach item='item' index='index' collection='docids' open='(' separator=',' close=')'> "+
            "#{item} "+
            "</foreach>"+
            "</where>" +
            "order by field (id, " +
            "<foreach item='item' index='index' collection='docids' open='' separator=',' close=')'> " +
            "#{item} " +
            "</foreach> " +
            "</script>"
    })
    List<NewsItem> getNews(@Param("docids") List<String> docids);
}
