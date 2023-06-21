package io.cloudflight.jems.server.audit.repository

import co.elastic.clients.elasticsearch.ElasticsearchClient
import co.elastic.clients.elasticsearch._types.SortOrder
import co.elastic.clients.elasticsearch.core.IndexRequest
import co.elastic.clients.elasticsearch.core.IndexResponse
import co.elastic.clients.elasticsearch.core.SearchRequest
import co.elastic.clients.elasticsearch.core.SearchResponse
import co.elastic.clients.elasticsearch.core.search.Hit
import co.elastic.clients.elasticsearch.core.search.HitsMetadata
import co.elastic.clients.elasticsearch.core.search.TotalHits
import co.elastic.clients.elasticsearch.core.search.TotalHitsRelation
import co.elastic.clients.util.ObjectBuilder
import io.cloudflight.jems.api.audit.dto.AuditAction
import io.cloudflight.jems.server.audit.model.Audit
import io.cloudflight.jems.server.audit.model.AuditFilter
import io.cloudflight.jems.server.audit.model.AuditProject
import io.cloudflight.jems.server.audit.model.AuditSearchRequest
import io.cloudflight.jems.server.audit.model.AuditUser
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.function.Function

@ExtendWith(MockKExtension::class)
internal class AuditPersistenceProviderTest {

    companion object {
        private val timeOfEntry = ZonedDateTime.now()

        private val sampleAudit = Audit(
            id = null,
            timestamp = timeOfEntry,
            action = AuditAction.PROGRAMME_INDICATOR_ADDED,
            user = AuditUser(id = 2, email = "userEmail"),
            project = AuditProject(id = "id1", customIdentifier = "custom1", name = "name1"),
            entityRelatedId = 45L,
            description = "desc",
        )
    }

    @MockK
    private lateinit var client: ElasticsearchClient

    @InjectMockKs
    private lateinit var persistence: AuditPersistenceProvider

    @BeforeEach
    fun resetMocks() {
        clearMocks(client)
    }

    @Test
    fun saveAudit() {
        val request = slot<Function<IndexRequest.Builder<Audit>, ObjectBuilder<IndexRequest<Audit>>>>()
        val result = mockk<IndexResponse>()
        every { result.id() } returns "custom-es-id"
        every { client.index(capture(request)) } returns result

        val toSave = Audit(
            id = "id",
            timestamp = ZonedDateTime.now(ZoneOffset.UTC),
            action = AuditAction.APPLICATION_STATUS_CHANGED,
            user = AuditUser(1L, "email"),
            project = AuditProject("2", "customIdentifier", "name"),
            entityRelatedId = 3L,
            description = "description"
        )

        assertThat(persistence.saveAudit(toSave)).isEqualTo("custom-es-id")
        with(request.captured.apply(IndexRequest.Builder()).build()) {
            assertThat(index()).isEqualTo("audit-log-v3")
            assertThat(document()).isEqualTo(toSave)
        }
    }

    @Test
    fun `getCalls - without parameters`() {
        val searchRequest = AuditSearchRequest()
        val searchResponse = mockk<SearchResponse<Audit>>()
        mockResponse(searchResponse, sampleAudit.copy())
        val searchSlot = slot<SearchRequest>()
        every { client.search(capture(searchSlot), Audit::class.java) } returns searchResponse

        val result = persistence.getAudit(searchRequest)
        assertThat(result).containsExactly(sampleAudit.copy(id = "very-es-custom-id"))

        with(searchSlot.captured) {
            assertThat(query()).isNull()
            assertThat(index()).containsExactly("audit-log-v3")
            assertThat(sort()).hasSize(1)
            assertThat(sort().first().field().field()).isEqualTo("timestamp")
            assertThat(sort().first().field().order()).isEqualTo(SortOrder.Desc)
        }
    }

    @Test
    fun `getCalls - with parameters`() {
        val searchRequest = AuditSearchRequest(
            userId = AuditFilter(setOf(15L, 16L), isInverted = false),
            userEmail = AuditFilter(setOf("why@me?"), isInverted = true),
            action = AuditFilter(setOf("action 1", "action 2"), isInverted = false),
            projectId = AuditFilter(setOf("project5"), isInverted = false),
            timeFrom = timeOfEntry.minusDays(1),
            timeTo = timeOfEntry.plusDays(1),
        )
        val searchResponse = mockk<SearchResponse<Audit>>()
        mockResponse(searchResponse, sampleAudit.copy())
        val searchSlot = slot<SearchRequest>()
        every { client.search(capture(searchSlot), Audit::class.java) } returns searchResponse

        val result = persistence.getAudit(searchRequest)
        assertThat(result).containsExactly(sampleAudit.copy(id = "very-es-custom-id"))

        with(searchSlot.captured) {
            assertThat(query()!!.bool().filter()).hasSize(4)
            assertThat(query()!!.bool().filter()[0].range().field()).isEqualTo("timestamp")
            assertThat(query()!!.bool().filter()[0].range().from()).isEqualTo(
                timeOfEntry.minusDays(1).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME))
            assertThat(query()!!.bool().filter()[0].range().to()).isEqualTo(
                timeOfEntry.plusDays(1).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME))

            assertThat(query()!!.bool().filter()[1].terms().field()).isEqualTo("user.id")
            assertThat(query()!!.bool().filter()[1].terms().terms().value().map { it.longValue() }).containsExactly(15L, 16L)

            assertThat(query()!!.bool().filter()[2].terms().field()).isEqualTo("action")
            assertThat(query()!!.bool().filter()[2].terms().terms().value().map { it.stringValue() }).containsExactly("action 1", "action 2")

            assertThat(query()!!.bool().filter()[3].terms().field()).isEqualTo("project.id")
            assertThat(query()!!.bool().filter()[3].terms().terms().value().map { it.stringValue() }).containsExactly("project5")

            assertThat(query()!!.bool().mustNot()).hasSize(1)
            assertThat(query()!!.bool().mustNot()[0].terms().field()).isEqualTo("user.email")
            assertThat(query()!!.bool().mustNot()[0].terms().terms().value().map { it.stringValue() }).containsExactly("why@me?")

            assertThat(query()!!.bool().must()).isEmpty()
            assertThat(query()!!.bool().should()).isEmpty()

            assertThat(index()).containsExactly("audit-log-v3")
            assertThat(sort()).hasSize(1)
            assertThat(sort().first().field().field()).isEqualTo("timestamp")
            assertThat(sort().first().field().order()).isEqualTo(SortOrder.Desc)
        }
    }

    private fun mockResponse(searchResponseMock: SearchResponse<Audit>, response: Audit) {
        every { searchResponseMock.hits() } returns HitsMetadata.Builder<Audit>()
            .hits(
                listOf(Hit.Builder<Audit>()
                    .id("very-es-custom-id")
                    .index("audit-index")
                    .source(response).build()
                ))
            .total(
                TotalHits.Builder().value(457L).relation(TotalHitsRelation.Eq).build())
            .build()
    }

}
