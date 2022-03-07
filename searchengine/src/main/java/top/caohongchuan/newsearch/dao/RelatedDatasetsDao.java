package top.caohongchuan.newsearch.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import top.caohongchuan.commonutil.datatypes.NewsItem;
import top.caohongchuan.commonutil.returntypes.CouThem;
import top.caohongchuan.commonutil.returntypes.Country;
import top.caohongchuan.commonutil.returntypes.Theme;

import java.sql.Timestamp;
import java.util.List;

@Mapper
public interface RelatedDatasetsDao {

    @Select("select country, count(country) as number from news group by country")
    List<Country> obtainCountry();

    @Select("select theme, count(theme) as number from news group by theme")
    List<Theme> obtainTheme();

    @Select("select theme, country, count(theme)as number from news group by theme, country")
    List<CouThem> obtainThemCoun();

    @Select("select id,publish_date,head_line,news_abstract,country,image,theme,url from news where country=#{country} order by publish_date desc")
    List<NewsItem> obtainNewsByCountry(@Param("country") String country);

    @Select("select id,publish_date,head_line,news_abstract,country,image,theme,url from news where theme=#{theme} order by publish_date desc")
    List<NewsItem> obtainNewsByTheme(@Param("theme") String theme);

    @Select("select id,publish_date,head_line,news_abstract,country,image,theme,url from news where publish_date>=#{starttime} and publish_date<=#{endtime} order by publish_date desc")
    List<NewsItem> obtainNewsByTime(@Param("starttime") Timestamp starttime, @Param("endtime") Timestamp endtime);

    @Select("select key_word from suggestion where key_word like CONCAT('%',#{wordsug},'%') order by number desc limit 10")
    List<String> obtainWordSug(@Param("wordsug") String wordSug);
}
