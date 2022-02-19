package top.caohongchuan.commonutil.datatypes;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.sql.Timestamp;

@ApiModel
@Data
public class RecommendationItem {
    int user_id;
    long news_id;
    int derive_algorithm;
}
