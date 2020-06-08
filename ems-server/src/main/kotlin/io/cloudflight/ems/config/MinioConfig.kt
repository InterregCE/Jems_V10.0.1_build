package io.cloudflight.ems.config

import io.minio.MinioClient
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "minio-storage")
class MinioConfig {

    lateinit var endpoint: String
    lateinit var accessKey: String
    lateinit var secretKey: String

    @Bean
    fun initClient(): MinioClient {
        return MinioClient(endpoint, accessKey, secretKey)
    }

}
