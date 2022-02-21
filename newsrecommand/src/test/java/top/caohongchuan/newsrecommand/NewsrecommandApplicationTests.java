package top.caohongchuan.newsrecommand;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import top.caohongchuan.newsrecommand.UserBasedCollaborativeRecommender.MahoutUserBasedCollaborativeRecommender;
import top.caohongchuan.newsrecommand.contentbasedrecommend.ContentBasedRecommender;
import top.caohongchuan.newsrecommand.dao.ContentBasedDao;
import top.caohongchuan.newsrecommand.hotrecommend.HotRecommender;
import top.caohongchuan.newsrecommand.service.JobService;

import java.util.List;

@SpringBootTest
class NewsrecommandApplicationTests {

    @Autowired
    JobService jobService;

    @Test
    void contextLoads() {
        jobService.JobSetter(true, true, true);
        jobService.deleteExpiryRecommendation();
        jobService.executeInstantJobForAllUsers();
    }

}
