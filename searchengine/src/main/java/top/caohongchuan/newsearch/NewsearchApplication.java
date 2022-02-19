package top.caohongchuan.newsearch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import springfox.documentation.oas.annotations.EnableOpenApi;

@EnableOpenApi
@SpringBootApplication
public class NewsearchApplication {

	public static void main(String[] args) {
		SpringApplication.run(NewsearchApplication.class, args);
	}

}
