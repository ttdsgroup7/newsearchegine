package top.caohongchuan.commonutil.datatypes;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import com.github.pagehelper.PageInfo;


@Data
@ApiModel(description = "Store News List")
public class ResponseNewsResult {
    @ApiModelProperty("Account id")
    private int userId;
    @ApiModelProperty("News Array")
    private PageInfo<NewsItem> newsarray;
    @ApiModelProperty("Correct Array")
    private String rightQueryString=null;
}
