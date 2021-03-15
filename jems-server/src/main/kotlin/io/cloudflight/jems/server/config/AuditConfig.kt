package io.cloudflight.jems.server.config

import io.cloudflight.jems.server.audit.repository.AuditPersistenceProvider.Companion.AUDIT_INDEX
import org.apache.commons.io.IOUtils
import org.elasticsearch.client.RequestOptions
import org.elasticsearch.client.RestHighLevelClient
import org.elasticsearch.client.indices.CreateIndexRequest
import org.elasticsearch.client.indices.GetIndexRequest
import org.elasticsearch.common.xcontent.XContentType
import org.slf4j.LoggerFactory
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.ApplicationListener
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets

@Component
class AuditConfig(
    private val client: RestHighLevelClient,
) : ApplicationListener<ApplicationReadyEvent> {

    companion object {
        private val logger = LoggerFactory.getLogger(AuditConfig::class.java)
        private const val ES_INDEX_JSON = "elasticsearch/create_index_body.json"
    }

    override fun onApplicationEvent(event: ApplicationReadyEvent) {
        val indexExists = client.indices().exists(GetIndexRequest(AUDIT_INDEX), RequestOptions.DEFAULT)
        if (indexExists) {
            logger.info("Index '$AUDIT_INDEX' already exists in ElasticSearch.")
            return
        }

        logger.info("Creating index '$AUDIT_INDEX' in ElasticSearch:")

        /*val index = IOUtils.toString(
            javaClass.classLoader.getResourceAsStream(ES_INDEX_JSON)
                ?: throw IllegalArgumentException("File $ES_INDEX_JSON is not available, though system cannot create index in ElasticSearch."),
            StandardCharsets.UTF_8)

        val createIndexRequest = CreateIndexRequest(AUDIT_INDEX)
        createIndexRequest.mapping(index, XContentType.JSON)

        val response = client.indices().create(createIndexRequest, RequestOptions.DEFAULT)
        logger.info("Index '${response.index()}' was created in ElasticSearch.")*/
    }

}
