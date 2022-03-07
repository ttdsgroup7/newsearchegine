package top.caohongchuan.newsearch.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import top.caohongchuan.commonutil.ExceptionTypes.BizException;
import top.caohongchuan.commonutil.datatypes.ResponseNewsResult;
import top.caohongchuan.commonutil.receiveType.UpdateRecord;
import top.caohongchuan.commonutil.returntypes.ReturnJson;
import top.caohongchuan.newsearch.service.ObtainDatasetsService;
import top.caohongchuan.newsearch.service.RecordService;
import top.caohongchuan.newsearch.service.SearchService;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

@Api(tags = "Query Search API")
@Slf4j
@RestController
@RequestMapping(value = "/search")
@CrossOrigin(origins = "*", maxAge = 3600)
public class SearchController {

    @Autowired
    SearchService searchService;
    @Autowired
    RecordService recordService;
    @Autowired
    ObtainDatasetsService obtainDatasetsService;

    @ApiOperation("Send one query to server and obtain news list")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "query", value = "Query String (contain search words and keyword)", dataTypeClass = String.class, required = true),
            @ApiImplicitParam(name = "extension", value = "Weather open extension query or not", dataTypeClass = Boolean.class, required = false)
    })
    @GetMapping("/querynews")
    public ReturnJson<ResponseNewsResult> query(@RequestParam("query") String querystr, @RequestParam(name = "extension", defaultValue = "false") boolean extension, @RequestParam(name = "page") int page, @RequestParam(name = "pagesize") int pageSize) {
        try {
            ResponseNewsResult responseNewsResult = searchService.dealquery(querystr, extension, page, pageSize);
            return ReturnJson.success(responseNewsResult);
        } catch (Exception e) {
            throw new BizException();
        }
    }

    @ApiOperation("Obtain word suggesstion")
    @ApiImplicitParam(name = "wordsug", value = "word input", dataTypeClass = String.class, required = false)
    @GetMapping("/wordsug")
    public ReturnJson getDownSuggestion(@RequestParam("wordsug")String wordSuggestion) {
        try {
            return ReturnJson.success(obtainDatasetsService.obtainDownSug(wordSuggestion));
        } catch (Exception e) {
            throw new BizException();
        }
    }

    @ApiOperation("Obtain country list")
    @GetMapping("/getcountry")
    public ReturnJson getCountry() {
        try {
            return ReturnJson.success(obtainDatasetsService.obtainCountry());
        } catch (Exception e) {
            throw new BizException();
        }
    }


    @ApiOperation("Obtain theme list")
    @GetMapping("/gettheme")
    public ReturnJson getTheme() {
        try {
            return ReturnJson.success(obtainDatasetsService.obtainTheme());
        } catch (Exception e) {
            throw new BizException();
        }
    }

    @ApiOperation("Send view record to server")
    @PutMapping("/viewrecord")
    public ReturnJson viewRecord(@RequestBody UpdateRecord newsLogList) {
        try {
            recordService.updateRecord(newsLogList);
            return ReturnJson.success(null);
        } catch (Exception e) {
            throw new BizException();
        }
    }

    @ApiOperation("Obtain news by country")
    @GetMapping("/newsbycountry")
    public ReturnJson getNewsByCountry(@RequestParam("country") String country, @RequestParam(name = "page") int page, @RequestParam(name = "pagesize") int pageSize) {
        try {
            return ReturnJson.success(obtainDatasetsService.obtainNewsByCountry(country, page, pageSize));
        } catch (Exception e) {
            throw new BizException();
        }
    }

    @ApiOperation("Obtain news by theme")
    @GetMapping("/newsbytheme")
    public ReturnJson getNewsByTheme(@RequestParam("theme") String theme, @RequestParam(name = "page") int page, @RequestParam(name = "pagesize") int pageSize) {
        try {
            return ReturnJson.success(obtainDatasetsService.obtainNewsByTheme(theme, page, pageSize));
        } catch (Exception e) {
            throw new BizException();
        }
    }

    @ApiOperation("Obtain news by time")
    @GetMapping("/newsbytime")
    public ReturnJson getNewsByTime(@RequestParam("starttime") Timestamp start, @RequestParam("endtime") Timestamp end) {
        try {
            return ReturnJson.success(obtainDatasetsService.obtainNewsByTime(start, end));
        } catch (Exception e) {
            throw new BizException();
        }
    }

    @InitBinder
    public void initBinder(WebDataBinder binder, WebRequest request) {

        //转换日期
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        binder.registerCustomEditor(Timestamp.class, new CustomDateEditor(dateFormat, true));// CustomDateEditor为自定义日期编辑器
    }

}
