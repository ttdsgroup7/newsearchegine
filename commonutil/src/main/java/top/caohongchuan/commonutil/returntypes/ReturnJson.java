package top.caohongchuan.commonutil.returntypes;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel
@Data
public class ReturnJson<T> {
    @ApiModelProperty("status code")
    private String code;
    @ApiModelProperty("status message")
    private String message;
    @ApiModelProperty("return data")
    private T data;

    public ReturnJson(ResultCode resultCode, T data) {
        this.code = resultCode.getResultCode();
        this.data = data;
        this.message = resultCode.getResultMsg();
    }

    private ReturnJson(ResultCode resultCode, T data, String message) {
        this.code = resultCode.getResultCode();
        this.data = data;
        this.message = message;
    }

    public static <T> ReturnJson<T> success(T data) {
        return new ReturnJson<T>(ResultCode.SUCCESS, data);
    }

    public static <T> ReturnJson<T> success(T data, String message) {
        return new ReturnJson<T>(ResultCode.SUCCESS, data, message);
    }

    public static <T> ReturnJson<T> error() {
        return new ReturnJson<T>(ResultCode.ERROR, null);
    }

    public static <T> ReturnJson<T> error(String message) {
        return new ReturnJson<T>(ResultCode.ERROR, null, message);
    }

    public static <T> ReturnJson<T> error(ResultCode resultCode, String message) {
        return new ReturnJson<T>(resultCode, null, message);
    }

    public static <T> ReturnJson<T> noauth() {
        return new ReturnJson<T>(ResultCode.NOAUTH, null);
    }

    public static <T> ReturnJson<T> nopermission() {
        return new ReturnJson<T>(ResultCode.NOPERMISSION, null);
    }
}
