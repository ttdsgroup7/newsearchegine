package top.caohongchuan.commonutil.receiveType;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import top.caohongchuan.commonutil.datatypes.NewsLogsItem;

import java.util.List;

@ApiModel
@Data
public class UpdateRecord {
    @ApiModelProperty(name = "username", value = "account name", dataType = "java.lang.String", required = true)
    String username;
    @ApiModelProperty(name = "newsLogsItemList", value = "view record for user", dataType = "top.caohongchuan.commonutil.datatypes.NewsLogsItem", required = true)
    List<NewsLogsItem> newsLogsItemList;
}
