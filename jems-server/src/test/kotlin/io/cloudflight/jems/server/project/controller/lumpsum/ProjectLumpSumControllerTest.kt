package io.cloudflight.jems.server.project.controller.lumpsum

import io.cloudflight.jems.api.project.dto.lumpsum.ProjectLumpSumDTO
import io.cloudflight.jems.api.project.dto.lumpsum.ProjectPartnerLumpSumDTO
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.lumpsum.get_project_lump_sums.GetProjectLumpSumsInteractor
import io.cloudflight.jems.server.project.service.lumpsum.model.ProjectLumpSum
import io.cloudflight.jems.server.project.service.lumpsum.model.ProjectPartnerLumpSum
import io.cloudflight.jems.server.project.service.lumpsum.update_project_lump_sums.UpdateProjectLumpSumsInteractor
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.util.UUID

internal class ProjectLumpSumControllerTest : UnitTest() {

    companion object {
        private val id1: UUID = UUID.randomUUID()
        private val id2: UUID = UUID.randomUUID()
        private val lumpSum1 = ProjectLumpSum(
            programmeLumpSumId = 1,
            period = 3,
            lumpSumContributions = listOf(
                ProjectPartnerLumpSum(partnerId = 13, amount = BigDecimal.TEN),
                ProjectPartnerLumpSum(partnerId = 14, amount = BigDecimal.ONE),
            )
        )
        private val lumpSum2 = ProjectLumpSum(
            programmeLumpSumId = 2,
            period = 4,
        )
    }

    @MockK
    lateinit var getLumpSumInteractor: GetProjectLumpSumsInteractor

    @MockK
    lateinit var updateLumpSumInteractor: UpdateProjectLumpSumsInteractor

    @InjectMockKs
    private lateinit var controller: ProjectLumpSumController

    @Test
    fun getLumpSums() {
        every { getLumpSumInteractor.getLumpSums(1L) } returns listOf(lumpSum1, lumpSum2)

        assertThat(controller.getProjectLumpSums(1L)).containsExactly(
            ProjectLumpSumDTO(
                programmeLumpSumId = 1,
                period = 3,
                lumpSumContributions = listOf(
                    ProjectPartnerLumpSumDTO(partnerId = 13, amount = BigDecimal.TEN),
                    ProjectPartnerLumpSumDTO(partnerId = 14, amount = BigDecimal.ONE),
                )
            ),
            ProjectLumpSumDTO(
                programmeLumpSumId = 2,
                period = 4,
            ),
        )
    }

    @Test
    fun updateLumpSums() {
        val lumpSumsSlot = slot<List<ProjectLumpSum>>()
        // we test retrieval in getLumpSums test
        every { updateLumpSumInteractor.updateLumpSums(2L, capture(lumpSumsSlot)) } returns emptyList()

        val lumpSumDto1 = ProjectLumpSumDTO(
            programmeLumpSumId = 5,
            period = 7,
            lumpSumContributions = listOf(
                ProjectPartnerLumpSumDTO(partnerId = 23, amount = BigDecimal.ZERO),
                ProjectPartnerLumpSumDTO(partnerId = 24, amount = BigDecimal.valueOf(0, 2)),
                ProjectPartnerLumpSumDTO(partnerId = 24, amount = BigDecimal.valueOf(0, 1)),
            )
        )
        val lumpSumDto2 = ProjectLumpSumDTO(
            programmeLumpSumId = 6,
            period = 8,
            lumpSumContributions = listOf(
                ProjectPartnerLumpSumDTO(partnerId = 23, amount = BigDecimal.ONE),
                ProjectPartnerLumpSumDTO(partnerId = 24, amount = BigDecimal.TEN),
            ),
        )

        controller.updateProjectLumpSums(2L, listOf(lumpSumDto1, lumpSumDto2))

        assertThat(lumpSumsSlot.captured).containsExactly(
            ProjectLumpSum(
                programmeLumpSumId = 5,
                period = 7,
                lumpSumContributions = emptyList(), // zero contributions are ignored
            ),
            ProjectLumpSum(
                programmeLumpSumId = 6,
                period = 8,
                lumpSumContributions = listOf(
                    ProjectPartnerLumpSum(partnerId = 23, amount = BigDecimal.ONE),
                    ProjectPartnerLumpSum(partnerId = 24, amount = BigDecimal.TEN),
                ),
            ),
        )
    }

}
