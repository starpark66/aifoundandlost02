package org.example.aifoundandlost;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.ai.autoconfigure.openai.OpenAiAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("org.example.aifoundandlost.mapper")
@SpringBootApplication(exclude = {OpenAiAutoConfiguration.class})
public class AifoundandlostApplication {

    public static void main(String[] args) {
        SpringApplication.run(AifoundandlostApplication.class, args);
    }

}
