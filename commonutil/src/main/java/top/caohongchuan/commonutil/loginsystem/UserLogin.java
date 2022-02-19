package top.caohongchuan.commonutil.loginsystem;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@ApiModel
@Data
public class UserLogin {
    String username;
    String password;
}
