package io.cloudflight.jems.server.project.repository.report.project.financialOverview.lumpSums

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.entity.report.project.financialOverview.ReportProjectCertificateLumpSumEntity
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class ProjectReportCertificateLumpSumPersistenceProviderTest : UnitTest() {

    companion object {
        private fun lumpSum(id: Long, orderNr: Int, currentVerified: BigDecimal) = ReportProjectCertificateLumpSumEntity(
            id = id,
            reportEntity = mockk(),
            programmeLumpSum = mockk(),
            orderNr = orderNr,
            periodNumber = 7,
            total = BigDecimal.valueOf(12L),
            current = BigDecimal.valueOf(13L),
            previouslyReported = BigDecimal.valueOf(14L),
            previouslyVerified = BigDecimal.valueOf(15L),
            currentVerified = currentVerified,
            previouslyPaid = BigDecimal.valueOf(17L),
        )
    }

    @MockK
    private lateinit var reportLumpSumRepository: ReportProjectCertificateLumpSumRepository

    @InjectMockKs
    private lateinit var persistence: ProjectReportCertificateLumpSumPersistenceProvider

    @BeforeEach
    fun reset() {
        clearMocks(reportLumpSumRepository)
    }

    @Test
    fun updateCurrentlyVerifiedValues() {
        val projectId = 450L
        val reportId = 95L

        val lumpSum_1 = lumpSum(41L, 3, currentVerified = BigDecimal.valueOf(450L))
        val lumpSum_2 = lumpSum(42L, 4, currentVerified = BigDecimal.valueOf(470L))
        every { reportLumpSumRepository
            .findByReportEntityProjectIdAndReportEntityIdOrderByOrderNrAscIdAsc(projectId = projectId, reportId = reportId)
        } returns listOf(lumpSum_1, lumpSum_2)

        persistence.updateCurrentlyVerifiedValues(projectId, reportId, mapOf(
            4 to BigDecimal.valueOf(380L),
            5 to BigDecimal.valueOf(400L),
        ))
        assertThat(lumpSum_1.currentVerified).isEqualTo(BigDecimal.valueOf(450L))
        assertThat(lumpSum_2.currentVerified).isEqualTo(BigDecimal.valueOf(380L))
    }

}
