package top.caohongchuan.newsearch.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.caohongchuan.commonutil.returntypes.CouThem;
import top.caohongchuan.commonutil.returntypes.Country;
import top.caohongchuan.commonutil.returntypes.Theme;
import top.caohongchuan.newsearch.dao.RelatedDatasetsDao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Some essential dataset for show in front-end
 * @Author Hongchuan CAO
 */
@Service
public class ObtainDatasetsService {
    @Autowired
    RelatedDatasetsDao relatedDatasetsDao;

    public List<Country> obtainCountry(){
        return relatedDatasetsDao.obtainCountry();
    }

    public List<Theme> obtainTheme(){
        return relatedDatasetsDao.obtainTheme();
    }

    public Map<String, Map<String, Integer>> obtainThemeCountry(){
        List<CouThem> couThemList = relatedDatasetsDao.obtainThemCoun();
        Map<String, Map<String, Integer>> res = new HashMap<>();
        for(CouThem couThem : couThemList){
            String country = couThem.getCountry();
            if(res.containsKey(country)){
                Map<String, Integer> couItem = res.get(country);
                couItem.put(couThem.getTheme(), couThem.getNumber());
            }else{
                Map<String, Integer> couItem = new HashMap<>();
                couItem.put(couThem.getTheme(), couThem.getNumber());
                res.put(country, couItem);
            }
        }
        return res;
    }
}
