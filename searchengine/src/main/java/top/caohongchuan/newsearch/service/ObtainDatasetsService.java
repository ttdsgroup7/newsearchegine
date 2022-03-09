package top.caohongchuan.newsearch.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.caohongchuan.commonutil.datatypes.NewsItem;
import top.caohongchuan.commonutil.returntypes.CouThem;
import top.caohongchuan.commonutil.returntypes.Country;
import top.caohongchuan.commonutil.returntypes.Theme;
import top.caohongchuan.newsearch.dao.RelatedDatasetsDao;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Some essential dataset for show in front-end
 *
 * @Author Hongchuan CAO
 */
@Service
public class ObtainDatasetsService {
    @Autowired
    RelatedDatasetsDao relatedDatasetsDao;

    public List<Country> obtainCountry() {
        List<Country> res = relatedDatasetsDao.obtainCountry();
        res.remove(null);
        res.remove("");
        return res;
    }

    public List<Theme> obtainTheme() {
        return relatedDatasetsDao.obtainTheme();
    }

    public List<String> obtainDownSug(String wordsug){
        List<String> res = relatedDatasetsDao.obtainWordSug(wordsug);
        return res;
    }

    public PageInfo<NewsItem> obtainNewsByCountry(String country, int page, int pageSize) {
        PageHelper.startPage(page, pageSize);
        List<NewsItem> news = relatedDatasetsDao.obtainNewsByCountry(country);
        return new PageInfo<>(news);
    }

    public PageInfo<NewsItem> obtainNewsByTheme(String theme, int page, int pageSize) {
        PageHelper.startPage(page, pageSize);
        List<NewsItem> news = relatedDatasetsDao.obtainNewsByTheme(theme);
        return new PageInfo<>(news);
    }

    public PageInfo<NewsItem> obtainNewsByTime(Timestamp start, Timestamp end, int page, int pageSize) {
        PageHelper.startPage(page, pageSize);
        List<NewsItem> news = relatedDatasetsDao.obtainNewsByTime(start, end);
        return new PageInfo<>(news);
    }

    public Map<String, Map<String, Integer>> obtainThemeCountry() {
        List<CouThem> couThemList = relatedDatasetsDao.obtainThemCoun();
        Map<String, Map<String, Integer>> res = new HashMap<>();
        for (CouThem couThem : couThemList) {
            String country = couThem.getCountry();
            if (res.containsKey(country)) {
                Map<String, Integer> couItem = res.get(country);
                couItem.put(couThem.getTheme(), couThem.getNumber());
            } else {
                Map<String, Integer> couItem = new HashMap<>();
                couItem.put(couThem.getTheme(), couThem.getNumber());
                res.put(country, couItem);
            }
        }
        return res;
    }
}
