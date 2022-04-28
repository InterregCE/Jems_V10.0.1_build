package io.cloudflight.jems.server.audit.repository

import io.cloudflight.jems.server.audit.repository.AuditPersistenceProvider.Companion.AUDIT_INDEX
import io.cloudflight.jems.server.audit.repository.AuditPersistenceProvider.Companion.AUDIT_INDEX_V2
import io.cloudflight.jems.server.audit.repository.AuditPersistenceProvider.Companion.AUDIT_INDEX_V3
import io.cloudflight.jems.server.config.AUDIT_ENABLED
import io.cloudflight.jems.server.config.AUDIT_PROPERTY_PREFIX
import org.apache.commons.io.IOUtils
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest
import org.elasticsearch.client.RequestOptions
import org.elasticsearch.client.RestHighLevelClient
import org.elasticsearch.client.indices.CreateIndexRequest
import org.elasticsearch.client.indices.GetIndexRequest
import org.elasticsearch.index.reindex.ReindexRequest
import org.elasticsearch.xcontent.XContentType
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.ApplicationListener
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets


private const val INDEX_NAME = AUDIT_INDEX
private const val INDEX_NAME_V2 = AUDIT_INDEX_V2
private const val INDEX_NAME_V3 = AUDIT_INDEX_V3
private const val ES_INDEX_SOURCE_JSON = "elasticsearch/create_index_body.json"


@Component
@ConditionalOnProperty(prefix = AUDIT_PROPERTY_PREFIX, name = [AUDIT_ENABLED], havingValue = "true")
class ElasticsearchAuditIndexInitializer(private val client: RestHighLevelClient) :
    ApplicationListener<ApplicationReadyEvent> {

    companion object {
        private val logger = LoggerFactory.getLogger(ElasticsearchAuditIndexInitializer::class.java)
    }

    override fun onApplicationEvent(event: ApplicationReadyEvent) {
        createIndexIfNotExists(INDEX_NAME, INDEX_NAME_V2)
        createIndexIfNotExists(INDEX_NAME_V2, INDEX_NAME_V3)
    }

    private fun createIndexIfNotExists(oldIndex: String, newIndex: String) {
        val getOldRequest = GetIndexRequest(oldIndex)
        val getNewRequest = GetIndexRequest(newIndex)

        if (client.indices().exists(getNewRequest, RequestOptions.DEFAULT)) {
            logger.info("Index '${newIndex}' already exists in ElasticSearch.")
            return
        }

        if (!client.indices().exists(getOldRequest, RequestOptions.DEFAULT)) {
            createIndex(newIndex)
        } else {
            createIndex(newIndex)
            reIndex(oldIndex, newIndex)
            deleteIndex(oldIndex)
        }
    }

    private fun getIndexSource(): String =
        IOUtils.toString(
            javaClass.classLoader.getResourceAsStream(ES_INDEX_SOURCE_JSON)
                ?: throw IllegalArgumentException("File $ES_INDEX_SOURCE_JSON is not available, though system cannot create index in ElasticSearch."),
            StandardCharsets.UTF_8
        )

    private fun reIndex(oldIndex: String, newIndex: String) {
        logger.info("Reindexing: '${oldIndex}' to: '${newIndex}' in ElasticSearch:")
        val reindexRequest = ReindexRequest()
        reindexRequest.setSourceIndices(oldIndex)
        reindexRequest.setDestIndex(newIndex)
        client.reindex(reindexRequest, RequestOptions.DEFAULT)
        logger.info("Reindex: '${oldIndex}' to: '${newIndex}' done in ElasticSearch")
    }

    private fun createIndex(newIndex: String) {
        logger.info("Creating index '${newIndex}' in ElasticSearch:")
        val createRequest = CreateIndexRequest(newIndex)
        createRequest.source(getIndexSource(), XContentType.JSON)
        client.indices().create(createRequest, RequestOptions.DEFAULT)
        logger.info("Index: '${newIndex}' is created in ElasticSearch")
        return
    }

    private fun deleteIndex(index: String) {
        logger.info("Deleting index '${index}' in ElasticSearch:")
        val deleteIndexRequest = DeleteIndexRequest(index)
        client.indices().delete(deleteIndexRequest, RequestOptions.DEFAULT)
        logger.info("Index: '${index}' is deleted in ElasticSearch")
    }
}
