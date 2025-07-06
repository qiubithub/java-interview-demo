package com.qiubithub.es.config;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.time.Duration;

/**
 * Elasticsearch RestHighLevelClient配置类
 * 用于高级搜索和索引管理功能
 */
@Configuration
@ConditionalOnProperty(name = "app.es.enabled", havingValue = "true")
public class RestClientConfig {

    @Value("${spring.elasticsearch.uris}")
    private String uris;

    @Value("${spring.elasticsearch.username:}")
    private String username;

    @Value("${spring.elasticsearch.password:}")
    private String password;

    @Value("${spring.elasticsearch.connection-timeout:5s}")
    private Duration connectionTimeout;

    @Value("${spring.elasticsearch.socket-timeout:30s}")
    private Duration socketTimeout;

    /**
     * 创建RestHighLevelClient客户端
     * @return RestHighLevelClient
     */
    @Bean
    public RestHighLevelClient restHighLevelClient() {
        // 解析URI
        String[] hosts = uris.split(",");
        HttpHost[] httpHosts = new HttpHost[hosts.length];
        
        for (int i = 0; i < hosts.length; i++) {
            String host = hosts[i];
            // 移除协议前缀
            if (host.startsWith("http://")) {
                host = host.substring(7);
            } else if (host.startsWith("https://")) {
                host = host.substring(8);
            }
            
            // 解析主机和端口
            String[] hostAndPort = host.split(":");
            String hostname = hostAndPort[0];
            int port = hostAndPort.length > 1 ? Integer.parseInt(hostAndPort[1]) : 9200;
            
            httpHosts[i] = new HttpHost(hostname, port, "http");
        }
        
        // 创建RestClientBuilder
        RestClientBuilder builder = RestClient.builder(httpHosts);
        
        // 设置连接超时和Socket超时
        builder.setRequestConfigCallback(requestConfigBuilder -> 
                requestConfigBuilder
                        .setConnectTimeout((int) connectionTimeout.toMillis())
                        .setSocketTimeout((int) socketTimeout.toMillis()));
        
        // 如果提供了用户名和密码，设置基本认证
        if (StringUtils.hasText(username) && StringUtils.hasText(password)) {
            final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            credentialsProvider.setCredentials(AuthScope.ANY,
                    new UsernamePasswordCredentials(username, password));
            
            builder.setHttpClientConfigCallback(httpClientBuilder -> 
                    httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider));
        }
        
        // 创建并返回客户端
        return new RestHighLevelClient(builder);
    }
} 