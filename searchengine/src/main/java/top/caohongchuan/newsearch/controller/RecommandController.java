package top.caohongchuan.newsearch.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.caohongchuan.commonutil.ExceptionTypes.BizException;
import top.caohongchuan.commonutil.datatypes.ResponseNewsResult;
import top.caohongchuan.commonutil.receiveType.UserInfo;
import top.caohongchuan.commonutil.returntypes.ReturnJson;
import top.caohongchuan.newsearch.service.RecommendService;

/**
 * Controller of Recommend news
 *
 * @author Hongchuan CAO
 */
@Api(tags = "News Recommend API")
@Slf4j
@RestController
@RequestMapping(value = "/recommend")
@CrossOrigin(origins = "*", maxAge = 3600)
public class RecommandController {

    @Autowired
    RecommendService recommendService;

    @ApiOperation("Send account name and password to obtain the recommend news for user")
    @PostMapping("/renews")
    public ReturnJson<ResponseNewsResult> recommend(@RequestBody UserInfo userInfo) {
        try {
            ResponseNewsResult responseNewsResult = recommendService.getRecommendNews(userInfo, 1, 100);
            return ReturnJson.success(responseNewsResult);
        } catch (BizException e) {
            throw e;
        } catch (Exception e) {
            throw new BizException();
        }
    }
}
