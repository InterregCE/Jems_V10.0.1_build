package io.cloudflight.jems.server.project.controller.contracting.partner

import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerRoleDTO
import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerSummaryDTO
import io.cloudflight.jems.server.UnitTest
import io.cloudflight.jems.server.project.service.contracting.partner.getPartners.GetContractingPartnersInteractor
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerSummary
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.data.domain.Pageable

internal class ContractingPartnersControllerTest: UnitTest() {

    companion object {
        val partnerSummary = ProjectPartnerSummary(
            id = 1L,
            institutionName = "Institution name",
            abbreviation = "A",
            role = ProjectPartnerRole.LEAD_PARTNER,
            active = true,
            sortNumber = 1
        )
        val partnerSummaryDTO = ProjectPartnerSummaryDTO(
            id = 1L,
            institutionName = "Institution name",
            abbreviation = "A",
            role = ProjectPartnerRoleDTO.LEAD_PARTNER,
            active = true,
            sortNumber = 1
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
