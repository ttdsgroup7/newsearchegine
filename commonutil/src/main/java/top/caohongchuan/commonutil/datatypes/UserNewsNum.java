package top.caohongchuan.commonutil.datatypes;

import io.swagger.annotations.ApiModel;
import lombok.Data;

@ApiModel
@Data
public class UserNewsNum {
    int user_id;
    int recnums;
}
