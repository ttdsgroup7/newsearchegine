package top.caohongchuan.newsearch.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.caohongchuan.commonutil.ExceptionTypes.BizException;
import top.caohongchuan.commonutil.datatypes.ResponseNewsResult;
import top.caohongchuan.commonutil.receiveType.UpdateRecord;
import top.caohongchuan.commonutil.returntypes.ReturnJson;
import top.caohongchuan.newsearch.service.ObtainDatasetsService;
import top.caohongchuan.newsearch.service.RecordService;
import top.caohongchuan.newsearch.service.SearchService;

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
    public ReturnJson<ResponseNewsResult> query(@RequestParam("query") String querystr, @RequestParam(name = "extension", defaultValue = "false") boolean extension) {
        try {
            ResponseNewsResult responseNewsResult = searchService.dealquery(querystr, extension);
            return ReturnJson.success(responseNewsResult);
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
}
