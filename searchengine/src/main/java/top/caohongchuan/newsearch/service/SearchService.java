package top.caohongchuan.newsearch.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.caohongchuan.commonutil.datatypes.NewsItem;
import top.caohongchuan.commonutil.datatypes.PostExprResult;
import top.caohongchuan.commonutil.datatypes.ResponseNewsResult;
import top.caohongchuan.newsearch.dao.NewsRetrieve;
import top.caohongchuan.newsearch.tools.ParseQuery;
import top.caohongchuan.newsearch.tools.SearchEngine;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class SearchService {

    @Autowired
    ParseQuery parseQuery;
    @Autowired
    SearchEngine searchService;
    @Autowired
    NewsRetrieve newsRetrieve;
    @Autowired
    SpellCorrectionService spellCorrectionService;
    @Autowired
    QueryExtension queryExtension;

    public ResponseNewsResult dealquery(String querystr, boolean extension, int page, int pageSize){

        // deal with wrong word
        String rightQueryStr = spellCorrectionService.checkQueryString(querystr);
        // obtain post expression
        ArrayList<String> postexpr = parseQuery.getPostExpr(rightQueryStr);
        // obtain docid list
        PostExprResult postExprResult = searchService.docIdFromPostExpr(postexpr);
        ArrayList<String> docids = postExprResult.getNewsresponse();
        ArrayList<String> wordIndex = postExprResult.getWordsIndex();

        // obtain top 100
        List<String> docidsLimited = docids.subList(0, Math.min(1000, docids.size()));
        List<String> docidsExtension = docidsLimited;
        // use query extension to query again
        if(extension){
            docidsExtension = queryExtension.extension(docidsLimited, wordIndex);
        }
        ResponseNewsResult responseNewsResult = new ResponseNewsResult();
        if (docidsExtension.isEmpty()) {
            responseNewsResult.setNewsarray(new PageInfo<>());
        }else{
            PageHelper.startPage(page, pageSize);
            List<NewsItem> news = newsRetrieve.getNews(docidsExtension);
            PageInfo<NewsItem> info = new PageInfo<>(news);
            responseNewsResult.setNewsarray(info);
        }
        // if error, return the right query string
        if(!rightQueryStr.toLowerCase(Locale.ROOT).equals(querystr.toLowerCase(Locale.ROOT))){
            responseNewsResult.setRightQueryString(rightQueryStr);
        }
        return responseNewsResult;
    }


}
