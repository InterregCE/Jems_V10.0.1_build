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
import java.time.ZoneId
import java.time.ZonedDateTime

internal class ProjectLumpSumControllerTest : UnitTest() {

    companion object {
        private val paymentEnabledDate = ZonedDateTime.of(2022, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC").normalized())
        private const val version = "v2.0"

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
        private val lumpSum3 = ProjectLumpSum(
            programmeLumpSumId = 3,
            period = 4,
            readyForPayment = true,
            lastApprovedVersionBeforeReadyForPayment = version,
            paymentEnabledDate = paymentEnabledDate
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
        every { getLumpSumInteractor.getLumpSums(1L) } returns listOf(lumpSum1, lumpSum2, lumpSum3)

        assertThat(controller.getProjectLumpSums(1L)).containsExactly(
            ProjectLumpSumDTO(
                programmeLumpSumId = 1,
                period = 3,
                lumpSumContributions = listOf(
                    ProjectPartnerLumpSumDTO(partnerId = 13, amount = BigDecimal.TEN),
                    ProjectPartnerLumpSumDTO(partnerId = 14, amount = BigDecimal.ONE),
                ),
                readyForPayment = false,
                comment = null,
                fastTrack = false
            ),
            ProjectLumpSumDTO(
                programmeLumpSumId = 2,
                period = 4,
                readyForPayment = false,
                comment = null,
                fastTrack = false
            ),
            ProjectLumpSumDTO(
                programmeLumpSumId = 3,
                period = 4,
                readyForPayment = true,
                comment = null,
                fastTrack = false,
                paymentEnabledDate = paymentEnabledDate,
                lastApprovedVersionBeforeReadyForPayment = version
            ),
        )
    }

    @Test
    fun getTotalSumOfLumpSumsPerPartner() {
        every { getLumpSumInteractor.getLumpSumsTotalForPartner(8L, "2.0") } returns BigDecimal.ONE
        assertThat(controller.getProjectLumpSumsTotalForPartner(1L, 8L, "2.0")).isEqualByComparingTo(BigDecimal.ONE)
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
                ProjectPartnerLumpSumDTO(partnerId = 23, amount = BigDecimal.valueOf(-1)),
                ProjectPartnerLumpSumDTO(partnerId = 24, amount = BigDecimal.valueOf(-1, 2)),
                ProjectPartnerLumpSumDTO(partnerId = 24, amount = BigDecimal.valueOf(-1, 1)),
            ),
            readyForPayment = false,
            comment = null,
            fastTrack = false,
        )
        val lumpSumDto2 = ProjectLumpSumDTO(
            programmeLumpSumId = 6,
            period = 8,
            lumpSumContributions = listOf(
                ProjectPartnerLumpSumDTO(partnerId = 23, amount = BigDecimal.ONE),
                ProjectPartnerLumpSumDTO(partnerId = 24, amount = BigDecimal.TEN),
            ),
            readyForPayment = false,
            comment = null,
            fastTrack = false,
        )
        val lumpSumDto3 = ProjectLumpSumDTO(
            programmeLumpSumId = 7,
            period = 2,
            lumpSumContributions = listOf(
                ProjectPartnerLumpSumDTO(partnerId = 23, amount = BigDecimal.ONE),
                ProjectPartnerLumpSumDTO(partnerId = 24, amount = BigDecimal.TEN),
            ),
            readyForPayment = true,
            comment = null,
            fastTrack = false,
            paymentEnabledDate = paymentEnabledDate,
            lastApprovedVersionBeforeReadyForPayment = version
        )

        controller.updateProjectLumpSums(2L, listOf(lumpSumDto1, lumpSumDto2, lumpSumDto3))

        assertThat(lumpSumsSlot.captured).containsExactly(
            ProjectLumpSum(
                programmeLumpSumId = 5,
                period = 7,
                lumpSumContributions = emptyList(), // negative contributions are not allowed
            ),
            ProjectLumpSum(
                programmeLumpSumId = 6,
                period = 8,
                lumpSumContributions = listOf(
                    ProjectPartnerLumpSum(partnerId = 23, amount = BigDecimal.ONE),
                    ProjectPartnerLumpSum(partnerId = 24, amount = BigDecimal.TEN),
                ),
            ),
            ProjectLumpSum(
                programmeLumpSumId = 7,
                period = 2,
                lumpSumContributions = listOf(
                    ProjectPartnerLumpSum(partnerId = 23, amount = BigDecimal.ONE),
                    ProjectPartnerLumpSum(partnerId = 24, amount = BigDecimal.TEN),
                ),
                paymentEnabledDate = paymentEnabledDate,
                lastApprovedVersionBeforeReadyForPayment = version,
                readyForPayment = true
            ),
        )
    }

}
