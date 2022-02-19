package top.caohongchuan.commonutil.receiveType;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class UserInfo {
    @ApiModelProperty(name = "username", value = "account name", dataType = "java.lang.String", required = true)
    String username;
    @ApiModelProperty(name = "password", value = "account password", dataType = "java.lang.String", required = true)
    String password;
}
