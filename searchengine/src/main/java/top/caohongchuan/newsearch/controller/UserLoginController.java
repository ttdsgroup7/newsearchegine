package top.caohongchuan.newsearch.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.caohongchuan.commonutil.ExceptionTypes.BizException;
import top.caohongchuan.commonutil.ExceptionTypes.UserNameExitedException;
import top.caohongchuan.commonutil.datatypes.UserItem;
import top.caohongchuan.commonutil.returntypes.ReturnJson;
import top.caohongchuan.newsearch.dao.UsersDao;
import top.caohongchuan.newsearch.service.UserLoginService;

@Api(tags="User Register API")
@RestController
@RequestMapping(value = "/login")
@CrossOrigin(origins = "*",maxAge = 3600)
public class UserLoginController {

    @Autowired
    UserLoginService userLoginService;

    @ApiOperation("Send username and password to register")
    @PostMapping("/register")
    public ReturnJson register(@RequestBody UserItem userItem){
        try{
            userLoginService.userRegister(userItem.getUsername(), userItem.getPassword());
            return ReturnJson.success(null);
        }catch (UserNameExitedException e){
            return ReturnJson.error("username has existed");
        }catch (Exception e){
            throw new BizException();
        }
    }
}
