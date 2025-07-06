package com.qiubithub.mongodb.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.util.concurrent.TimeUnit;

/**
 * MongoDB数据库配置
 */
@Configuration
@EnableMongoRepositories(basePackages = "com.qiubithub.mongodb.repository")
public class MongoConfig extends AbstractMongoClientConfiguration {

    @Value("${spring.data.mongodb.uri}")
    private String mongoUri;

    @Override
    protected String getDatabaseName() {
        return mongoUri.substring(mongoUri.lastIndexOf("/") + 1);
    }

    @Override
    public MongoClient mongoClient() {
        ConnectionString connectionString = new ConnectionString(mongoUri);
        MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .applyToConnectionPoolSettings(builder -> 
                    builder.maxConnectionIdleTime(30, TimeUnit.SECONDS)
                           .maxSize(20)
                           .minSize(5))
                .applyToSocketSettings(builder -> 
                    builder.connectTimeout(3000, TimeUnit.MILLISECONDS)
                           .readTimeout(5000, TimeUnit.MILLISECONDS))
                .build();
        
        return MongoClients.create(mongoClientSettings);
    }

    @Bean
    public MongoTemplate mongoTemplate() throws Exception {
        MongoDatabaseFactory factory = mongoDbFactory();
        
        // 自定义MongoConverter，移除_class字段
        MappingMongoConverter converter = new MappingMongoConverter(
                new DefaultDbRefResolver(factory), 
                new MongoMappingContext());
        converter.setTypeMapper(new DefaultMongoTypeMapper(null));
        
        MongoTemplate mongoTemplate = new MongoTemplate(factory, converter);
        return mongoTemplate;
    }

    @Bean
    public MongoTransactionManager transactionManager(MongoDatabaseFactory dbFactory) {
        return new MongoTransactionManager(dbFactory);
    }
}