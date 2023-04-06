package io.cloudflight.jems.server.audit.repository

import co.elastic.clients.elasticsearch.ElasticsearchClient
import co.elastic.clients.elasticsearch._types.FieldSort
import co.elastic.clients.elasticsearch._types.FieldValue
import co.elastic.clients.elasticsearch._types.SortOptions
import co.elastic.clients.elasticsearch._types.SortOrder
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery
import co.elastic.clients.elasticsearch._types.query_dsl.Query
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders
import co.elastic.clients.elasticsearch._types.query_dsl.TermsQueryField
import co.elastic.clients.elasticsearch.core.SearchRequest
import io.cloudflight.jems.server.audit.model.Audit
import io.cloudflight.jems.server.audit.model.AuditFilter
import io.cloudflight.jems.server.audit.model.AuditSearchRequest
import io.cloudflight.jems.server.audit.service.AuditPersistence
import io.cloudflight.jems.server.config.AUDIT_ENABLED
import io.cloudflight.jems.server.config.AUDIT_PROPERTY_PREFIX
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.data.domain.Page
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Repository
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@Repository
@ConditionalOnProperty(prefix = AUDIT_PROPERTY_PREFIX, name = [AUDIT_ENABLED], havingValue = "true")
class AuditPersistenceProvider(
    private val client: ElasticsearchClient,
) : AuditPersistence {

    companion object {
        const val AUDIT_INDEX = "audit-log"
        const val AUDIT_INDEX_V3 = "audit-log-v3"
    }

    override fun saveAudit(audit: Audit): String {
        val response = client.index<Audit> { i -> i
            .index(AUDIT_INDEX_V3)
            .document(audit)
        }
        return response.id()
    }

    override fun getAudit(searchRequest: AuditSearchRequest): Page<Audit> =
        client.search(searchRequest.getQuery(AUDIT_INDEX_V3, searchRequest.pageable.sort), Audit::class.java)
            .hits()
            .toModel(searchRequest.pageable)

    private fun AuditSearchRequest.getQuery(index: String, sort: Sort): SearchRequest {
        val filterQuery = QueryBuilders.bool()

        filterQuery.addTimestampFilter(timeFrom, timeTo)
        filterQuery.addEmailFilter(userEmail)
        filterQuery.addFieldFilter("$FIELD_USER.$FIELD_USER_ID", userId)
        filterQuery.addFieldFilter(FIELD_ACTION, action)
        filterQuery.addFieldFilter("$FIELD_PROJECT.$FIELD_PROJECT_ID", projectId)

        val finalQuery = filterQuery.build().let { if (it.isEmpty()) null else Query.Builder().bool(it).build() }
        val timestampOrderField = FieldSort.Builder().field(FIELD_TIMESTAMP)
            .order(if (sort.getOrderFor("timestamp")?.direction?.isAscending == true) SortOrder.Asc else SortOrder.Desc)
            .build()

        return SearchRequest.Builder()
            .index(index)
            .query(finalQuery)
            .from(pageable.offset.toInt())
            .size(pageable.pageSize)
            .sort(SortOptions.Builder().field(timestampOrderField).build())
            .build()
    }

    private fun BoolQuery.Builder.addTimestampFilter(timeFrom: ZonedDateTime?, timeTo: ZonedDateTime?): BoolQuery.Builder {
        if (timeFrom != null || timeTo != null) {
            val timestampQuery = QueryBuilders.range().field(FIELD_TIMESTAMP)
            if (timeFrom != null)
                timestampQuery.from(timeFrom.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME))
            if (timeTo != null)
                timestampQuery.to(timeTo.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME))
            filter(Query.Builder().range(timestampQuery.build()).build())
        }
        return this
    }

    private fun BoolQuery.Builder.addEmailFilter(email: AuditFilter<String>): BoolQuery.Builder {
        if (email.values.isNotEmpty()) {
            val userEmailsQueryBuilder = QueryBuilders.terms()
                .field("$FIELD_USER.$FIELD_USER_EMAIL")
                .terms(
                    TermsQueryField.Builder()
                        .value(email.values.map { FieldValue.Builder().stringValue(it).build() })
                        .build()
                )
            val userEmailsQuery = Query.Builder().terms(userEmailsQueryBuilder.build()).build()
            if (email.isInverted)
                mustNot(userEmailsQuery)
            else
                filter(userEmailsQuery)
        }
        return this
    }

    private fun BoolQuery.Builder.addFieldFilter(field: String, filter: AuditFilter<*>): BoolQuery.Builder {
        if (filter.values.isNotEmpty()) {
            val auditFilterTermsQueryBuilder = QueryBuilders.terms().field(field)
                .terms(
                    TermsQueryField.Builder().value(
                        filter.values.map {
                            return@map when (it) {
                                is Long -> FieldValue.Builder().longValue(it).build()
                                else -> FieldValue.Builder().stringValue(it.toString()).build()
                            } }
                    ).build()
                )

            val query = Query.Builder().terms(auditFilterTermsQueryBuilder.build()).build()
            if (filter.isInverted)
                mustNot(query)
            else
                filter(query)
        }
        return this
    }

    private fun BoolQuery.isEmpty() = must().isEmpty() && mustNot().isEmpty() && filter().isEmpty() && should().isEmpty()

}
