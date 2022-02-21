package top.caohongchuan.newsearch.service;

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

    public ResponseNewsResult dealquery(String querystr, boolean extension){
        // deal with wrong word
        String rightQueryStr = spellCorrectionService.checkQueryString(querystr);
        // obtain post expression
        ArrayList<String> postexpr = parseQuery.getPostExpr(rightQueryStr);
        // obtain docid list
        PostExprResult postExprResult = searchService.docIdFromPostExpr(postexpr);
        ArrayList<String> docids = postExprResult.getNewsresponse();
        ArrayList<String> wordIndex = postExprResult.getWordsIndex();

        // obtain top 100
        List<String> docidsLimited = docids.subList(0, Math.min(100, docids.size()));
        List<String> docidsExtension = docidsLimited;
        // use query extension to query again
        if(extension){
            docidsExtension = queryExtension.extension(docidsLimited, wordIndex);
        }
        ResponseNewsResult responseNewsResult = new ResponseNewsResult();
        if (docidsExtension.isEmpty()) {
            responseNewsResult.setNewsarray(new ArrayList<>());
        }else{
            List<NewsItem> news = newsRetrieve.getNews(docidsExtension);
            List<NewsItem> newsSorted = new ArrayList<>();
            for(String docid : docidsExtension){
                for(NewsItem newsItem : news){
                    if(Long.parseLong(docid) == newsItem.getId()){
                        newsSorted.add(newsItem);
                        break;
                    }
                }
            }
            responseNewsResult.setNewsarray(newsSorted);
        }
        // if error, return the right query string
        if(!rightQueryStr.equals(querystr)){
            responseNewsResult.setRightQueryString(rightQueryStr);
        }
        return responseNewsResult;
    }


}
