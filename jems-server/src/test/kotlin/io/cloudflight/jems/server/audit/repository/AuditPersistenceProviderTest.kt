package io.cloudflight.jems.server.audit.repository

import io.cloudflight.jems.server.audit.model.Audit
import io.cloudflight.jems.server.audit.model.AuditProject
import io.cloudflight.jems.server.audit.model.AuditSearchRequest
import io.cloudflight.jems.server.audit.model.AuditUser
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.apache.lucene.search.TotalHits
import org.assertj.core.api.Assertions.assertThat
import org.elasticsearch.action.search.SearchResponse
import org.elasticsearch.action.search.SearchResponseSections
import org.elasticsearch.client.RestHighLevelClient
import org.elasticsearch.common.bytes.BytesArray
import org.elasticsearch.search.SearchHit
import org.elasticsearch.search.SearchHits
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@ExtendWith(MockKExtension::class)
internal class AuditPersistenceProviderTest {

    companion object {
        private const val timeOfEntry = "2021-06-20T15:43:20.762Z"
        private const val userEmail = "test@test"
        private const val sourceEntry =
            "{\"timestamp\":\"$timeOfEntry\",\"project\": {\"id\": \"id1\", \"name\": \"name1\"},\"user\": {\"id\": 2, \"email\": \"$userEmail\"}}"
    }

    @MockK
    private lateinit var client: RestHighLevelClient

    @InjectMockKs
    private lateinit var persistence: AuditPersistenceProvider

    @Test
    fun `getCalls - without parameters`() {
        val searchRequest = AuditSearchRequest()
        val searchHit = SearchHit(1)
        searchHit.sourceRef(BytesArray(sourceEntry))
        val searchResponse = SearchResponse(
            SearchResponseSections(
                SearchHits(arrayOf(searchHit), TotalHits(1, TotalHits.Relation.EQUAL_TO), 2f),
                null,
                null,
                false,
                false,
                null,
                1),
            "scrollId",
            1,
            1,
            0,
            10,
            null,
            null
        )
        every { client.search(any(), any()) } returns searchResponse

        val result = persistence.getAudit(searchRequest)
        assertThat(result).containsExactly(
            Audit(
                id = null,
                timestamp = ZonedDateTime.parse(timeOfEntry, DateTimeFormatter.ISO_DATE_TIME),
                action = null,
                user = AuditUser(id = 2, email = userEmail),
                project = AuditProject(id = "id1", name = "name1"),
                entityRelatedId = null,
                description = null
            )
        )
    }
}
