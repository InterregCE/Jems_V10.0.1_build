package io.cloudflight.jems.server.audit.repository

import io.cloudflight.jems.server.audit.repository.AuditPersistenceProvider.Companion.AUDIT_INDEX
import io.cloudflight.jems.server.config.AUDIT_ENABLED
import io.cloudflight.jems.server.config.AUDIT_PROPERTY_PREFIX
import org.apache.commons.io.IOUtils
import org.elasticsearch.client.RequestOptions
import org.elasticsearch.client.RestHighLevelClient
import org.elasticsearch.client.indices.CreateIndexRequest
import org.elasticsearch.client.indices.GetIndexRequest
import org.elasticsearch.common.xcontent.XContentType
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.ApplicationListener
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets


private const val INDEX_NAME = AUDIT_INDEX
private const val ES_INDEX_SOURCE_JSON = "elasticsearch/create_index_body.json"


@Component
@ConditionalOnProperty(prefix = AUDIT_PROPERTY_PREFIX, name = [AUDIT_ENABLED], havingValue = "true")
class ElasticsearchAuditIndexInitializer(private val client: RestHighLevelClient) :
    ApplicationListener<ApplicationReadyEvent> {

    companion object {
        private val logger = LoggerFactory.getLogger(ElasticsearchAuditIndexInitializer::class.java)
    }

    override fun onApplicationEvent(event: ApplicationReadyEvent) =
        createIndexIfNotExists()

    private fun createIndexIfNotExists() {
        val getRequest = GetIndexRequest(INDEX_NAME)
        if (client.indices().exists(getRequest, RequestOptions.DEFAULT)) {
            logger.info("Index '${INDEX_NAME}' already exists in ElasticSearch.")
            return
        }

        logger.info("Creating index '${INDEX_NAME}' in ElasticSearch:")

        val createRequest = CreateIndexRequest(INDEX_NAME)
        createRequest.source(getIndexSource(), XContentType.JSON)
        client.indices().create(createRequest, RequestOptions.DEFAULT)

        logger.info("Index: '${INDEX_NAME}' is created in ElasticSearch")
    }

    private fun getIndexSource(): String =
        IOUtils.toString(
            javaClass.classLoader.getResourceAsStream(ES_INDEX_SOURCE_JSON)
                ?: throw IllegalArgumentException("File $ES_INDEX_SOURCE_JSON is not available, though system cannot create index in ElasticSearch."),
            StandardCharsets.UTF_8
        )
}
