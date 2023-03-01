package io.cloudflight.jems.server.project.controller.contracting.partner

import io.cloudflight.jems.api.project.dto.contracting.partner.ContractingPartnerSummaryDTO
import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerRoleDTO
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.contracting.model.partner.getPartners.ContractingPartnerSummary
import io.cloudflight.jems.server.project.service.contracting.partner.getPartners.GetContractingPartnersInteractor
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.data.domain.Pageable

internal class ContractingPartnersControllerTest: UnitTest() {

    companion object {
        val partnerSummary = ContractingPartnerSummary(
            id = 1L,
            institutionName = "Institution name",
            abbreviation = "A",
            role = ProjectPartnerRole.LEAD_PARTNER,
            active = true,
            sortNumber = 1,
            locked = false
        )
        val partnerSummaryDTO = ContractingPartnerSummaryDTO(
            id = 1L,
            institutionName = "Institution name",
            abbreviation = "A",
            role = ProjectPartnerRoleDTO.LEAD_PARTNER,
            active = true,
            sortNumber = 1,
            locked = false
        )
    }

    @MockK
    lateinit var getContractingPartnersInteractor: GetContractingPartnersInteractor

    @InjectMockKs
    private lateinit var controller: ContractingPartnersController


    @Test
    fun getProjectPartnersForContracting() {
        every { getContractingPartnersInteractor.findAllByProjectIdForContracting(1, any()) } returns listOf(partnerSummary)
        assertThat(controller.getProjectPartnersForContracting(1, Pageable.unpaged())).containsExactly(partnerSummaryDTO)
    }
}
