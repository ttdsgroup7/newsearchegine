package top.caohongchuan.newsrecommand;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import top.caohongchuan.newsrecommand.UserBasedCollaborativeRecommender.MahoutUserBasedCollaborativeRecommender;
import top.caohongchuan.newsrecommand.algorithms.RecommendKit;
import top.caohongchuan.newsrecommand.contentbasedrecommend.ContentBasedRecommender;
import top.caohongchuan.newsrecommand.dao.ContentBasedDao;
import top.caohongchuan.newsrecommand.hotrecommend.HotRecommender;

import java.util.ArrayList;
import java.util.Arrays;

@SpringBootTest
class NewsrecommandApplicationTests {

    @Autowired
    MahoutUserBasedCollaborativeRecommender mub;
    @Autowired
    HotRecommender hotRecommender;

    @Autowired
    ContentBasedRecommender contentBasedRecommender;

    @Autowired
    ContentBasedDao contentBasedDao;

    @Test
    void contextLoads() {
//        mub.recommend(Arrays.asList(1,2,3,4,5,6,7));
//        hotRecommender.recommend(Arrays.asList(1,2,3,4,5,6,7));
        contentBasedRecommender.recommend(contentBasedDao.queryAllUserId());
    }

}
