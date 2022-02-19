package top.caohongchuan.commonutil.datatypes;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.sql.Timestamp;

@Data
@ApiModel
public class UserItem {
    @ApiModelProperty(name = "id", dataType = "java.lang.Integer", required = false)
    int id;
    @ApiModelProperty(name = "pref_list", dataType = "java.lang.String", required = false)
    String pref_list;
    @ApiModelProperty(name = "latest_log_time", dataType = "java.sql.Timestamp", required = false)
    Timestamp latest_log_time;
    @ApiModelProperty(name = "username", value = "account name", dataType = "java.lang.String", required = true)
    String username;
    @ApiModelProperty(name = "password", value = "account password", dataType = "java.lang.String", required = true)
    String password;
}
