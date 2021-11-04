package io.cloudflight.jems.server.audit.repository

import io.cloudflight.jems.server.audit.model.Audit
import io.cloudflight.jems.server.audit.model.AuditFilter
import io.cloudflight.jems.server.audit.model.AuditSearchRequest
import io.cloudflight.jems.server.audit.service.AuditPersistence
import org.elasticsearch.action.index.IndexRequest
import org.elasticsearch.action.search.SearchRequest
import org.elasticsearch.client.RequestOptions
import org.elasticsearch.client.RestHighLevelClient
import org.elasticsearch.index.query.BoolQueryBuilder
import org.elasticsearch.index.query.TermsQueryBuilder
import org.elasticsearch.search.builder.SearchSourceBuilder
import org.elasticsearch.search.sort.FieldSortBuilder
import org.elasticsearch.search.sort.SortOrder
import org.springframework.data.domain.Page
import org.springframework.stereotype.Repository
import java.util.stream.Stream
import org.elasticsearch.index.query.RangeQueryBuilder
import org.springframework.data.domain.Sort

@Repository
class AuditPersistenceProvider(
    private val client: RestHighLevelClient,
) : AuditPersistence {

    companion object {
        const val AUDIT_INDEX = "audit-log"
    }

    override fun saveAudit(audit: Audit): String {
        val request = IndexRequest(AUDIT_INDEX).source(audit.toElasticsearchEntity())
        val response = client.index(request, RequestOptions.DEFAULT)
        return response.id
    }

    override fun getAudit(searchRequest: AuditSearchRequest): Page<Audit> =
        client.search(
            searchRequest.getQuery(AUDIT_INDEX, searchRequest.pageable.sort),
            RequestOptions.DEFAULT,
        ).hits.toModel(searchRequest.pageable)

    private fun AuditSearchRequest.getQuery(index: String, sort: Sort): SearchRequest {
        val filterQuery = BoolQueryBuilder()

        val timestampQuery = RangeQueryBuilder(FIELD_TIMESTAMP)
        if (timeFrom != null)
            timestampQuery.from(timeFrom)
        if (timeTo != null)
            timestampQuery.to(timeTo)

        val order = if (sort.getOrderFor("timestamp")?.direction?.isAscending == true) SortOrder.ASC else SortOrder.DESC

        Stream.of<Pair<String, AuditFilter<*>>>(
            Pair("$FIELD_USER.$FIELD_USER_ID", userId),
            Pair("$FIELD_USER.$FIELD_USER_EMAIL", userEmail),
            Pair(FIELD_ACTION, action),
            Pair("$FIELD_PROJECT.$FIELD_PROJECT_ID", projectId),
        )
            .filter { it.second.values.isNotEmpty() }
            .forEach {
                if (it.second.isInverted)
                    filterQuery.mustNot(TermsQueryBuilder(it.first, it.second.values))
                else
                    filterQuery.must(TermsQueryBuilder(it.first, it.second.values))
            }

        filterQuery.must(timestampQuery)

        return SearchRequest(index)
            .source(
                SearchSourceBuilder()
                    .query(filterQuery)
                    .from(pageable.offset.toInt())
                    .size(pageable.pageSize)
                    .sort(FieldSortBuilder(FIELD_TIMESTAMP).order(order))
            )
    }

}
