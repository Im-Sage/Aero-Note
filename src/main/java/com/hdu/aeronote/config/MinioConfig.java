package com.hdu.aeronote.config;

import io.minio.BucketExistsArgs;
import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinioConfig {
    // 动态读取application.properties中的配置
    @Value("${minio.endpoint}")
    private String endpoint;

    @Value("${minio.accessKey}")
    private String accessKey;

    @Value("${minio.secretKey}")
    private String secretKey;

    @Value("${minio.bucketName}")
    private String bucketName;

    @Bean
    public MinioClient minioClient() throws Exception {
        MinioClient minioClient = MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey) // 咱们之前 Docker 里改的密码
                .build();

        // 检查Bucket是否存在，如果不存在则创建
        boolean exits = minioClient.bucketExists(
                BucketExistsArgs.builder().bucket(bucketName).build()
        );

        // 如果桶不存在，创建一个新的桶
        if (!exits) {
            minioClient.makeBucket(
                    io.minio.MakeBucketArgs.builder().bucket(bucketName).build()
            );
            System.out.printf("Bucket is not exist. Created bucket: %s\n", bucketName);
        }
        return minioClient;
    }
}
