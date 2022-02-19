//package top.caohongchuan.newsrecommand.config;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.web.bind.annotation.ControllerAdvice;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.bind.annotation.ResponseBody;
//import top.caohongchuan.commonutil.ExceptionTypes.BizException;
//import top.caohongchuan.commonutil.returntypes.ResultCode;
//import top.caohongchuan.commonutil.returntypes.ReturnJson;
//
//import javax.servlet.http.HttpServletRequest;
//
//@Slf4j
//@ControllerAdvice
//public class GlobalExceptionHandler {
//
//    @ExceptionHandler(value = BizException.class)
//    @ResponseBody
//        public ReturnJson bizExceptionHandler(HttpServletRequest req, BizException e) {
//        if(e.getErrorCode()==null){
//            return ReturnJson.error("Servers Error");
//        }else if(e.getErrorCode().equals(ResultCode.NOPERMISSION.getResultCode())){
//            return ReturnJson.error(ResultCode.NOPERMISSION, e.getMessage());
//        }else{
//            return ReturnJson.error("Servers Error");
//        }
//    }
//}
