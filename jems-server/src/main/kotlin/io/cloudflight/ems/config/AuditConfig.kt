package io.cloudflight.ems.config

import io.cloudflight.ems.audit.repository.AuditRepository
import org.elasticsearch.client.RestHighLevelClient
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.data.elasticsearch.client.ClientConfiguration
import org.springframework.data.elasticsearch.client.RestClients
import org.springframework.data.elasticsearch.config.AbstractElasticsearchConfiguration
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories

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
@EnableElasticsearchRepositories(basePackageClasses = [AuditRepository::class])
class AuditConfig: AbstractElasticsearchConfiguration() {

    lateinit var urlAndPort: String

    override fun elasticsearchClient(): RestHighLevelClient {
        return RestClients.create(
            ClientConfiguration.builder()
                .connectedTo(urlAndPort)
                .build()).rest()
    }

}
