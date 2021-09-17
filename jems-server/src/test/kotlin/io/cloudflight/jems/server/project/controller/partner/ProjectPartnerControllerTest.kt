package io.cloudflight.jems.server.project.controller.partner

import io.cloudflight.jems.api.programme.dto.language.SystemLanguage.EN
import io.cloudflight.jems.api.project.dto.InputTranslation
import io.cloudflight.jems.api.project.dto.ProjectPartnerStateAidDTO
import io.cloudflight.jems.server.project.service.partner.create_project_partner.CreateProjectPartnerInteractor
import io.cloudflight.jems.server.project.service.partner.delete_project_partner.DeleteProjectPartnerInteractor
import io.cloudflight.jems.server.project.service.partner.get_project_partner.GetProjectPartnerInteractor
import io.cloudflight.jems.server.project.service.partner.model.ProjectPartnerStateAid
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

}
