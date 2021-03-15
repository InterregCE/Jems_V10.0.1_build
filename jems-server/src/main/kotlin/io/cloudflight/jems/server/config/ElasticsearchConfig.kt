package io.cloudflight.jems.server.config

import org.apache.http.HttpHost
import org.elasticsearch.client.RestClient
import org.elasticsearch.client.RestHighLevelClient
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

const val AUDIT_PROPERTY_PREFIX = "audit-service"
const val AUDIT_ENABLED = "enabled"

/**
 * For audit configuration there are mandatory application properties:
 *   audit-service.enabled: [true|false]
 *   audit-service.url-and-port: [specify URL:PORT of ElasticSearch
 */
@Configuration
@ConditionalOnProperty(prefix = AUDIT_PROPERTY_PREFIX, name = [AUDIT_ENABLED], havingValue = "true")
@ConfigurationProperties(prefix = AUDIT_PROPERTY_PREFIX)
class ElasticsearchConfig {

    lateinit var urlAndPort: String

    @Bean(destroyMethod = "close")
    fun client(): RestHighLevelClient =
        RestHighLevelClient(RestClient.builder(HttpHost.create(urlAndPort)))

}
