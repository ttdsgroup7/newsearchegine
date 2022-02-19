package top.caohongchuan.commonutil.returntypes;

import io.swagger.annotations.ApiModel;
import lombok.Getter;

@ApiModel
@Getter
public enum ResultCode implements BaseErrorInfoInterface {

    // 数据操作错误定义
    SUCCESS("200", "Success"),
    NOPERMISSION("403", "No Permission"),
    NOAUTH("301", "No Login"),
    BODY_NOT_MATCH("400", "Request Data Style Error"),
    NOT_FOUND("404", "Not Found"),
    ERROR("500", "System Error"),
    SERVER_BUSY("503", "System Busy");

    private String resultCode;

    private String resultMsg;

    ResultCode(String resultCode, String resultMsg) {
        this.resultCode = resultCode;
        this.resultMsg = resultMsg;
    }

    @Override
    public String getResultCode() {
        return resultCode;
    }

    @Override
    public String getResultMsg() {
        return resultMsg;
    }
}
