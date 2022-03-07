package top.caohongchuan.commonutil.datatypes;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.sql.Timestamp;

@ApiModel
@Data
public class NewsLogsItem {
    @ApiModelProperty(name = "user_id", dataType = "java.lang.Integer", required = false)
    int user_id;
    @ApiModelProperty(name = "news_id", dataType = "java.lang.Long", required = true)
    long news_id;
    @ApiModelProperty(name = "view_time", dataType = "java.sql.Timestamp", required = false)
    Timestamp view_time;
    @ApiModelProperty(name = "prefer_degree", dataType = "java.lang.Double", required = true)
    double prefer_degree;
}
