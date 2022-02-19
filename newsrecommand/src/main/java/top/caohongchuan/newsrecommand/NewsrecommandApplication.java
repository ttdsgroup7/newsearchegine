package top.caohongchuan.newsrecommand;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class NewsrecommandApplication {

    public static void main(String[] args) {
        SpringApplication.run(NewsrecommandApplication.class, args);
    }

}
