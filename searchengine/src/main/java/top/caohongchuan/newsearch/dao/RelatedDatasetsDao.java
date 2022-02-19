package top.caohongchuan.newsearch.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import top.caohongchuan.commonutil.returntypes.CouThem;
import top.caohongchuan.commonutil.returntypes.Country;
import top.caohongchuan.commonutil.returntypes.Theme;

import java.util.List;

@Mapper
public interface RelatedDatasetsDao {

    @Select("select country, count(country) as number from news group by country")
    List<Country> obtainCountry();

    @Select("select theme, count(theme) as number from news group by theme")
    List<Theme> obtainTheme();

    @Select("select theme, country, count(theme)as number from news group by theme, country")
    List<CouThem> obtainThemCoun();
}
