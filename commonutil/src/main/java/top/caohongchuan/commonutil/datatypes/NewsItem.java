package top.caohongchuan.commonutil.datatypes;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.sql.Timestamp;

@ApiModel
@Data
public class NewsItem {
    long id;
    Timestamp publish_date;
    String head_line;
    String news_abstract;
    String content;
    String country;
    String image;
    String theme;
    String url;
}
