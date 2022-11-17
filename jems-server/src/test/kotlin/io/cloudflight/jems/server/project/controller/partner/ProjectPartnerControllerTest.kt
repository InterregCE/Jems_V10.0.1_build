package io.cloudflight.jems.server.project.controller.partner

import io.cloudflight.jems.api.programme.dto.fund.ProgrammeFundDTO
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage
import io.cloudflight.jems.api.programme.dto.language.SystemLanguage.EN
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.api.project.dto.ProjectPartnerStateAidDTO
import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerPaymentSummaryDTO
import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerRoleDTO
import io.cloudflight.jems.api.project.dto.partner.ProjectPartnerSummaryDTO
import io.cloudflight.jems.api.project.dto.partner.cofinancing.ProjectPartnerContributionDTO
import io.cloudflight.jems.api.project.dto.partner.cofinancing.ProjectPartnerContributionStatusDTO
import io.cloudflight.jems.server.programme.service.fund.model.ProgrammeFund
import io.cloudflight.jems.server.project.service.partner.cofinancing.model.ProjectPartnerContribution
import io.cloudflight.jems.server.project.service.partner.create_project_partner.CreateProjectPartnerInteractor
import io.cloudflight.jems.server.project.service.partner.deactivate_project_partner.DeactivateProjectPartnerInteractor
import io.cloudflight.jems.server.project.service.partner.delete_project_partner.DeleteProjectPartnerInteractor
import io.cloudflight.jems.server.project.service.partner.get_project_partner.GetProjectPartnerInteractor
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerPaymentSummary
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerRole
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerStateAid
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerSummary
import io.cloudflight.jems.server.project.service.partner.state_aid.get_project_partner_state_aid.GetProjectPartnerStateAidInteractor
import io.cloudflight.jems.server.project.service.partner.state_aid.update_project_partner_state_aid.UpdateProjectPartnerStateAidInteractor
import io.cloudflight.jems.server.project.service.partner.update_project_partner.UpdateProjectPartnerInteractor
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.math.BigDecimal

@ExtendWith(MockKExtension::class)
class ProjectPartnerControllerTest {

    companion object {
        private const val PARTNER_ID = 478L

        private val stateAid = ProjectPartnerStateAid(
            answer1 = true,
            justification1 = setOf(InputTranslation(EN, "true")),
            answer2 = false,
            justification2 = setOf(InputTranslation(EN, "false")),
            answer3 = null,
            justification3 = setOf(InputTranslation(EN, "null")),
            answer4 = null,
            justification4 = emptySet(),
            stateAidScheme = null
        )

        private val stateAidDto = ProjectPartnerStateAidDTO(
            answer1 = true,
            justification1 = setOf(InputTranslation(EN, "true")),
            answer2 = false,
            justification2 = setOf(InputTranslation(EN, "false")),
            answer3 = null,
            justification3 = setOf(InputTranslation(EN, "null")),
            answer4 = null,
            justification4 = emptySet(),
        )

        private val projectPartnerPaymentSummary = ProjectPartnerPaymentSummary(
            partnerSummary = ProjectPartnerSummary(
                id = 1L,
                abbreviation = "A",
                role = ProjectPartnerRole.PARTNER,
                active = true,
                sortNumber = 1
            ),
            partnerCoFinancing = listOf(
                ProgrammeFund(
                    id = 2L,
                    selected = true,
                    abbreviation = setOf(
                        InputTranslation(language = EN, translation = "ERDF"),
                        InputTranslation(language = SystemLanguage.DE, translation = "ERDF DE")
                    )
                )
            ),
            partnerContributions = listOf(
                ProjectPartnerContribution(
                    id = 1,
                    name = "contribution 1 test",
                    status = ProjectPartnerContributionStatusDTO.Public,
                    isPartner = true,
                    amount = BigDecimal(100)
                )
            )
        )
    }

    @MockK
    lateinit var getProjectPartnerInteractor: GetProjectPartnerInteractor

    @MockK
    lateinit var createProjectPartnerInteractor: CreateProjectPartnerInteractor

    @MockK
    lateinit var updateProjectPartnerInteractor: UpdateProjectPartnerInteractor

    @MockK
    lateinit var getProjectPartnerStateAidInteractor: GetProjectPartnerStateAidInteractor

    @MockK
    lateinit var updateProjectPartnerStateAidInteractor: UpdateProjectPartnerStateAidInteractor

    @MockK
    lateinit var deleteProjectPartnerInteractor: DeleteProjectPartnerInteractor

    @MockK
    lateinit var deactivateProjectPartner: DeactivateProjectPartnerInteractor

    @InjectMockKs
    private lateinit var controller: ProjectPartnerController

    @Test
    fun getProjectPartnerStateAid() {
        every { getProjectPartnerStateAidInteractor.getStateAidForPartnerId(PARTNER_ID) } returns stateAid
        assertThat(controller.getProjectPartnerStateAid(PARTNER_ID)).isEqualTo(stateAidDto)
    }

    @Test
    fun updateProjectPartnerStateAid() {
        every { updateProjectPartnerStateAidInteractor.updatePartnerStateAid(PARTNER_ID, any()) } returnsArgument 1
        assertThat(controller.updateProjectPartnerStateAid(PARTNER_ID, stateAidDto)).isEqualTo(stateAidDto)
    }

    @Test
    fun getProjectPartnersAndContributions() {
        val projectPartnerPaymentSummaryDto = ProjectPartnerPaymentSummaryDTO(
            partnerSummary = ProjectPartnerSummaryDTO(
                id = 1L,
                institutionName = null,
                abbreviation = "A",
                role = ProjectPartnerRoleDTO.PARTNER,
                active = true,
                sortNumber = 1
            ),
            partnerCoFinancing = listOf(
                ProgrammeFundDTO(
                    id = 2L,
                    selected = true,
                    abbreviation = setOf(
                        InputTranslation(language = EN, translation = "ERDF"),
                        InputTranslation(language = SystemLanguage.DE, translation = "ERDF DE")
                    )
                )
            ),
            partnerContributions = listOf(
                ProjectPartnerContributionDTO(
                    id = 1,
                    name = "contribution 1 test",
                    status = ProjectPartnerContributionStatusDTO.Public,
                    partner = true,
                    amount = BigDecimal(100)
                )
            )
        )
        every { getProjectPartnerInteractor.findAllByProjectIdWithContributionsForDropdown(1) } returns listOf(projectPartnerPaymentSummary)
        assertThat(controller.getProjectPartnersAndContributions(1)).containsExactly(projectPartnerPaymentSummaryDto)
    }
}
