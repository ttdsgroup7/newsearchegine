package top.caohongchuan.newsearch.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.caohongchuan.commonutil.datatypes.ResponseNewsResult;
import top.caohongchuan.newsearch.dao.NewsRetrieve;
import top.caohongchuan.newsearch.tools.ParseQuery;
import top.caohongchuan.newsearch.tools.SearchEngine;

import java.util.ArrayList;

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

    public ResponseNewsResult dealquery(String querystr){
        // deal with wrong word
        String rightQueryStr = spellCorrectionService.checkQueryString(querystr);
        // obtain post expression
        ArrayList<String> postexpr = parseQuery.getPostExpr(rightQueryStr);
        // obtain docid list
        ArrayList<String> docids = searchService.docIdFromPostExpr(postexpr);
        ResponseNewsResult responseNewsResult = new ResponseNewsResult();
        if (docids.isEmpty()) {
            responseNewsResult.setNewsarray(new ArrayList<>());
        }else{
            responseNewsResult.setNewsarray(newsRetrieve.getNews(docids));
        }
        // if error, return the right query string
        if(!rightQueryStr.equals(querystr)){
            responseNewsResult.setRightQueryString(rightQueryStr);
        }
        return responseNewsResult;
    }


}
