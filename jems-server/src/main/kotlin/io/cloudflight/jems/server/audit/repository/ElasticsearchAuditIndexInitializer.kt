package io.cloudflight.jems.server.audit.repository

import co.elastic.clients.elasticsearch.ElasticsearchClient
import co.elastic.clients.elasticsearch._types.mapping.TypeMapping
import co.elastic.clients.elasticsearch.core.ReindexRequest
import co.elastic.clients.elasticsearch.core.reindex.Destination
import co.elastic.clients.elasticsearch.core.reindex.Source
import co.elastic.clients.elasticsearch.indices.CreateIndexRequest
import co.elastic.clients.elasticsearch.indices.DeleteIndexRequest
import co.elastic.clients.elasticsearch.indices.ExistsRequest
import co.elastic.clients.elasticsearch.indices.IndexSettings
import io.cloudflight.jems.server.audit.repository.AuditPersistenceProvider.Companion.AUDIT_INDEX
import io.cloudflight.jems.server.audit.repository.AuditPersistenceProvider.Companion.AUDIT_INDEX_V3
import io.cloudflight.jems.server.config.AUDIT_ENABLED
import io.cloudflight.jems.server.config.AUDIT_PROPERTY_PREFIX
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.ApplicationListener
import org.springframework.stereotype.Component

private const val INDEX_NAME = AUDIT_INDEX
private const val INDEX_NAME_V3 = AUDIT_INDEX_V3
private const val ES_INDEX_MAPPINGS = "elasticsearch/create_index_mappings.json"
private const val ES_INDEX_SETTINGS = "elasticsearch/create_index_settings.json"

@Component
@ConditionalOnProperty(prefix = AUDIT_PROPERTY_PREFIX, name = [AUDIT_ENABLED], havingValue = "true")
class ElasticsearchAuditIndexInitializer(private val client: ElasticsearchClient) :
    ApplicationListener<ApplicationReadyEvent> {

    companion object {
        private val logger = LoggerFactory.getLogger(ElasticsearchAuditIndexInitializer::class.java)
    }

    override fun onApplicationEvent(event: ApplicationReadyEvent) {
        createIndexIfNotExists(INDEX_NAME, INDEX_NAME_V3)
    }

    private fun createIndexIfNotExists(oldIndex: String, newIndex: String) {
        val getOldRequest = ExistsRequest.of { e -> e.index(oldIndex) }
        val getNewRequest = ExistsRequest.of { e -> e.index(newIndex) }

        if (client.indices().exists(getNewRequest).value()) {
            logger.info("Index '${newIndex}' already exists in ElasticSearch.")
            return
        }

        if (!client.indices().exists(getOldRequest).value()) {
            createIndex(newIndex)
        } else {
            createIndex(newIndex)
            reIndex(oldIndex, newIndex)
            deleteIndex(oldIndex)
        }
    }

    private fun reIndex(oldIndex: String, newIndex: String) {
        logger.info("Reindexing: '${oldIndex}' to: '${newIndex}' in ElasticSearch:")
        val reindexRequest = ReindexRequest.Builder()
            .source(Source.Builder().index(oldIndex).build())
            .dest(Destination.Builder().index(newIndex).build())
            .build()
        client.reindex(reindexRequest)
        logger.info("Reindex: '${oldIndex}' to: '${newIndex}' done in ElasticSearch")
    }

    private fun createIndex(newIndex: String) {
        logger.info("Creating index '${newIndex}' in ElasticSearch:")
        val createRequest = CreateIndexRequest.Builder()
            .index(newIndex)
            .mappings(
                TypeMapping.Builder().withJson(javaClass.classLoader.getResourceAsStream(ES_INDEX_MAPPINGS)).build()
            )
            .settings(
                IndexSettings.Builder().withJson(javaClass.classLoader.getResourceAsStream(ES_INDEX_SETTINGS)).build()
            ).build()
        client.indices().create(createRequest)
        logger.info("Index: '${newIndex}' is created in ElasticSearch")
        return
    }

    private fun deleteIndex(index: String) {
        logger.info("Deleting index '${index}' in ElasticSearch:")
        val deleteIndexRequest = DeleteIndexRequest.Builder().index(index).build()
        client.indices().delete(deleteIndexRequest)
        logger.info("Index: '${index}' is deleted in ElasticSearch")
    }
}
