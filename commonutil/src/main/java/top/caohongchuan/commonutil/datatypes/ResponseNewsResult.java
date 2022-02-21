package top.caohongchuan.commonutil.datatypes;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.stereotype.Component;
import java.util.List;


@Data
@ApiModel(description = "Store News List")
public class ResponseNewsResult {
    @ApiModelProperty("Account id")
    private int userId;
    @ApiModelProperty("News Array")
    private List<NewsItem> newsarray;
    @ApiModelProperty("Correct Array")
    private String rightQueryString=null;
}
