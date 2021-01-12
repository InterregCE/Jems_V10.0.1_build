package io.cloudflight.jems.server.project.service.lumpsum.get_project_lump_sums

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.lumpsum.ProjectLumpSumPersistence
import io.cloudflight.jems.server.project.service.lumpsum.model.ProjectLumpSum
import io.cloudflight.jems.server.project.service.lumpsum.model.ProjectPartnerLumpSum
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.util.UUID

internal class GetProjectLumpSumsTest : UnitTest() {

    companion object {
        private val id: UUID = UUID.randomUUID()
        private val lumpSum = ProjectLumpSum(
            id = id,
            programmeLumpSumId = 51,
            period = 1,
            lumpSumContributions = listOf(
                ProjectPartnerLumpSum(partnerId = 14, amount = BigDecimal.TEN),
                ProjectPartnerLumpSum(partnerId = 15, amount = BigDecimal.ONE),
            ),
        )
    }

    @MockK
    lateinit var persistence: ProjectLumpSumPersistence

    @InjectMockKs
    lateinit var getProjectLumpSums: GetProjectLumpSums

    @Test
    fun getActivitiesForWorkPackage() {
        every { persistence.getLumpSums(1L) } returns listOf(lumpSum)
        assertThat(getProjectLumpSums.getLumpSums(1L)).containsExactly(lumpSum)
    }

}
