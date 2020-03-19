package com.synergetica.config;

import com.github.cloudyrock.mongock.SpringBootMongock;
import com.github.cloudyrock.mongock.SpringBootMongockBuilder;
import com.mongodb.MongoClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MongockConfig {

    @Value("${spring.data.mongodb.database}")
    private String database;

    @Bean
    public SpringBootMongock mongock(ApplicationContext springContext, MongoClient mongoClient) {
        SpringBootMongockBuilder builder = new SpringBootMongockBuilder(mongoClient, database, "com.synergetica.config")
                .setApplicationContext(springContext);
        return ((SpringBootMongockBuilder) builder.setLockQuickConfig()).build();
    }
}
