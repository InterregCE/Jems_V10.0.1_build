package io.cloudflight.jems.server.config

import io.minio.MinioClient
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean

@AutoConfiguration
@ConfigurationProperties(prefix = "minio-storage")
class MinioConfig {

    lateinit var endpoint: String
    lateinit var accessKey: String
    lateinit var secretKey: String

    @Bean
    fun initClient(): MinioClient {
        return MinioClient.builder().endpoint(endpoint).credentials(accessKey, secretKey).build()
    }

}
