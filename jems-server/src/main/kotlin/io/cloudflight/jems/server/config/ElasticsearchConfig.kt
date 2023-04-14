package io.cloudflight.jems.server.config

import co.elastic.clients.elasticsearch.ElasticsearchClient
import co.elastic.clients.json.jackson.JacksonJsonpMapper
import co.elastic.clients.transport.rest_client.RestClientTransport
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import org.apache.http.HttpHost
import org.apache.http.auth.AuthScope
import org.apache.http.auth.UsernamePasswordCredentials
import org.apache.http.impl.client.BasicCredentialsProvider
import org.elasticsearch.client.RestClient
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
    lateinit var password: String

    @Bean
    fun client(): ElasticsearchClient {
        val credentials = BasicCredentialsProvider()
        credentials.setCredentials(AuthScope.ANY, UsernamePasswordCredentials("elastic", password))
        return ElasticsearchClient(
            RestClientTransport(
                RestClient.builder(HttpHost.create(urlAndPort))
                    .setHttpClientConfigCallback { hc -> hc
                        // to configure with CA certificate (auto-generated on Elastic 8)
                        //.setSSLContext(TransportUtils.sslContextFromCaFingerprint("fingerprint"))
                        .setDefaultCredentialsProvider(credentials)
                    }.build(),
                JacksonJsonpMapper(
                    ObjectMapper()
                        .registerModule(JavaTimeModule())
                        .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                )
            )
        )
    }
}
