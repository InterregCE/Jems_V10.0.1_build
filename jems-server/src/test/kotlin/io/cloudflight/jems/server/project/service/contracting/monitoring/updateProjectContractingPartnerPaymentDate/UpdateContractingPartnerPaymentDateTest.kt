package io.cloudflight.jems.server.project.service.contracting.monitoring.updateProjectContractingPartnerPaymentDate

import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.ProjectVersionPersistence
import io.cloudflight.jems.server.project.service.contracting.model.lastPaymentDate.ContractingClosure
import io.cloudflight.jems.server.project.service.contracting.model.lastPaymentDate.ContractingClosureLastPaymentDate
import io.cloudflight.jems.server.project.service.contracting.model.lastPaymentDate.ContractingClosureLastPaymentDateUpdate
import io.cloudflight.jems.server.project.service.contracting.model.lastPaymentDate.ContractingClosureUpdate
import io.cloudflight.jems.server.project.service.contracting.monitoring.ContractingMonitoringPersistence
import io.cloudflight.jems.server.project.service.partner.PartnerPersistence
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerSummary
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDate

class UpdateContractingPartnerPaymentDateTest : UnitTest() {

    companion object {
        private const val projectId = 7L
        private const val version = "2.0"

        private fun partner(id: Long, nr: Int, abbr: String) = ProjectPartnerSummary(
            id = id,
            abbreviation = abbr,
            institutionName = null,
            active = true,
            role = ProjectPartnerRole.PARTNER,
            sortNumber = nr,
            country = null,
            region = null,
            currencyCode = null,
        )
    }

    @MockK private lateinit var partnerPersistence: PartnerPersistence
    @MockK private lateinit var contractingMonitoringPersistence: ContractingMonitoringPersistence
    @MockK private lateinit var versionPersistence: ProjectVersionPersistence

    @InjectMockKs
    private lateinit var service: UpdateContractingPartnerPaymentDate

    @BeforeEach
    fun setup() {
        clearMocks(partnerPersistence, contractingMonitoringPersistence, versionPersistence)
    }

    @Test
    fun updatePartnerPaymentDate() {
        every { versionPersistence.getLatestApprovedOrCurrent(projectId) } returns version
        every { partnerPersistence.findAllByProjectIdForDropdown(projectId, any(), version) } returns listOf(
            partner(id = 26L, nr = 4, abbr = "26-partn-4"),
            partner(id = 27L, nr = 5, abbr = "27-partn-5"),
        )

        val newDate = LocalDate.of(2025, 4, 18)
        every { contractingMonitoringPersistence.updateClosureDate(projectId, newDate) } returnsArgument 1
        val slotNewDates = slot<Map<Long, LocalDate?>>()
        every { contractingMonitoringPersistence.updatePartnerPaymentDate(projectId, capture(slotNewDates)) } returns mapOf(
            26L to LocalDate.of(2024, 1, 26),
        )

        val toUpdate = ContractingClosureUpdate(
            closureDate = newDate,
            lastPaymentDates = listOf(
                ContractingClosureLastPaymentDateUpdate(25L, LocalDate.of(1888, 12, 20)),
                ContractingClosureLastPaymentDateUpdate(26L, LocalDate.of(2024, 1, 26)),
            ),
        )
        assertThat(service.updatePartnerPaymentDate(projectId, toUpdate)).isEqualTo(
            ContractingClosure(
                closureDate = newDate,
                lastPaymentDates = listOf(
                    ContractingClosureLastPaymentDate(26L, 4, "26-partn-4",
                        ProjectPartnerRole.PARTNER, false, LocalDate.of(2024, 1, 26)),
                    ContractingClosureLastPaymentDate(27L, 5, "27-partn-5",
                        ProjectPartnerRole.PARTNER, false, null),
                )
            )
        )
    }

}
